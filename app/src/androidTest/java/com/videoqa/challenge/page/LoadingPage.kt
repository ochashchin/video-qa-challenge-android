package com.videoqa.challenge.page

import com.videoqa.challenge.data.LoadingDataProvider
import com.videoqa.challenge.robot.LoadingRobot
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class LoadingPage(private val robot: LoadingRobot) {

    fun verifyLoadingSpinner() = robot.verifyLoadingSpinner()

    fun verifyLoadingText(text: String = LoadingDataProvider.loadingText) =
        robot.verifyLoadingText(text)

    fun verifyLoadingForDuration(
        duration: Duration,
        step: Duration = 500.milliseconds,
        text: String = LoadingDataProvider.loadingText,
    ) = robot.verifyLoadingForDuration(duration)
}
