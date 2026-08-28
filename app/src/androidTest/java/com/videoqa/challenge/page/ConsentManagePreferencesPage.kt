package com.videoqa.challenge.page

import com.videoqa.challenge.data.ConsentDataProvider
import com.videoqa.challenge.robot.ConsentManagePreferencesRobot

class ConsentManagePreferencesPage(private val robot: ConsentManagePreferencesRobot) {

    // --- Component-Level Verifications ---

    fun verifyPreferencesTitle(text: String = ConsentDataProvider.preferencesTitleText) =
        robot.verifyPreferencesTitle(text)

    fun verifyBackButton() =
        robot.verifyBackButton()

    fun verifyAnalyticsLabel(text: String = ConsentDataProvider.preferencesAnalyticsText) =
        robot.verifyAnalyticsLabel(text)

    fun verifyAnalyticsSwitch(isChecked: Boolean = false) =
        robot.verifyAnalyticsSwitch(isChecked)

    fun verifyPersonalisationLabel(text: String = ConsentDataProvider.preferencesPersonalisationText) =
        robot.verifyPersonalisationLabel(text)

    fun verifyPersonalisationSwitch(isChecked: Boolean = false) =
        robot.verifyPersonalisationSwitch(isChecked)

    fun verifySupportingText(text: String = ConsentDataProvider.preferencesSupportingText) =
        robot.verifySupportingText(text)

    fun verifySavePreferencesButton(text: String = ConsentDataProvider.preferencesSaveButtonText) =
        robot.verifySavePreferencesButton(text)

    // --- Actions ---

    fun clickBackButton() = robot.clickBackButton()

    fun toggleAnalytics() = robot.toggleAnalyticsSwitch()

    fun togglePersonalisation() = robot.togglePersonalisationSwitch()

    fun clickSavePreferencesButton() = robot.clickSavePreferences()
}
