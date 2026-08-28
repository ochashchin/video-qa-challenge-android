package com.videoqa.challenge.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.videoqa.challenge.model.ConsentState
import com.videoqa.challenge.model.ContentMode
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class LoadingScreenTest : BaseTest() {

    // Home screen on consent "Accepted all", loading fast respond screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-135
    @Test
    fun verify_135() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS
        )

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }

    // Home screen on consent managed preference "Analytics", loading fast respond screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-136
    @Test
    fun verify_136() {
        launchApp(
            consentState = ConsentState.Custom(analytics = true, personalisation = false),
            contentMode = ContentMode.SUCCESS
        )

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }

    // Home screen on consent managed preference "Personalisation", loading fast respond screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-137
    @Test
    fun verify_137() {
        launchApp(
            consentState = ConsentState.Custom(analytics = false, personalisation = true),
            contentMode = ContentMode.SUCCESS
        )

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }

    // Home screen on consent managed preference "Analytics" and "Personalisation", loading fast respond screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-138
    @Test
    fun verify_138() {
        launchApp(
            consentState = ConsentState.Custom(analytics = true, personalisation = true),
            contentMode = ContentMode.SUCCESS
        )

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }

    // Consent manage preferences screen on "Save preferences" click, loading fast respond screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-139
    @Test
    fun verify_139() {
        launchApp(
            consentState = ConsentState.Required,
            contentMode = ContentMode.SUCCESS
        )

        consentPage.run {
            clickManagePreferencesButton()
        }

        consentManagePreferencesPage.run {
            clickSavePreferencesButton()
        }

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }

    // Consent screen on "Accept all" click, loading fast respond screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-140
    @Test
    fun verify_140() {
        launchApp(
            consentState = ConsentState.Required,
            contentMode = ContentMode.SUCCESS
        )

        consentPage.run {
            clickAcceptAllButton()
        }

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }

    // Consent screen on "Reject optional" click, loading fast respond screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-141
    @Test
    fun verify_141() {
        launchApp(
            consentState = ConsentState.Required,
            contentMode = ContentMode.SUCCESS
        )

        consentPage.run {
            clickRejectOptionalButton()
        }

        loadingPage.run {
            verifyLoadingSpinner()
            verifyLoadingText()
        }
    }

    // Loading screen on fast respond, spinner and "Loading videos..." text shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-142
    @Test
    fun verify_142() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS
        )

        loadingPage.run {
            verifyLoadingForDuration(duration = 400.milliseconds, step = 100.milliseconds)
        }
    }

    // Loading screen on slow respond, spinner and "Loading videos..." text shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-143
    @Test
    fun verify_143() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SLOW
        )

        loadingPage.run {
            verifyLoadingForDuration(duration = 4500.milliseconds, step = 500.milliseconds)
        }
    }
}
