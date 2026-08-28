package com.videoqa.challenge.model

import android.content.Intent

sealed interface ConsentState {
    /**
     * Clean state: App shows Consent Screen.
     */
    data object Required : ConsentState

    /**
     * <string name="consent.choice">accepted_all</string>
     * analytics: true, personalisation: true
     */
    data object AcceptedAll : ConsentState

    /**
     * <string name="consent.choice">rejected_optional</string>
     * analytics: false, personalisation: false
     */
    data object RejectedOptional : ConsentState

    /**
     * <string name="consent.choice">custom</string>
     * Explicit analytics & personalisation settings
     */
    data class Custom(
        val analytics: Boolean = false,
        val personalisation: Boolean = false
    ) : ConsentState
}

/**
 * Extension on Intent to inject the ConsentState via Intent Extras.
 */
fun Intent.applyConsentState(state: ConsentState) {
    when (state) {
        is ConsentState.Required -> {
            putExtra("resetAllState", true)
            removeExtra("consentChoice")
            removeExtra("consentAnalytics")
            removeExtra("consentPersonalisation")
        }
        is ConsentState.AcceptedAll -> {
            putExtra("resetAllState", false)
            putExtra("consentChoice", "accepted_all")
            putExtra("consentAnalytics", true)
            putExtra("consentPersonalisation", true)
        }
        is ConsentState.RejectedOptional -> {
            putExtra("resetAllState", false)
            putExtra("consentChoice", "rejected_optional")
            putExtra("consentAnalytics", false)
            putExtra("consentPersonalisation", false)
        }
        is ConsentState.Custom -> {
            putExtra("resetAllState", false)
            putExtra("consentChoice", "custom")
            putExtra("consentAnalytics", state.analytics)
            putExtra("consentPersonalisation", state.personalisation)
        }
    }
}
