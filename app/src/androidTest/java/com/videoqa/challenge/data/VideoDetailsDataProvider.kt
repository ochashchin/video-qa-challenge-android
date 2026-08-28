package com.videoqa.challenge.data

import com.videoqa.challenge.test.R
import com.videoqa.challenge.util.string

object VideoDetailsDataProvider {

    // Default target video item under test ("Amsterdam from above")
    val defaultItem: VideoCardExpectedData
        get() = VideoDataProvider.expectedVideoCards.first()

    // Time Constants
    val TIME_00_00: String
        get() = string(R.string.expected_time_00_00)

    val TIME_00_27: String
        get() = string(R.string.expected_time_00_27)

    val TIME_00_30: String
        get() = string(R.string.expected_time_00_30)

    // Player Statuses
    val statusPlaying: String
        get() = string(R.string.expected_player_status_playing)

    val statusPaused: String
        get() = string(R.string.expected_player_status_paused)

    val statusBuffering: String
        get() = string(R.string.expected_player_status_buffering)

    val statusCompleted: String
        get() = string(R.string.expected_player_status_completed)

    val statusError: String
        get() = string(R.string.expected_player_status_error)

    // Messages & Labels
    val videoPreviewCta: String
        get() = string(R.string.expected_video_preview_cta)

    val publishedPrefix: String
        get() = string(R.string.expected_published_prefix)

    val errorMessage: String
        get() = string(R.string.expected_player_error_message)

    val tryAgainButton: String
        get() = string(R.string.expected_try_again)

    // Icon SVG Asset Paths
    const val ICON_CALENDAR_MONTH = "icons/calendar_month.svg"
    const val ICON_WARNING = "icons/warning.svg"

    // Video Frame Asset Paths
    const val ASSET_FRAME_00_00 = "frames/sample_video_00_00_frame_11.png"
    const val ASSET_FRAME_00_27 = "frames/sample_video_00_27_frame_663.png"
    const val ASSET_FRAME_29_00 = "frames/sample_video_29_00_frame_719.png"
}
