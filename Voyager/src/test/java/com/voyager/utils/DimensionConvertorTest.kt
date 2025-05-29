package com.voyager.utils

import android.util.DisplayMetrics
import android.view.ViewGroup
import com.voyager.utils.toPixels // Corrected import for top-level extension function
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.math.roundToInt

@RunWith(MockitoJUnitRunner::class)
class DimensionConvertorTest {

    @Mock
    lateinit var mockDisplayMetrics: DisplayMetrics

    @Mock
    lateinit var mockParentView: ViewGroup

    private val delta = 1e-5f // Delta for float comparisons

    @Before
    fun setUp() {
        mockDisplayMetrics.density = 2.0f
        mockDisplayMetrics.scaledDensity = 2.0f
        mockDisplayMetrics.xdpi = 320f // Assuming density 2.0 * 160dpi base
    }

    // --- DP Tests ---
    @Test
    fun `valid DP to pixels as Float`() {
        val result = "16dp".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(16 * mockDisplayMetrics.density, result as Float, delta)
    }

    @Test
    fun `valid DP to pixels as Int`() {
        val result = "16dp".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = true)
        assertEquals((16 * mockDisplayMetrics.density).roundToInt(), result as Int)
    }

    // --- SP Tests ---
    @Test
    fun `valid SP to pixels as Float`() {
        val result = "12sp".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(12 * mockDisplayMetrics.scaledDensity, result as Float, delta)
    }

    @Test
    fun `valid SP to pixels as Int`() {
        val result = "12sp".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = true)
        assertEquals((12 * mockDisplayMetrics.scaledDensity).roundToInt(), result as Int)
    }

    // --- PX Tests ---
    @Test
    fun `valid PX to pixels as Float`() {
        val result = "20px".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(20f, result as Float, delta)
    }

    @Test
    fun `valid PX to pixels as Int`() {
        val result = "20px".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = true)
        assertEquals(20, result as Int)
    }

    // --- PT Tests ---
    @Test
    fun `valid PT to pixels as Float`() {
        // This test assumes DimensionConvertor.toPixels for "pt" uses a formula similar to
        // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, metrics)
        // which is (value / 72) * metrics.xdpi
        val expected = (1.0f / 72.0f) * mockDisplayMetrics.xdpi
        val result = "1pt".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(expected, result as Float, delta)
    }

    // --- Percentage Tests (Width) ---
    @Test
    fun `percentage width to pixels as Float`() {
        `when`(mockParentView.measuredWidth).thenReturn(1000)
        val result = "50%".toPixels(metrics = mockDisplayMetrics, parent = mockParentView, horizontal = true, asInt = false)
        assertEquals(500f, result as Float, delta)
    }

    @Test
    fun `percentage width to pixels as Int`() {
        `when`(mockParentView.measuredWidth).thenReturn(800)
        val result = "25%".toPixels(metrics = mockDisplayMetrics, parent = mockParentView, horizontal = true, asInt = true)
        assertEquals(200, result as Int)
    }

    // --- Percentage Tests (Height) ---
    @Test
    fun `percentage height to pixels as Float`() {
        `when`(mockParentView.measuredHeight).thenReturn(600)
        val result = "50%".toPixels(metrics = mockDisplayMetrics, parent = mockParentView, horizontal = false, asInt = false)
        assertEquals(300f, result as Float, delta)
    }

    // --- Percentage Value with Null Parent ---
    @Test
    fun `percentage with null parent returns 0f`() {
        val result = "50%".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(0f, result as Float, delta)
    }

    // --- Invalid Dimension String Tests ---
    @Test
    fun `invalid string 'abc' returns 0f`() {
        val result = "abc".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(0f, result as Float, delta)
    }

    @Test
    fun `invalid string '16' (missing unit) returns 0f`() {
        val result = "16".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(0f, result as Float, delta)
    }

    @Test
    fun `invalid string 'dp16' returns 0f`() {
        val result = "dp16".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(0f, result as Float, delta)
    }

    // --- Invalid Percentage String ---
    @Test
    fun `invalid percentage 'abc%' returns 0f`() {
        val result = "abc%".toPixels(metrics = mockDisplayMetrics, parent = mockParentView, horizontal = true, asInt = false)
        assertEquals(0f, result as Float, delta)
    }

    // --- Zero Value Tests ---
    @Test
    fun `zero dp returns 0f`() {
        val result = "0dp".toPixels(metrics = mockDisplayMetrics, parent = null, horizontal = true, asInt = false)
        assertEquals(0f, result as Float, delta)
    }

    @Test
    fun `zero percentage returns 0f`() {
        `when`(mockParentView.measuredWidth).thenReturn(100)
        val result = "0%".toPixels(metrics = mockDisplayMetrics, parent = mockParentView, horizontal = true, asInt = false)
        assertEquals(0f, result as Float, delta)
    }
}
