package com.videoqa.challenge.robot

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.videoqa.challenge.data.VideoCardExpectedData
import com.videoqa.challenge.data.VideoDataProvider
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class VideoRobot(rule: ComposeTestRule) : BaseRobot(rule) {

    // --- Locators ---
    private val overviewScreen = "content_overview_screen"
    private val refreshButton = "content_refresh_button"
    private val debugOptionsButton = "debug_options_button"
    private val contentList = "content_list"

    // --- Screen / Loading Idling ---

    fun waitForDataToLoad(timeout: Duration = 10.seconds) {
        waitForContent(timeout)
    }

    // --- Actions ---

    fun clickVideoCard(id: String) {
        val cardTag = "content_item_$id"
        rule.onNodeWithTag(contentList).performScrollToNode(hasTestTag(cardTag))
        rule.onNodeWithTag(cardTag).performClick()
    }

    fun clickDefaultVideoCard() {
        val defaultId = VideoDataProvider.expectedVideoCards.first().id
        clickVideoCard(defaultId)
    }

    // --- Toolbar Verifications ---

    fun verifyToolbarHeader(title: String = VideoDataProvider.toolbarTitle) =
        verifyTextDisplayed(title)

    fun verifyRefreshButton() = verifyIconButton(refreshButton)

    fun verifyDebugOptionsButton() = verifyIconButton(debugOptionsButton)

    // --- Video Card Component Verification ---

    fun verifyVideoCard(card: VideoCardExpectedData) {
        val cardTag = "content_item_${card.id}"
        val imageTag = "content_image_${card.id}"
        val iconTag = "content_icon_${card.id}"
        val titleTag = "content_title_${card.id}"
        val durationTag = "content_duration_${card.id}"
        val categoryTag = "content_category_${card.id}"
        val dateTag = "content_date_${card.id}"

        // Scroll to card inside LazyColumn
        rule.onNodeWithTag(contentList).performScrollToNode(hasTestTag(cardTag))

        // Verify card container is visible and clickable
        verifyClickableElement(cardTag)

        // Verify card gradient image layer matches expected SVG asset
        verifyImageAsset(imageTag, card.gradientAssetPath, useUnmergedTree = true)

        // Verify card center icon layer matches VectorFilled.SmartDisplay asset
        verifyIconElement(iconTag, card.iconAssetPath, useUnmergedTree = true)

        // Verify headline title
        verifyTextElement(titleTag, card.title, useUnmergedTree = true)

        // Verify time badge text
        verifyTextElement(durationTag, card.durationText, useUnmergedTree = true)

        // Verify category text
        verifyTextElement(categoryTag, card.category, useUnmergedTree = true)

        // Verify published date text
        verifyTextElement(dateTag, card.dateText, useUnmergedTree = true)
    }

    fun verifyAllVideoCards(cards: List<VideoCardExpectedData> = VideoDataProvider.expectedVideoCards) {
        waitForDataToLoad()
        cards.forEach { card ->
            verifyVideoCard(card)
        }
    }
}
