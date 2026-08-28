package com.videoqa.challenge.robot

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.videoqa.challenge.data.ConsentDataProvider

class ConsentRobot(rule: ComposeTestRule) : BaseRobot(rule) {

    // --- Locators ---
    private val consentIcon = "consent_icon"
    private val consentHeader = "consent_header"
    private val consentSubheader = "consent_subheader"
    private val consentAcceptButton = "consent_accept_button"
    private val consentRejectButton = "consent_reject_button"
    private val consentManagePreferencesButton = "consent_manage_preferences_button"

    // --- Component-Level Verifications ---

    fun verifyConsentIcon(assetPath: String = ConsentDataProvider.consentIconAssetPath) =
        verifyIconElement(consentIcon, assetPath)

    fun verifyConsentHeader(text: String = ConsentDataProvider.headerText) =
        verifyTextElement(consentHeader, text)

    fun verifyConsentSubheader(text: String = ConsentDataProvider.subheaderText) =
        verifyTextElement(consentSubheader, text)

    fun verifyConsentAcceptButton(text: String = ConsentDataProvider.acceptAllButtonText) =
        verifyButton(consentAcceptButton, text)

    fun verifyConsentRejectButton(text: String = ConsentDataProvider.rejectOptionalButtonText) =
        verifyButton(consentRejectButton, text)

    fun verifyConsentManagePreferencesButton(text: String = ConsentDataProvider.managePreferencesButtonText) =
        verifyButton(consentManagePreferencesButton, text)

    // --- Actions ---

    fun clickAcceptAll() = clickNode(consentAcceptButton)

    fun clickRejectOptional() = clickNode(consentRejectButton)

    fun clickManagePreferences() = clickNode(consentManagePreferencesButton)
}
