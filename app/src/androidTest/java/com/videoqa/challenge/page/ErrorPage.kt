package com.videoqa.challenge.page

import com.videoqa.challenge.data.ErrorDataProvider
import com.videoqa.challenge.robot.ErrorRobot
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ErrorPage(private val robot: ErrorRobot) {

    // --- Server Error Actions & Verifications ---

    fun clickErrorTryAgainButton() = robot.clickErrorTryAgainButton()

    fun waitForErrorState(timeout: Duration = 10.seconds) =
        robot.waitForErrorState(timeout)

    fun verifyErrorState(
        header: String = ErrorDataProvider.errorHeaderText,
        subhead: String = ErrorDataProvider.errorSubheadText,
        buttonText: String = ErrorDataProvider.tryAgainButtonText,
        iconAssetPath: String = ErrorDataProvider.ICON_WARNING,
    ) = robot.verifyErrorState(header, subhead, buttonText, iconAssetPath)

    // --- Empty Response Actions & Verifications ---

    fun clickEmptyTryAgainButton() = robot.clickEmptyTryAgainButton()

    fun waitForEmptyState(timeout: Duration = 10.seconds) =
        robot.waitForEmptyState(timeout)

    fun verifyEmptyState(
        header: String = ErrorDataProvider.emptyHeaderText,
        buttonText: String = ErrorDataProvider.tryAgainButtonText,
        iconAssetPath: String = ErrorDataProvider.ICON_MOVIE,
    ) = robot.verifyEmptyState(header, buttonText, iconAssetPath)
}
