package com.videoqa.challenge.page

import com.videoqa.challenge.data.ConsentDataProvider
import com.videoqa.challenge.robot.ConsentRobot

class ConsentPage(private val robot: ConsentRobot) {

    // --- Component-Level Verifications ---

    fun verifyConsentIcon(assetPath: String = ConsentDataProvider.consentIconAssetPath) =
        robot.verifyConsentIcon(assetPath)

    fun verifyConsentHeader(text: String = ConsentDataProvider.headerText) =
        robot.verifyConsentHeader(text)

    fun verifyConsentSubheader(text: String = ConsentDataProvider.subheaderText) =
        robot.verifyConsentSubheader(text)

    fun verifyConsentAcceptButton(text: String = ConsentDataProvider.acceptAllButtonText) =
        robot.verifyConsentAcceptButton(text)

    fun verifyConsentRejectButton(text: String = ConsentDataProvider.rejectOptionalButtonText) =
        robot.verifyConsentRejectButton(text)

    fun verifyConsentManagePreferencesButton(text: String = ConsentDataProvider.managePreferencesButtonText) =
        robot.verifyConsentManagePreferencesButton(text)

    // --- Actions ---

    fun clickAcceptAllButton() = robot.clickAcceptAll()

    fun clickRejectOptionalButton() = robot.clickRejectOptional()

    fun clickManagePreferencesButton() = robot.clickManagePreferences()
}
