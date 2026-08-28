package com.videoqa.challenge.robot

import android.graphics.Bitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.videoqa.challenge.util.assertBitmapMatchesAsset
import com.videoqa.challenge.util.assertMatchesAsset
import com.videoqa.challenge.util.waitForContentLoaded
import com.videoqa.challenge.util.waitForNode
import com.videoqa.challenge.util.waitWhilePresent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

enum class IconPlacement {
    TOP,     // Above the anchor (e.g. Warning icon above error message)
    BOTTOM,  // Below the anchor
    START,   // Leading edge of the anchor (e.g. Calendar icon before Published date)
    END,     // Trailing edge of the anchor
}

abstract class BaseRobot(protected val rule: ComposeTestRule) {

    fun waitForNode(tag: String, timeout: Duration = 10.seconds) =
        rule.waitForNode(tag, timeout)

    fun waitUntilGone(tag: String, timeout: Duration = 10.seconds) =
        rule.waitWhilePresent(tag, timeout)

    fun waitForText(tag: String, expectedText: String, timeout: Duration = 10.seconds) {
        rule.waitUntil(timeout.inWholeMilliseconds) {
            runCatching {
                rule.onNodeWithTag(tag).assertTextEquals(expectedText)
                true
            }.getOrDefault(false)
        }
    }

    /**
     * Verifies a Text / Label component is displayed on screen and matches the expected text.
     */
    protected fun verifyTextElement(tag: String, expectedText: String, useUnmergedTree: Boolean = true) {
        rule.onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
            .assertIsDisplayed()
            .assertTextEquals(expectedText)
    }

    /**
     * Verifies a Text component found directly by its text string is displayed on screen.
     */
    protected fun verifyTextDisplayed(text: String, useUnmergedTree: Boolean = true) {
        rule.onNode(hasText(text), useUnmergedTree = useUnmergedTree)
            .assertIsDisplayed()
    }

    /**
     * Verifies that at least one Text component matching [text] is displayed on screen.
     * Useful when the same text is present in both a Toolbar and Screen Body.
     */
    protected fun verifyAnyTextDisplayed(text: String, useUnmergedTree: Boolean = true) {
        rule.onAllNodes(hasText(text), useUnmergedTree = useUnmergedTree)
            .onFirst()
            .assertIsDisplayed()
    }

    /**
     * Verifies a Button component is displayed, clickable, and has the expected label text.
     */
    protected fun verifyButton(tag: String, expectedText: String) {
        rule.onNodeWithTag(tag)
            .assertIsDisplayed()
            .assertHasClickAction()
            .assertTextEquals(expectedText)
    }

    /**
     * Verifies an Icon Button component is displayed and clickable (e.g. Back button).
     */
    protected fun verifyIconButton(tag: String) {
        rule.onNodeWithTag(tag)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    /**
     * Verifies a component is displayed and has a click action.
     */
    protected fun verifyClickableElement(tag: String) {
        rule.onNodeWithTag(tag)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    /**
     * Verifies an Icon / Image component is displayed and matches the expected vector asset silhouette.
     */
    protected fun verifyIconElement(tag: String, assetPath: String, useUnmergedTree: Boolean = true) {
        rule.onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
            .assertIsDisplayed()
            .assertMatchesAsset(assetPath)
    }

    /**
     * Verifies an Image or Gradient component is displayed and matches the expected SVG / image asset.
     */
    protected fun verifyImageAsset(
        tag: String,
        assetPath: String,
        useUnmergedTree: Boolean = true,
        maxDiffRatio: Double = 0.20,
    ) {
        rule.onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
            .assertIsDisplayed()
            .assertMatchesAsset(assetPath, maxDiffRatio = maxDiffRatio)
    }

    /**
     * Verifies a Switch / Toggle component is displayed and is in the expected checked/unchecked state.
     */
    protected fun verifySwitchElement(tag: String, isChecked: Boolean) {
        val node = rule.onNodeWithTag(tag).assertIsDisplayed()
        if (isChecked) {
            node.assertIsOn()
        } else {
            node.assertIsOff()
        }
    }

    /**
     * Universally verifies any untagged icon positioned relative to an anchor node.
     */
    protected fun verifyRelativeIcon(
        matcher: SemanticsMatcher,
        assetPath: String,
        placement: IconPlacement = IconPlacement.START,
        iconSize: Dp = 24.dp,
        spacing: Dp = 0.dp,
        minShapeOverlap: Double = 0.60,
    ) {
        val node = rule.onNode(matcher).apply {
            runCatching { performScrollTo() }
        }.assertIsDisplayed()
        val semanticsNode = node.fetchSemanticsNode()
        val density = semanticsNode.layoutInfo.density
        val sizePx = with(density) { iconSize.roundToPx() }
        val spacingPx = with(density) { spacing.roundToPx() }
        val bounds = semanticsNode.boundsInWindow

        val (left, top) = when (placement) {
            IconPlacement.TOP -> Pair(
                (bounds.left + (bounds.width - sizePx) / 2).toInt(),
                (bounds.top - spacingPx - sizePx).toInt(),
            )
            IconPlacement.BOTTOM -> Pair(
                (bounds.left + (bounds.width - sizePx) / 2).toInt(),
                (bounds.bottom + spacingPx).toInt(),
            )
            IconPlacement.START -> Pair(
                bounds.left.toInt(),
                (bounds.top + (bounds.height - sizePx) / 2).toInt(),
            )
            IconPlacement.END -> Pair(
                (bounds.right - sizePx).toInt(),
                (bounds.top + (bounds.height - sizePx) / 2).toInt(),
            )
        }

        val screenshot = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            ?: throw IllegalStateException("UiAutomation.takeScreenshot returned null")
        val safeLeft = left.coerceIn(0, (screenshot.width - sizePx).coerceAtLeast(0))
        val safeTop = top.coerceIn(0, (screenshot.height - sizePx).coerceAtLeast(0))

        val iconBitmap = Bitmap.createBitmap(screenshot, safeLeft, safeTop, sizePx, sizePx)
        assertBitmapMatchesAsset(iconBitmap, assetPath, minShapeOverlap = minShapeOverlap)
    }

    protected fun verifyRelativeIcon(
        tag: String,
        assetPath: String,
        placement: IconPlacement = IconPlacement.START,
        iconSize: Dp = 24.dp,
        spacing: Dp = 0.dp,
        minShapeOverlap: Double = 0.60,
    ) = verifyRelativeIcon(hasTestTag(tag), assetPath, placement, iconSize, spacing, minShapeOverlap)

    // =========================================================================
    // Atomic Actions & Interactions
    // =========================================================================

    protected fun clickNode(tag: String) {
        rule.waitForNode(tag)
        rule.onNodeWithTag(tag)
            .assertIsDisplayed()
            .performClick()
    }

    protected fun toggleSwitch(tag: String) {
        clickNode(tag)
    }

    // =========================================================================
    // Atomic Visibility Assertions
    // =========================================================================

    protected fun verifyNodeDisplayed(tag: String, useUnmergedTree: Boolean = true) {
        rule.onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsDisplayed()
    }

    protected fun verifyNodeDoesNotExist(tag: String) {
        rule.onNodeWithTag(tag).assertDoesNotExist()
    }

    protected fun verifyNodesDoNotExist(vararg tags: String) {
        tags.forEach { verifyNodeDoesNotExist(it) }
    }

    /**
     * Natively verifies that [condition] remains continuously true for [duration].
     * If [condition] returns false at any point before [duration] has elapsed,
     * the assertion fails immediately.
     */
    protected fun verifyPersistsForDuration(
        duration: Duration,
        condition: () -> Boolean,
    ) {
        val startTime = System.currentTimeMillis()
        val durationMs = duration.inWholeMilliseconds

        rule.waitUntil(timeoutMillis = durationMs + 2000L) {
            if (!condition()) {
                val elapsed = (System.currentTimeMillis() - startTime).milliseconds
                throw AssertionError(
                    "Condition failed early after $elapsed (expected to persist for at least $duration)!"
                )
            }
            System.currentTimeMillis() - startTime >= durationMs
        }
    }

    protected fun isNodeDisplayed(tag: String): Boolean {
        return rule.onAllNodesWithTag(tag)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

    /**
     * Asserts that [tag] is currently present, and waits natively using Compose's
     * [waitUntilDoesNotExist] until it disappears, verifying that the elapsed time
     * falls within [expectedDuration] ± [tolerance].
     */
    @OptIn(ExperimentalTestApi::class)
    protected fun verifyPresenceDuration(
        tag: String,
        expectedDuration: Duration,
        tolerance: Duration = 1500.milliseconds,
    ) {
        val startTime = System.currentTimeMillis()
        val maxWaitMs = (expectedDuration + tolerance * 2).inWholeMilliseconds
        rule.waitUntilDoesNotExist(hasTestTag(tag), timeoutMillis = maxWaitMs)

        val elapsed = (System.currentTimeMillis() - startTime).milliseconds
        val minAllowed = (expectedDuration - tolerance).coerceAtLeast(Duration.ZERO)
        val maxAllowed = expectedDuration + tolerance

        if (elapsed !in minAllowed..maxAllowed) {
            throw AssertionError(
                "Element [$tag] was displayed for $elapsed, which is outside the expected range [$minAllowed .. $maxAllowed]"
            )
        }
    }

    // =========================================================================
    // Idling & Asynchronous Waiting
    // =========================================================================

    fun waitForContent(timeout: Duration = 10.seconds) = rule.waitForContentLoaded(timeout)
}
