package com.videoqa.challenge.model

import com.videoqa.challenge.data.VideoDataProvider

/**
 * Type-safe boundary and progress presets for Video Player test mocking.
 * Automatically targets the default first item ("amsterdam" from expected_content.json).
 */
sealed class PlaybackProgress(
    val positionMs: Long,
    val contentId: String = VideoDataProvider.expectedVideoCards.first().id,
) {
    /** Negative progress (-00:01) clamped to 00:00  */
    data object LeftBoundary : PlaybackProgress(
        positionMs = -1_000L,
    )

    /** Minimum start progress 00:00  */
    data object MinBoundary : PlaybackProgress(
        positionMs = 0L,
    )

    /** Standard Paused / Resumed progress 00:27 */
    data object PausedAt27s : PlaybackProgress(
        positionMs = 27_000L,
    )

    /** Progress exceeding duration (00:31) clamped to 00:29  */
    data object RightBoundary : PlaybackProgress(
        positionMs = 31_000L,
    )

    /** Maximum progress (00:30) displaying Completed status */
    data object MaxBoundary : PlaybackProgress(
        positionMs = 28_999L,
    )
}
