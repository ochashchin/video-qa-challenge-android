package com.videoqa.challenge.util

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Waits until the node with [tag] is gone.
 * Uses Compose's native `waitUntilDoesNotExist` which handles infinite animations like CircularProgressIndicator without freezing.
 */
@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitWhilePresent(
    tag: String,
    timeout: Duration = 10.seconds,
) {
    waitUntilDoesNotExist(hasTestTag(tag), timeout.inWholeMilliseconds)
}

/**
 * Waits until the node with [tag] appears.
 * Uses Compose's native `waitUntilAtLeastOneExists` without freezing the main thread.
 */
@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitForNode(
    tag: String,
    timeout: Duration = 10.seconds,
) {
    waitUntilAtLeastOneExists(hasTestTag(tag), timeout.inWholeMilliseconds)
}

/**
 * Waits until the loading indicator disappears and the content list is displayed.
 */
@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitForContentLoaded(timeout: Duration = 10.seconds) {
    waitWhilePresent("content_loading_indicator", timeout)
    waitForNode("content_list", timeout)
}

fun ComposeTestRule.waitForVideoReady(timeout: Duration = 10.seconds) =
    waitWhilePresent("video_buffering_indicator", timeout)
