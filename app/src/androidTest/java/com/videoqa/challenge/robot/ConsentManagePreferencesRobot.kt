package com.videoqa.challenge.robot

import androidx.compose.ui.test.junit4.ComposeTestRule
import com.videoqa.challenge.data.ConsentDataProvider

class ConsentManagePreferencesRobot(rule: ComposeTestRule) : BaseRobot(rule) {

    // --- Locators ---
    private val preferencesTitle = "preferences_title"
    private val preferencesBackButton = "preferences_back_button"
    private val analyticsToggle = "analytics_toggle"
    private val personalisationToggle = "personalisation_toggle"
    private val preferencesSaveButton = "preferences_save_button"

    // --- Component-Level Verifications ---

    fun verifyPreferencesTitle(text: String = ConsentDataProvider.preferencesTitleText) =
        verifyTextElement(preferencesTitle, text)

    fun verifyBackButton() =
        verifyIconButton(preferencesBackButton)

    fun verifyAnalyticsLabel(text: String = ConsentDataProvider.preferencesAnalyticsText) =
        verifyTextDisplayed(text)

    fun verifyAnalyticsSwitch(isChecked: Boolean = false) =
        verifySwitchElement(analyticsToggle, isChecked)

    fun verifyPersonalisationLabel(text: String = ConsentDataProvider.preferencesPersonalisationText) =
        verifyTextDisplayed(text)

    fun verifyPersonalisationSwitch(isChecked: Boolean = false) =
        verifySwitchElement(personalisationToggle, isChecked)

    fun verifySupportingText(text: String = ConsentDataProvider.preferencesSupportingText) =
        verifyTextDisplayed(text)

    fun verifySavePreferencesButton(text: String = ConsentDataProvider.preferencesSaveButtonText) =
        verifyButton(preferencesSaveButton, text)

    // --- Actions ---

    fun clickBackButton() = clickNode(preferencesBackButton)

    fun toggleAnalyticsSwitch() = toggleSwitch(analyticsToggle)

    fun togglePersonalisationSwitch() = toggleSwitch(personalisationToggle)

    fun clickSavePreferences() = clickNode(preferencesSaveButton)
}
