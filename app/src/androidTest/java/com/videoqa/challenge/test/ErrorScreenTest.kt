package com.videoqa.challenge.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.videoqa.challenge.model.ConsentState
import com.videoqa.challenge.model.ContentMode
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorScreenTest : BaseTest() {

    // Error screen on server error response, "Something went wrong" and "Try again" shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-189
    @Test
    fun verify_189() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.ERROR
        )

        errorPage.run {
            waitForErrorState()
            verifyErrorState()
        }
    }

    // Error screen on server empty response, "No videos are available" and "Try again" shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-188
    @Test
    fun verify_188() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.EMPTY
        )

        errorPage.run {
            waitForEmptyState()
            verifyEmptyState()
        }
    }

    // Error screen on server error response on "Try again" click, loading screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-133
    @Test
    fun verify_133() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.ERROR
        )

        errorPage.run {
            clickErrorTryAgainButton()
        }

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }

    // Error screen on server empty response "Try again" click, loading screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-134
    @Test
    fun verify_134() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.EMPTY
        )

        errorPage.run {
            clickEmptyTryAgainButton()
        }

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }
}
