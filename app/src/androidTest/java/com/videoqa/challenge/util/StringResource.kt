package com.videoqa.challenge.util

import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry

/**
 * Resolves a string resource dynamically during UI tests.
 */
fun string(@StringRes id: Int): String {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context.getString(id)
}
