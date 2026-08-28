package com.videoqa.challenge.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.videoqa.challenge.model.ConsentState
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConsentScreenTest : BaseTest() {

    // Consent screen within icon, "Your privacy choices", sub-header, "Accept all", "Reject optional", "Manage preferences" shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-60
    @Test
    fun verify_60() {
        launchApp(consentState = ConsentState.Required)

        consentPage.run {
            verifyConsentIcon()
            verifyConsentHeader()
            verifyConsentSubheader()
            verifyConsentAcceptButton()
            verifyConsentRejectButton()
            verifyConsentManagePreferencesButton()
        }
    }

    // Consent screen on "Manage preferences" click, consent manage preferences screen shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-58
    @Test
    fun verify_58() {
        launchApp(consentState = ConsentState.Required)

        consentPage.run {
            clickManagePreferencesButton()
        }

        consentManagePreferencesPage.run {
            verifyPreferencesTitle()
            verifyBackButton()
            verifyAnalyticsLabel()
            verifyAnalyticsSwitch(isChecked = false)
            verifyPersonalisationLabel()
            verifyPersonalisationSwitch(isChecked = false)
            verifySupportingText()
            verifySavePreferencesButton()
        }
    }
}
