package com.videoqa.challenge.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.videoqa.challenge.model.ConsentState
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConsentManagePreferencesScreenTest : BaseTest() {

    // Consent manage preferences screen on "Back" click, consent screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-59
    @Test
    fun verify_59() {
        launchApp(consentState = ConsentState.Required)

        consentPage.run {
            clickManagePreferencesButton()
        }

        consentManagePreferencesPage.run {
            clickBackButton()
        }

        consentPage.run {
            verifyConsentIcon()
            verifyConsentHeader()
            verifyConsentSubheader()
            verifyConsentAcceptButton()
            verifyConsentRejectButton()
            verifyConsentManagePreferencesButton()
        }
    }

    // Consent manage preferences screen within "Analytics" and "Personalized content" shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-79
    @Test
    fun verify_79() {
        launchApp(consentState = ConsentState.Required)

        consentPage.run {
            clickManagePreferencesButton()
        }

        consentManagePreferencesPage.run {
            verifyBackButton()
            verifyPreferencesTitle()
            verifyAnalyticsLabel()
            verifyAnalyticsSwitch(isChecked = false)
            verifyPersonalisationLabel()
            verifyPersonalisationSwitch(isChecked = false)
            verifySupportingText()
            verifySavePreferencesButton()
        }
    }

    // Consent manage preferences on consent item switch click, enable analytics preferences
    // https://fiverrtesttracking.atlassian.net/browse/VQA-80
    @Test
    fun verify_80() {
        launchApp(consentState = ConsentState.Required)

        consentPage.run {
            clickManagePreferencesButton()
        }

        consentManagePreferencesPage.run {
            toggleAnalytics()
            verifyAnalyticsSwitch(isChecked = true)
        }
    }

    // Consent manage preferences on consent item switch click, disable analytics preferences
    // https://fiverrtesttracking.atlassian.net/browse/VQA-82
    @Test
    fun verify_82() {
        launchApp(consentState = ConsentState.Required)

        consentPage.run {
            clickManagePreferencesButton()
        }

        consentManagePreferencesPage.run {
            toggleAnalytics()
            toggleAnalytics()
            verifyAnalyticsSwitch(isChecked = false)
        }
    }

    // Consent manage preferences on consent item switch click, updates personalized content preferences
    // https://fiverrtesttracking.atlassian.net/browse/VQA-84
    @Test
    fun verify_84() {
        launchApp(consentState = ConsentState.Required)

        consentPage.run {
            clickManagePreferencesButton()
        }

        consentManagePreferencesPage.run {
            togglePersonalisation()
            verifyPersonalisationSwitch(isChecked = true)
        }
    }

    // Consent manage preferences on consent item switch click, disable personalized content preferences
    // https://fiverrtesttracking.atlassian.net/browse/VQA-86
    @Test
    fun verify_86() {
        launchApp(consentState = ConsentState.Required)

        consentPage.run {
            clickManagePreferencesButton()
        }

        consentManagePreferencesPage.run {
            togglePersonalisation()
            togglePersonalisation()
            verifyPersonalisationSwitch(isChecked = false)
        }
    }
}
