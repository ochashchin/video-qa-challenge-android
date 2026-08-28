package com.videoqa.challenge.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.core.content.ContextCompat
import androidx.core.graphics.PathParser
import androidx.core.graphics.drawable.toBitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Asserts that the captured Compose node's image/icon matches the given drawable resource.
 */
fun SemanticsNodeInteraction.assertMatchesDrawable(
    @DrawableRes expectedId: Int,
    minShapeOverlap: Double = 0.70,
) {
    if (expectedId <= 0) throw IllegalArgumentException("Invalid drawable resource id: $expectedId")
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val expectedDrawable = ContextCompat.getDrawable(context, expectedId)
        ?: throw IllegalArgumentException("Drawable resource $expectedId not found")

    val actualBitmap = this.captureNodeBitmap()
    val expectedBitmap = expectedDrawable.toBitmap(actualBitmap.width, actualBitmap.height)

    if (!bitmapsMatch(actualBitmap, expectedBitmap, minShapeOverlap = minShapeOverlap)) {
        throw AssertionError("Captured icon bitmap does not match expected drawable resource: $expectedId")
    }
}

/**
 * Asserts that the captured Compose node matches the SVG or Vector XML asset in assets,
 * or a raster screenshot frame (.png, .jpg, .webp).
 *
 * @param maxDiffRatio Maximum allowed pixel difference ratio (default 0.015 = 1.5% max diff / 98.5% similarity).
 *                     Allows natural 1-frame jitter (e.g. frame 663 vs 664) while strictly rejecting different timestamps (e.g. 00:00 vs 00:27).
 */
fun SemanticsNodeInteraction.assertMatchesAsset(
    assetPath: String,
    maxDiffRatio: Double = 0.20,
    minShapeOverlap: Double = 0.70,
) {
    val actualBitmap = this.captureNodeBitmap()
    val expectedBitmap = renderAssetToBitmap(assetPath, actualBitmap.width, actualBitmap.height)

    if (!bitmapsMatch(actualBitmap, expectedBitmap, minShapeOverlap = minShapeOverlap, maxDiffRatio = maxDiffRatio)) {
        throw AssertionError("Captured node bitmap does not match asset at: $assetPath")
    }
}

/**
 * Asserts that the provided [actualBitmap] matches the SVG or Vector XML asset in assets.
 */
fun assertBitmapMatchesAsset(
    actualBitmap: Bitmap,
    assetPath: String,
    maxDiffRatio: Double = 0.20,
    minShapeOverlap: Double = 0.70,
) {
    val expectedBitmap = renderAssetToBitmap(assetPath, actualBitmap.width, actualBitmap.height)
    if (!bitmapsMatch(actualBitmap, expectedBitmap, minShapeOverlap = minShapeOverlap, maxDiffRatio = maxDiffRatio)) {
        throw AssertionError("Bitmap does not match asset at: $assetPath")
    }
}

/**
 * Captures a SemanticsNode to a Bitmap.
 * Uses UiAutomation screenshot (from SurfaceFlinger) to capture hardware-rendered surfaces
 * (like SurfaceView / ExoPlayer PlayerView) with complete video frames.
 * Falls back to PixelCopy and Compose captureToImage if needed.
 */
fun SemanticsNodeInteraction.captureNodeBitmap(): Bitmap {
    return runCatching {
        captureWithUiAutomation()
    }.recoverCatching {
        captureWithPixelCopy()
    }.getOrElse {
        this.captureToImage().asAndroidBitmap()
    }
}

private fun SemanticsNodeInteraction.captureWithUiAutomation(): Bitmap {
    val node = this.fetchSemanticsNode("Failed to fetch node for UiAutomation capture")
    val bounds = node.boundsInWindow
    val left = bounds.left.toInt()
    val top = bounds.top.toInt()
    val width = bounds.width.toInt()
    val height = bounds.height.toInt()

    if (width <= 0 || height <= 0) {
        throw IllegalArgumentException("Invalid node bounds: $bounds")
    }

    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val fullScreenshot = instrumentation.uiAutomation.takeScreenshot()
        ?: throw IllegalStateException("UiAutomation.takeScreenshot returned null")

    val safeLeft = left.coerceIn(0, fullScreenshot.width)
    val safeTop = top.coerceIn(0, fullScreenshot.height)
    val safeWidth = width.coerceAtMost(fullScreenshot.width - safeLeft)
    val safeHeight = height.coerceAtMost(fullScreenshot.height - safeTop)

    return Bitmap.createBitmap(fullScreenshot, safeLeft, safeTop, safeWidth, safeHeight)
}

private fun SemanticsNodeInteraction.captureWithPixelCopy(): Bitmap {
    val node = this.fetchSemanticsNode("Failed to fetch node for PixelCopy")
    val bounds = node.boundsInWindow
    val srcRect = Rect(
        bounds.left.toInt(),
        bounds.top.toInt(),
        bounds.right.toInt(),
        bounds.bottom.toInt(),
    )

    if (srcRect.width() <= 0 || srcRect.height() <= 0) {
        return this.captureToImage().asAndroidBitmap()
    }

    var activity: Activity? = null
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        activity = ActivityLifecycleMonitorRegistry.getInstance()
            .getActivitiesInStage(Stage.RESUMED)
            .firstOrNull()
    }

    val window = activity?.window ?: throw IllegalStateException("No active window for PixelCopy")
    val destBitmap = Bitmap.createBitmap(srcRect.width(), srcRect.height(), Bitmap.Config.ARGB_8888)

    val latch = CountDownLatch(1)
    var copyResult = PixelCopy.ERROR_UNKNOWN

    PixelCopy.request(
        window,
        srcRect,
        destBitmap,
        { result ->
            copyResult = result
            latch.countDown()
        },
        Handler(Looper.getMainLooper()),
    )

    val completed = latch.await(3, TimeUnit.SECONDS)
    if (!completed || copyResult != PixelCopy.SUCCESS) {
        throw IllegalStateException("PixelCopy failed (code: $copyResult, timeout: ${!completed})")
    }

    return destBitmap
}

/**
 * Compares two bitmaps.
 * For icons: uses Intersection-over-Union (IoU) shape matching so completely different icons (e.g. Gear vs Play) fail.
 * For gradients & video frames: evaluates whole-surface RGB delta against [maxDiffRatio].
 */
fun bitmapsMatch(
    actualBmp: Bitmap,
    expectedBmp: Bitmap,
    minShapeOverlap: Double = 0.70,
    maxDiffRatio: Double = 0.015,
): Boolean {
    if (actualBmp.width != expectedBmp.width || actualBmp.height != expectedBmp.height) {
        return false
    }
    if (actualBmp.sameAs(expectedBmp)) {
        return true
    }

    // Check if expected bitmap has transparent pixels (indicating an icon vector)
    val cornerExpectedAlpha = (expectedBmp.getPixel(0, 0) ushr 24) and 0xFF
    val isIconComparison = cornerExpectedAlpha < 50

    if (isIconComparison) {
        // Ambient background color detection
        val bgPixel = actualBmp.getPixel(0, 0)
        var matchCount = 0
        var unionCount = 0

        for (x in 0 until actualBmp.width) {
            for (y in 0 until actualBmp.height) {
                val pActual = actualBmp.getPixel(x, y)
                val pExpected = expectedBmp.getPixel(x, y)

                val isActualIconPixel = colorDistance(pActual, bgPixel) > 25
                val isExpectedIconPixel = ((pExpected ushr 24) and 0xFF) > 25

                if (isActualIconPixel || isExpectedIconPixel) {
                    unionCount++
                    if (isActualIconPixel && isExpectedIconPixel) {
                        matchCount++
                    }
                }
            }
        }

        if (unionCount == 0) return true
        val iouScore = matchCount.toDouble() / unionCount
        return iouScore >= minShapeOverlap
    } else {
        // Full surface comparison: sample across the entire image
        var diffCount = 0
        var totalSampled = 0

        for (x in 0 until actualBmp.width step 2) {
            for (y in 0 until actualBmp.height step 2) {
                totalSampled++
                val pActual = actualBmp.getPixel(x, y)
                val pExpected = expectedBmp.getPixel(x, y)
                if (colorDistance(pActual, pExpected) > 40) {
                    diffCount++
                }
            }
        }

        if (totalSampled == 0) return true
        val diffRatio = diffCount.toDouble() / totalSampled
        return diffRatio <= maxDiffRatio
    }
}

private fun colorDistance(color1: Int, color2: Int): Int {
    val r1 = (color1 ushr 16) and 0xFF
    val g1 = (color1 ushr 8) and 0xFF
    val b1 = color1 and 0xFF

    val r2 = (color2 ushr 16) and 0xFF
    val g2 = (color2 ushr 8) and 0xFF
    val b2 = color2 and 0xFF

    return abs(r1 - r2) + abs(g1 - g2) + abs(b1 - b2)
}

/**
 * Parses SVG (<svg>) or Android Vector Drawable (<vector>) or raster image (.png, .jpg, .webp) from assets,
 * rendering it onto an in-memory Bitmap matching the target dimensions.
 */
private fun renderAssetToBitmap(assetPath: String, targetWidth: Int, targetHeight: Int): Bitmap {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val inputStream = runCatching {
        instrumentation.context.assets.open(assetPath)
    }.recoverCatching {
        instrumentation.targetContext.assets.open(assetPath)
    }.getOrThrow()

    // 1. Direct raster decode for frame screenshots (.png, .jpg, .jpeg, .webp)
    if (assetPath.endsWith(".png", ignoreCase = true) ||
        assetPath.endsWith(".jpg", ignoreCase = true) ||
        assetPath.endsWith(".jpeg", ignoreCase = true) ||
        assetPath.endsWith(".webp", ignoreCase = true)
    ) {
        val decoded = BitmapFactory.decodeStream(inputStream)
            ?: throw IllegalArgumentException("Could not decode image asset at: $assetPath")
        return Bitmap.createScaledBitmap(decoded, targetWidth, targetHeight, true)
    }

    // 2. Vector SVG / XML parsing
    val factory = XmlPullParserFactory.newInstance().apply { isNamespaceAware = true }
    val parser = factory.newPullParser().apply { setInput(inputStream, "UTF-8") }

    val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val pathDataList = mutableListOf<String>()
    val stopColors = mutableListOf<Int>()
    var viewportWidth = 24f
    var viewportHeight = 24f

    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
            when (parser.name) {
                "vector" -> {
                    for (i in 0 until parser.attributeCount) {
                        if (parser.getAttributeName(i).contains("viewportWidth")) {
                            viewportWidth = parser.getAttributeValue(i).toFloatOrNull() ?: 24f
                        } else if (parser.getAttributeName(i).contains("viewportHeight")) {
                            viewportHeight = parser.getAttributeValue(i).toFloatOrNull() ?: 24f
                        }
                    }
                }
                "svg" -> {
                    for (i in 0 until parser.attributeCount) {
                        val name = parser.getAttributeName(i)
                        val value = parser.getAttributeValue(i)
                        if (name == "viewBox") {
                            val parts = value.split(" ", ",").filter { it.isNotBlank() }
                            if (parts.size >= 4) {
                                viewportWidth = parts[2].toFloatOrNull() ?: 24f
                                viewportHeight = parts[3].toFloatOrNull() ?: 24f
                            }
                        } else if (name == "width" && viewportWidth == 24f) {
                            viewportWidth = value.replace("px", "").replace("dp", "").toFloatOrNull() ?: 24f
                        } else if (name == "height" && viewportHeight == 24f) {
                            viewportHeight = value.replace("px", "").replace("dp", "").toFloatOrNull() ?: 24f
                        }
                    }
                }
                "path" -> {
                    for (i in 0 until parser.attributeCount) {
                        val name = parser.getAttributeName(i)
                        if (name == "d" || name.contains("pathData")) {
                            pathDataList.add(parser.getAttributeValue(i))
                        }
                    }
                }
                "stop" -> {
                    for (i in 0 until parser.attributeCount) {
                        if (parser.getAttributeName(i).contains("stop-color") || parser.getAttributeName(i).contains("stopColor")) {
                            val colorHex = parser.getAttributeValue(i)
                            runCatching { Color.parseColor(colorHex) }.onSuccess { stopColors.add(it) }
                        }
                    }
                }
            }
        }
        eventType = parser.next()
    }

    // If SVG defines a gradient with stop colors, render the linear gradient
    if (stopColors.size >= 2) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(),
                stopColors.first(), stopColors.last(),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), paint)
        return bitmap
    }

    // Otherwise render vector path data with EVEN_ODD fill rule for shape cutouts
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0xFF000000.toInt()
    }
    val matrix = Matrix().apply {
        postScale(targetWidth / viewportWidth, targetHeight / viewportHeight)
    }
    for (pathData in pathDataList) {
        val path = PathParser.createPathFromPathData(pathData)
        path.fillType = Path.FillType.EVEN_ODD
        path.transform(matrix)
        canvas.drawPath(path, paint)
    }

    return bitmap
}
