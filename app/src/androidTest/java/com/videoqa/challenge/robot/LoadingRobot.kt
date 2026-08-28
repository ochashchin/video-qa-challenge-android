package com.videoqa.challenge.robot

import androidx.compose.ui.test.junit4.ComposeTestRule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class LoadingRobot(rule: ComposeTestRule) : BaseRobot(rule) {

    // --- Locators ---
    private val loadingIndicator = "content_loading_indicator"

    // --- Component Verifications ---

    fun verifyLoadingSpinner() {
        verifyNodeDisplayed(loadingIndicator)
    }

    fun verifyLoadingText(text: String) {
        verifyTextDisplayed(text)
    }

    fun verifyLoadingSpinnerGone(timeout: Duration = 7.seconds) {
        waitUntilGone(loadingIndicator, timeout)
    }

    /**
     * Natively verifies that the loading spinner remains displayed for at least [duration].
     * Uses Compose's native waitUntil to keep the UI looper pumping without Thread.sleep.
     */
    fun verifyLoadingForDuration(
        duration: Duration,
    ) {
        verifyPersistsForDuration(duration) {
            isNodeDisplayed(loadingIndicator)
        }
    }
}
