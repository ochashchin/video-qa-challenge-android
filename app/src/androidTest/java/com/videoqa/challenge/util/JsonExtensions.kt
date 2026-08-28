package com.videoqa.challenge.util

import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.json.JSONObject

/**
 * Reads a JSON array from test assets and maps each JSONObject to model [T] using [transform].
 */
fun <T> readJsonArrayAsset(assetPath: String, transform: (JSONObject) -> T): List<T> {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val jsonString = runCatching {
        instrumentation.context.assets.open(assetPath).bufferedReader().use { it.readText() }
    }.recoverCatching {
        instrumentation.targetContext.assets.open(assetPath).bufferedReader().use { it.readText() }
    }.getOrThrow()

    val array = JSONArray(jsonString)
    return (0 until array.length()).map { index ->
        transform(array.getJSONObject(index))
    }
}
