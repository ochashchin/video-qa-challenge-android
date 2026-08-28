package com.videoqa.challenge.page

import com.videoqa.challenge.data.VideoCardExpectedData
import com.videoqa.challenge.data.VideoDataProvider
import com.videoqa.challenge.robot.VideoRobot
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class VideoPage(private val robot: VideoRobot) {

    fun waitForDataToLoad(timeout: Duration = 10.seconds) = robot.waitForDataToLoad(timeout)

    fun clickVideoCard(id: String) = robot.clickVideoCard(id)

    fun clickDefaultVideoCard() = robot.clickDefaultVideoCard()

    fun verifyToolbar() {
        robot.verifyToolbarHeader()
        robot.verifyRefreshButton()
        robot.verifyDebugOptionsButton()
    }

    fun verifyVideoCards(cards: List<VideoCardExpectedData> = VideoDataProvider.expectedVideoCards) {
        robot.verifyAllVideoCards(cards)
    }
}
