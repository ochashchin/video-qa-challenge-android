package com.videoqa.challenge.test

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.videoqa.challenge.MainActivity
import com.videoqa.challenge.model.ConsentState
import com.videoqa.challenge.model.ContentMode
import com.videoqa.challenge.model.PlaybackProgress
import com.videoqa.challenge.model.VideoMode
import com.videoqa.challenge.model.applyConsentState
import com.videoqa.challenge.page.ConsentManagePreferencesPage
import com.videoqa.challenge.page.ConsentPage
import com.videoqa.challenge.page.ErrorPage
import com.videoqa.challenge.page.LoadingPage
import com.videoqa.challenge.page.VideoDetailsPage
import com.videoqa.challenge.page.VideoPage
import com.videoqa.challenge.robot.ConsentManagePreferencesRobot
import com.videoqa.challenge.robot.ConsentRobot
import com.videoqa.challenge.robot.ErrorRobot
import com.videoqa.challenge.robot.LoadingRobot
import com.videoqa.challenge.robot.VideoDetailsRobot
import com.videoqa.challenge.robot.VideoRobot
import org.junit.After
import org.junit.Rule

abstract class BaseTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<MainActivity>? = null

    val consentPage by lazy { ConsentPage(ConsentRobot(composeTestRule)) }
    val consentManagePreferencesPage by lazy {
        ConsentManagePreferencesPage(
            ConsentManagePreferencesRobot(composeTestRule)
        )
    }
    val loadingPage by lazy { LoadingPage(LoadingRobot(composeTestRule)) }
    val errorPage by lazy { ErrorPage(ErrorRobot(composeTestRule)) }
    val videoPage by lazy { VideoPage(VideoRobot(composeTestRule)) }
    val videoDetailsPage by lazy { VideoDetailsPage(VideoDetailsRobot(composeTestRule)) }

    /**
     * Launches the MainActivity with specific mock states injected via Intent Extras.
     */
    protected fun launchApp(
        consentState: ConsentState = ConsentState.Required,
        contentMode: ContentMode = ContentMode.SUCCESS,
        videoMode: VideoMode = VideoMode.NORMAL,
        playbackProgress: PlaybackProgress? = null,
        contentDelayMs: Long? = null,
        videoBufferingMs: Long? = null,
    ) {
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
            applyConsentState(consentState)
            putExtra("contentMode", contentMode.rawValue)
            putExtra("videoMode", videoMode.rawValue)
            contentDelayMs?.let { putExtra("contentDelayMs", it) }
            videoBufferingMs?.let { putExtra("videoBufferingMs", it) }
            playbackProgress?.let {
                putExtra("playbackProgressContentId", it.contentId)
                putExtra("playbackProgressMs", it.positionMs)
            }
        }
        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun teardown() {
        scenario?.close()
    }
}
