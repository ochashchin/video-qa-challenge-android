package com.videoqa.challenge.data

import com.videoqa.challenge.test.R
import com.videoqa.challenge.util.string

object ConsentDataProvider {
    const val consentIconAssetPath = "icons/privacy_tip.svg"

    val headerText: String
        get() = string(R.string.expected_consent_header)

    val subheaderText: String
        get() = string(R.string.expected_consent_subheader)

    val acceptAllButtonText: String
        get() = string(R.string.expected_consent_accept_all)

    val rejectOptionalButtonText: String
        get() = string(R.string.expected_consent_reject_optional)

    val managePreferencesButtonText: String
        get() = string(R.string.expected_consent_manage_preferences)

    val preferencesTitleText: String
        get() = string(R.string.expected_preferences_title)

    val preferencesAnalyticsText: String
        get() = string(R.string.expected_preferences_analytics)

    val preferencesPersonalisationText: String
        get() = string(R.string.expected_preferences_personalisation)

    val preferencesSupportingText: String
        get() = string(R.string.expected_preferences_supporting_text)

    val preferencesSaveButtonText: String
        get() = string(R.string.expected_preferences_save)
}
