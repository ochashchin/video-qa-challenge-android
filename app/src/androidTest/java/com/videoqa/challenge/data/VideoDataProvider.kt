package com.videoqa.challenge.data

import com.videoqa.challenge.test.R
import com.videoqa.challenge.util.readJsonArrayAsset
import com.videoqa.challenge.util.string
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class VideoCardExpectedData(
    val id: String,
    val title: String,
    val category: String,
    val durationSeconds: Int,
    val publishedDate: String,
    val description: String = "",
    val videoAsset: String = "",
) {
    val durationText: String
        get() = "%02d:%02d".format(durationSeconds / 60, durationSeconds % 60)

    val dateText: String
        get() = "  ·  " + runCatching {
            LocalDate.parse(publishedDate).format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))
        }.getOrDefault(publishedDate)

    val gradientAssetPath: String
        get() = when (category) {
            VideoDataProvider.categoryTravel -> "gradients/gradient_travel.svg"
            VideoDataProvider.categoryNews -> "gradients/gradient_news.svg"
            VideoDataProvider.categoryTechnology -> "gradients/gradient_technology.svg"
            else -> "gradients/gradient_interviews.svg"
        }

    val iconAssetPath: String = "icons/smart_display.svg"
}

object VideoDataProvider {

    val toolbarTitle: String
        get() = string(R.string.expected_video_toolbar_title)

    val categoryTravel: String
        get() = string(R.string.expected_category_travel)

    val categoryNews: String
        get() = string(R.string.expected_category_news)

    val categoryTechnology: String
        get() = string(R.string.expected_category_technology)

    val categoryInterviews: String
        get() = string(R.string.expected_category_interviews)

    val expectedVideoCards: List<VideoCardExpectedData> by lazy {
        readJsonArrayAsset("expected_content.json") { json ->
            VideoCardExpectedData(
                id = json.getString("id"),
                title = json.getString("title"),
                category = json.getString("category"),
                durationSeconds = json.getInt("durationSeconds"),
                publishedDate = json.getString("publishedDate"),
                description = json.optString("description", ""),
                videoAsset = json.optString("videoAsset", ""),
            )
        }
    }
}
