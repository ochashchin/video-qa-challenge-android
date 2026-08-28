package com.videoqa.challenge.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.videoqa.challenge.data.VideoDetailsDataProvider.ASSET_FRAME_00_00
import com.videoqa.challenge.data.VideoDetailsDataProvider.ASSET_FRAME_00_27
import com.videoqa.challenge.data.VideoDetailsDataProvider.ASSET_FRAME_29_00
import com.videoqa.challenge.data.VideoDetailsDataProvider.TIME_00_00
import com.videoqa.challenge.data.VideoDetailsDataProvider.TIME_00_27
import com.videoqa.challenge.model.ConsentState
import com.videoqa.challenge.model.ContentMode
import com.videoqa.challenge.model.PlaybackProgress
import com.videoqa.challenge.model.VideoMode
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class VideoDetailsScreenTest : BaseTest() {

    // Video screen on video details player cta button, video player time progress resumed shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-156
    @Test
    fun verify_156() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            videoMode = VideoMode.COMPLETE_QUICKLY
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyPlayingAt(TIME_00_27)
            verifyPlayerContentImage(ASSET_FRAME_00_27)
        }
    }

    // Video screen on video details player cta button, video player time progress resumed left boundary shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-158
    @Test
    fun verify_158() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            playbackProgress = PlaybackProgress.LeftBoundary,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyPlayingAt(TIME_00_00)
            verifyPlayerContentImage(ASSET_FRAME_00_00)
        }
    }

    // Video screen on video details player cta button, video player time progress resumed min boundary shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-159
    @Test
    fun verify_159() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            playbackProgress = PlaybackProgress.MinBoundary,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyPlayingAt(TIME_00_00)
            verifyPlayerContentImage(ASSET_FRAME_00_00)
        }
    }

    // Video screen on video details player cta button, video player time progress resumed right boundary shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-160
    @Test
    fun verify_160() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            playbackProgress = PlaybackProgress.RightBoundary,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyPlayingAt(TIME_00_00)
            verifyPlayerContentImage(ASSET_FRAME_00_00)
        }
    }

    // Video screen on video details player cta button, video player time progress resumed max boundary shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-161
    @Test
    fun verify_161() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            playbackProgress = PlaybackProgress.MaxBoundary,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyCompleted()
            verifyPlayerContentImage(ASSET_FRAME_29_00)
        }
    }

    // Video details screen on "Amsterdam from above" preview shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-162
    @Test
    fun verify_162() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            verifyDetailsScreen()
        }
    }

    // Video details screen on player cta button click, video player slow buffering shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-163
    @Test
    fun verify_163() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            videoMode = VideoMode.BUFFERING,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyBufferingForDuration(6000.milliseconds)
        }
    }

    // Video details screen on player cta button click, video player fast buffering shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-164
    @Test
    fun verify_164() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            videoMode = VideoMode.NORMAL,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyBufferingForDuration(500.milliseconds)
        }
    }

    // Video details screen on video player slow buffering wait, video player playing shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-165
    @Test
    fun verify_165() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            videoMode = VideoMode.BUFFERING,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyPlayingAt(TIME_00_00)
            verifyPlayerContentImage(ASSET_FRAME_00_00)
        }
    }

    // Video details screen on video player slow buffering wait, video player error buffering shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-166
    @Test
    fun verify_166() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            videoMode = VideoMode.ERROR,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyError()
        }
    }

    // Video details screen on video player error buffering "Try again" click, video player error buffering shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-167
    @Test
    fun verify_167() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            videoMode = VideoMode.ERROR,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            clickTryAgain()
            verifyError()
        }
    }

    // Video details screen on video player error buffering "Try again" click, video player progress playing shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-168
    @Test
    fun verify_168() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            videoMode = VideoMode.ERROR,
            playbackProgress = PlaybackProgress.PausedAt27s,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyError()
            injectVideoMode(VideoMode.NORMAL)
            clickTryAgain()
            verifyPlayingAt(TIME_00_27)
            verifyPlayerContentImage(ASSET_FRAME_00_27)
        }
    }

    // Video details screen on video player slow buffering wait, video player resume playing shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-169
    @Test
    fun verify_169() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            videoMode = VideoMode.BUFFERING,
            playbackProgress = PlaybackProgress.PausedAt27s,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            verifyPlayingAt(TIME_00_27)
            verifyPlayerContentImage(ASSET_FRAME_00_27)
        }
    }

    // Video details screen on video player playing "Pause" click, video player progress paused shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-170
    @Test
    fun verify_170() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            playbackProgress = PlaybackProgress.PausedAt27s,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            clickPause()
            verifyPausedAt(TIME_00_27)
            verifyPlayerContentImage(ASSET_FRAME_00_27)
        }
    }

    // Video details screen on video player paused "Play" click, video player progress resumed shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-171
    @Test
    fun verify_171() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS,
            playbackProgress = PlaybackProgress.PausedAt27s,
        )

        videoPage.run {
            waitForDataToLoad()
            clickDefaultVideoCard()
        }

        videoDetailsPage.run {
            waitForDataToLoad()
            startPlayback()
            clickPause()
            clickPlay()
            verifyPlayingAt(TIME_00_27)
            verifyPlayerContentImage(ASSET_FRAME_00_27)
        }
    }
}
