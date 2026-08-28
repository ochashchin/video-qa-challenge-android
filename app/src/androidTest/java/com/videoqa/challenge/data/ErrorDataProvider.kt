package com.videoqa.challenge.data

import com.videoqa.challenge.test.R
import com.videoqa.challenge.util.string

object ErrorDataProvider {

    val errorHeaderText: String
        get() = string(R.string.expected_error_header)

    val errorSubheadText: String
        get() = string(R.string.expected_error_subhead)

    val emptyHeaderText: String
        get() = string(R.string.expected_empty_header)

    val tryAgainButtonText: String
        get() = string(R.string.expected_try_again)

    const val ICON_WARNING = "icons/warning.svg"
    const val ICON_MOVIE = "icons/movie.svg"
}
