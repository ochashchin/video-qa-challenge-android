package com.videoqa.challenge.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.videoqa.challenge.model.ConsentState
import com.videoqa.challenge.model.ContentMode
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoScreenTest : BaseTest() {

    // Video screen within "Travel", "News", "News", "Technology", "Travel", "Interviews" shown
    // https://fiverrtesttracking.atlassian.net/browse/VQA-146
    @Test
    fun verify_146() {
        launchApp(
            consentState = ConsentState.AcceptedAll,
            contentMode = ContentMode.SUCCESS
        )

        videoPage.run {
            waitForDataToLoad()
            verifyToolbar()
            verifyVideoCards()
        }
    }
}
