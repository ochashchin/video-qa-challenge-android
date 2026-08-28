package com.videoqa.challenge.robot

import android.view.View
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.unit.dp
import androidx.media3.ui.PlayerView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage.RESUMED
import com.videoqa.challenge.data.VideoCardExpectedData
import com.videoqa.challenge.data.VideoDetailsDataProvider
import com.videoqa.challenge.viewmodel.PlayerViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class VideoDetailsRobot(rule: ComposeTestRule) : BaseRobot(rule) {

    // --- Locators ---
    private val detailScreen = "content_detail_screen"
    private val backButton = "detail_back_button"
    private val detailTitle = "detail_title"
    private val detailCategory = "detail_category"
    private val detailDescription = "detail_description"
    private val videoPlayer = "video_player"
    private val previewPlayButton = "video_play_button"
    private val bufferingIndicator = "video_buffering_indicator"
    private val errorMessage = "video_error_message"
    private val retryButton = "video_retry_button"
    private val pauseButton = "video_pause_button"
    private val playButton = "video_play_button"
    private val currentPosition = "video_current_position"
    private val duration = "video_duration"
    private val progress = "video_progress"
    private val stateLabel = "video_state_label"

    // --- Screen / Loading Idling ---

    fun waitForDataToLoad(timeout: Duration = 10.seconds) {
        waitForNode(detailScreen, timeout)
    }

    // --- Actions ---

    fun startPlayback() = clickNode(previewPlayButton)

    fun clickPause() = clickNode(pauseButton)

    fun clickPlay() = clickNode(playButton)

    fun clickTryAgain() = clickNode(retryButton)

    fun clickBack() = clickNode(backButton)

    // --- Semantic Verifications ---

    fun verifyPlayingAt(expectedTime: String, timeout: Duration = 15.seconds) {
        waitForText(stateLabel, VideoDetailsDataProvider.statusPlaying, timeout)
        waitForText(currentPosition, expectedTime, timeout)
        verifyTextElement(stateLabel, VideoDetailsDataProvider.statusPlaying)
        verifyTextElement(currentPosition, expectedTime)
        verifyIconButton(pauseButton)
        verifyNodeDisplayed(progress)
    }

    fun verifyPausedAt(expectedTime: String, timeout: Duration = 15.seconds) {
        waitForText(stateLabel, VideoDetailsDataProvider.statusPaused, timeout)
        waitForText(currentPosition, expectedTime, timeout)
        verifyTextElement(stateLabel, VideoDetailsDataProvider.statusPaused)
        verifyTextElement(currentPosition, expectedTime)
        verifyIconButton(playButton)
        verifyNodeDisplayed(progress)
    }

    fun verifyCompleted(timeout: Duration = 15.seconds) {
        waitForText(stateLabel, VideoDetailsDataProvider.statusCompleted, timeout)
        verifyTextElement(stateLabel, VideoDetailsDataProvider.statusCompleted)
        verifyTextElement(currentPosition, VideoDetailsDataProvider.TIME_00_30)
        verifyTextElement(duration, VideoDetailsDataProvider.TIME_00_30)
    }

    fun verifyBuffering() {
        verifyTextElement(stateLabel, VideoDetailsDataProvider.statusBuffering)
        verifyNodeDisplayed(bufferingIndicator)
    }

    /**
     * Natively verifies using Compose [waitUntil] that buffering is displayed
     * and remains active for approximately [expectedDuration] before transitioning.
     */
    fun verifyBufferingDuration(
        expectedDuration: Duration = 6.seconds,
        tolerance: Duration = 1200.milliseconds,
    ) {
        waitForNode(bufferingIndicator)
        verifyTextElement(stateLabel, VideoDetailsDataProvider.statusBuffering)
        verifyPresenceDuration(bufferingIndicator, expectedDuration, tolerance)
    }

    /**
     * Natively verifies that the buffering indicator and "Buffering" state remain
     * active for at least [duration]. If buffering ends before [duration], the test fails immediately.
     * Uses Compose's native waitUntil to keep the UI looper pumping without Thread.sleep.
     */
    fun verifyBufferingForDuration(
        duration: Duration = 5500.milliseconds,
        step: Duration = 500.milliseconds,
    ) {
        waitForNode(bufferingIndicator)
        verifyPersistsForDuration(duration) {
            isNodeDisplayed(bufferingIndicator)
        }
    }

    fun verifyError(timeout: Duration = 15.seconds) {
        waitForText(stateLabel, VideoDetailsDataProvider.statusError, timeout)
        verifyTextElement(stateLabel, VideoDetailsDataProvider.statusError)
        verifyTextElement(errorMessage, VideoDetailsDataProvider.errorMessage)
        verifyButton(retryButton, VideoDetailsDataProvider.tryAgainButton)
        verifyWarningIcon(VideoDetailsDataProvider.ICON_WARNING)
    }

    fun verifyWarningIcon(assetPath: String = VideoDetailsDataProvider.ICON_WARNING) {
        verifyRelativeIcon(
            tag = errorMessage,
            assetPath = assetPath,
            placement = IconPlacement.TOP,
            iconSize = 24.dp,
            spacing = 12.dp,
            minShapeOverlap = 0.60,
        )
    }

    fun verifyPlayerContentImage(assetPath: String, maxDiffRatio: Double = 0.20) {
        verifyImageAsset(videoPlayer, assetPath, maxDiffRatio = maxDiffRatio)
    }

    fun verifyDateIcon(assetPath: String = VideoDetailsDataProvider.ICON_CALENDAR_MONTH) {
        verifyRelativeIcon(
            matcher = hasText(VideoDetailsDataProvider.publishedPrefix, substring = true),
            assetPath = assetPath,
            placement = IconPlacement.START,
            iconSize = 16.dp,
            minShapeOverlap = 0.60,
        )
    }

    fun verifyPreviewContract(item: VideoCardExpectedData = VideoDetailsDataProvider.defaultItem) {

        verifyAnyTextDisplayed(item.title)
        verifyIconButton(backButton)

        verifyIconButton(previewPlayButton)
        verifyTextDisplayed(VideoDetailsDataProvider.videoPreviewCta)

        verifyNodesDoNotExist(pauseButton, currentPosition, progress, duration, stateLabel)

        verifyTextElement(detailTitle, item.title)
        verifyTextElement(detailCategory, item.category)
        verifyTextElement(detailDescription, item.description)
        verifyDateIcon(VideoDetailsDataProvider.ICON_CALENDAR_MONTH)
    }

    /**
     * Injects a new [VideoMode] directly into the active [PlayerViewModel] instance
     * at runtime via reflection, enabling dynamic retry recovery testing without restarting.
     */
    fun injectVideoMode(mode: com.videoqa.challenge.model.VideoMode) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val allActivities = androidx.test.runner.lifecycle.Stage.entries.flatMap { stage ->
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(stage)
            }

            var playerViewModel: PlayerViewModel? = null
            for (activity in allActivities) {
                val window = activity.window ?: continue
                val decorView = window.decorView
                val composeViews = mutableListOf<View>()
                collectComposeViews(decorView, composeViews)

                for (composeView in composeViews) {
                    playerViewModel = findPlayerViewModelWithBfs(composeView)
                    if (playerViewModel != null) break
                }
                if (playerViewModel != null) break
            }

            if (playerViewModel == null) {
                throw IllegalStateException("Active PlayerViewModel instance not found across ${allActivities.size} activities")
            }

            val field = PlayerViewModel::class.java.getDeclaredField("videoMode")
            field.isAccessible = true
            field.set(playerViewModel, mode)
        }
    }

    private fun collectComposeViews(root: View, outList: MutableList<View>) {
        if (root.javaClass.name.contains("ComposeView")) {
            outList.add(root)
        }
        if (root is android.view.ViewGroup) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                if (child != null) {
                    collectComposeViews(child, outList)
                }
            }
        }
    }

    private fun findPlayerViewModelWithBfs(root: Any): PlayerViewModel? {
        val queue = ArrayDeque<Any>()
        val visited = java.util.Collections.newSetFromMap(java.util.IdentityHashMap<Any, Boolean>())

        queue.add(root)
        visited.add(root)

        var inspectedCount = 0
        while (queue.isNotEmpty() && inspectedCount < 50000) {
            val current = queue.removeFirst()
            inspectedCount++

            if (current is PlayerViewModel) {
                return current
            }

            if (current is Array<*>) {
                for (item in current) {
                    if (item != null && visited.add(item)) {
                        if (item is PlayerViewModel) return item
                        queue.add(item)
                    }
                }
                continue
            }

            if (current is Iterable<*>) {
                for (item in current) {
                    if (item != null && visited.add(item)) {
                        if (item is PlayerViewModel) return item
                        queue.add(item)
                    }
                }
                continue
            }

            if (current is Map<*, *>) {
                for (value in current.values) {
                    if (value != null && visited.add(value)) {
                        if (value is PlayerViewModel) return value
                        queue.add(value)
                    }
                }
                continue
            }

            val objClass = current.javaClass
            if (objClass.isPrimitive ||
                objClass == String::class.java ||
                objClass == Class::class.java ||
                objClass.name.startsWith("android.graphics.") ||
                objClass.name.startsWith("android.content.res.") ||
                objClass.name.startsWith("android.view.") ||
                objClass.name.startsWith("android.widget.") ||
                objClass.name.startsWith("androidx.compose.ui.node.LayoutNode") // Skip deep layout nodes to save time
            ) {
                continue
            }

            var cls: Class<*>? = objClass
            while (cls != null && cls != Any::class.java) {
                for (field in cls.declaredFields) {
                    if (java.lang.reflect.Modifier.isStatic(field.modifiers)) continue
                    field.isAccessible = true
                    val child = runCatching { field.get(current) }.getOrNull() ?: continue
                    if (child is PlayerViewModel) return child
                    if (visited.add(child)) {
                        queue.add(child)
                    }
                }
                cls = cls.superclass
            }
        }
        return null
    }
}
