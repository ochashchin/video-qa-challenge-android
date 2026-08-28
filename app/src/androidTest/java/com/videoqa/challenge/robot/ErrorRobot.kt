package com.videoqa.challenge.robot

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.unit.dp
import com.videoqa.challenge.data.ErrorDataProvider
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ErrorRobot(rule: ComposeTestRule) : BaseRobot(rule) {

    // --- Locators ---
    private val errorState = "content_error_state"
    private val errorMessage = "content_error_message"
    private val errorRetryButton = "content_error_retry_button"
    private val emptyState = "content_empty_state"
    private val emptyRetryButton = "content_empty_retry_button"

    // --- Server Error Actions & Verifications ---

    fun verifyErrorStateDisplayed() = verifyNodeDisplayed(errorState)

    fun verifyErrorMessage(text: String = ErrorDataProvider.errorSubheadText) =
        verifyTextElement(errorMessage, text)

    fun verifyErrorTryAgainButton(text: String = ErrorDataProvider.tryAgainButtonText) =
        verifyButton(errorRetryButton, text)

    fun clickErrorTryAgainButton() = clickNode(errorRetryButton)

    fun waitForErrorState(timeout: Duration = 10.seconds) = waitForNode(errorState, timeout)

    fun verifyErrorWarningIcon(assetPath: String = ErrorDataProvider.ICON_WARNING) {
        verifyRelativeIcon(
            matcher = hasText(ErrorDataProvider.errorHeaderText),
            assetPath = assetPath,
            placement = IconPlacement.TOP,
            iconSize = 48.dp,
            spacing = 16.dp,
            minShapeOverlap = 0.60,
        )
    }

    fun verifyErrorState(
        header: String = ErrorDataProvider.errorHeaderText,
        subhead: String = ErrorDataProvider.errorSubheadText,
        buttonText: String = ErrorDataProvider.tryAgainButtonText,
        iconAssetPath: String = ErrorDataProvider.ICON_WARNING,
    ) {
        verifyNodeDisplayed(errorState)
        verifyErrorWarningIcon(iconAssetPath)
        verifyTextDisplayed(header)
        verifyTextElement(errorMessage, subhead)
        verifyButton(errorRetryButton, buttonText)
    }

    // --- Empty Response Actions & Verifications ---

    fun verifyEmptyStateDisplayed() = verifyNodeDisplayed(emptyState)

    fun verifyEmptyMovieIcon(assetPath: String = ErrorDataProvider.ICON_MOVIE) {
        verifyRelativeIcon(
            matcher = hasText(ErrorDataProvider.emptyHeaderText),
            assetPath = assetPath,
            placement = IconPlacement.TOP,
            iconSize = 48.dp,
            spacing = 16.dp,
            minShapeOverlap = 0.60,
        )
    }

    fun verifyEmptyTryAgainButton(text: String = ErrorDataProvider.tryAgainButtonText) =
        verifyButton(emptyRetryButton, text)

    fun clickEmptyTryAgainButton() = clickNode(emptyRetryButton)

    fun waitForEmptyState(timeout: Duration = 10.seconds) = waitForNode(emptyState, timeout)

    fun verifyEmptyState(
        header: String = ErrorDataProvider.emptyHeaderText,
        buttonText: String = ErrorDataProvider.tryAgainButtonText,
        iconAssetPath: String = ErrorDataProvider.ICON_MOVIE,
    ) {
        verifyNodeDisplayed(emptyState)
        verifyEmptyMovieIcon(iconAssetPath)
        verifyTextDisplayed(header)
        verifyButton(emptyRetryButton, buttonText)
    }
}
