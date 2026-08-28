package com.videoqa.challenge.page

import com.videoqa.challenge.data.VideoCardExpectedData
import com.videoqa.challenge.data.VideoDetailsDataProvider
import com.videoqa.challenge.model.VideoMode
import com.videoqa.challenge.robot.VideoDetailsRobot
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class VideoDetailsPage(private val robot: VideoDetailsRobot) {

    fun waitForDataToLoad(timeout: Duration = 10.seconds) = robot.waitForDataToLoad(timeout)

    fun startPlayback() = robot.startPlayback()

    fun clickPause() = robot.clickPause()

    fun clickPlay() = robot.clickPlay()

    fun clickTryAgain() = robot.clickTryAgain()

    fun clickBack() = robot.clickBack()

    fun verifyPlayingAt(expectedTime: String, timeout: Duration = 15.seconds) = robot.verifyPlayingAt(expectedTime, timeout)

    fun verifyPausedAt(expectedTime: String, timeout: Duration = 15.seconds) = robot.verifyPausedAt(expectedTime, timeout)

    fun verifyCompleted(timeout: Duration = 15.seconds) = robot.verifyCompleted(timeout)

    fun verifyBufferingForDuration(
        duration: Duration,
        step: Duration = 500.milliseconds,
    ) = robot.verifyBufferingForDuration(duration, step)

    fun verifyError(timeout: Duration = 15.seconds) = robot.verifyError(timeout)

    fun verifyPlayerContentImage(assetPath: String, maxDiffRatio: Double = 0.20) =
        robot.verifyPlayerContentImage(assetPath, maxDiffRatio)

    fun verifyDetailsScreen(item: VideoCardExpectedData = VideoDetailsDataProvider.defaultItem) =
        robot.verifyPreviewContract(item)

    fun injectVideoMode(mode: VideoMode) = robot.injectVideoMode(mode)
}
