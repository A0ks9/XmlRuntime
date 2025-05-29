package com.voyager.utils

import android.util.DisplayMetrics
import android.view.ViewGroup
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import android.util.Log // Import for mocking Log.w
import android.util.TypedValue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

// Needed for TypedValue.applyDimension which is a static method.
// If using Robolectric, this might not be needed or handled differently.
@DisplayName("String.toPixels Dimension Conversion Tests")
class DimensionConverterTest {

    private lateinit var mockMetrics: DisplayMetrics
    private lateinit var mockParentView: ViewGroup

    @BeforeEach
    fun setUp() {
        mockMetrics = mockk<DisplayMetrics>()
        // Default density; tests for specific units will override this.
        every { mockMetrics.density } returns 1.0f // 1dp = 1px
        every { mockMetrics.scaledDensity } returns 1.0f // 1sp = 1px

        mockParentView = mockk<ViewGroup>()

        // Mock static TypedValue.applyDimension if not using Robolectric
        // This is a common pattern when dealing with Android static utils in JUnit.
        // For this conceptual test, we'll assume direct calls to TypedValue work
        // or are handled by Robolectric if these tests were run in such an environment.
        // If not, more elaborate mocking of TypedValue would be needed.
        // E.g. mockkStatic(TypedValue::class)
        // every { TypedValue.applyDimension(any(), any(), mockMetrics) } answers { args[1] as Float } // Simplified for px

        // Mock Log.w to avoid Android framework dependencies in pure JUnit if needed
        mockkStatic(Log::class)
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    @ParameterizedTest(name = "Plain number \"{0}\" should default to 0f")
    @ValueSource(strings = ["100", "25.5", "-10"])
    fun `plain numbers without units should default to 0`(input: String) {
        val result = input.toPixels(mockMetrics, asInt = false)
        assertEquals(0f, result as Float)
    }

    @Test
    fun `px unit conversion`() {
        assertEquals(16f, "16px".toPixels(mockMetrics, asInt = false) as Float)
        assertEquals(16, "16.0px".toPixels(mockMetrics, asInt = true) as Int)
        assertEquals(17, "16.7px".toPixels(mockMetrics, asInt = true) as Int) // Test rounding
    }

    @Test
    fun `dp unit conversion`() {
        // Assuming TypedValue.applyDimension is correctly mocked or works in test env
        // For simplicity, let's assume 1dp = 2px for this test
        mockkStatic(TypedValue::class) // Mock static for this specific test
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, mockMetrics) } returns 32f
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.5f, mockMetrics) } returns 33f


        assertEquals(32f, "16dp".toPixels(mockMetrics, asInt = false) as Float)
        assertEquals(33, "16.5dp".toPixels(mockMetrics, asInt = true) as Int) // 16.5 * 2 = 33
    }
    
    @Test
    fun `dip unit conversion`() {
        mockkStatic(TypedValue::class)
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, mockMetrics) } returns 40f
        assertEquals(40f, "20dip".toPixels(mockMetrics, asInt = false) as Float)
    }


    @Test
    fun `sp unit conversion`() {
        // Assuming TypedValue.applyDimension is correctly mocked or works
        // Let's assume 1sp = 2.5px for this test
        mockkStatic(TypedValue::class)
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, mockMetrics) } returns 30f
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12.8f, mockMetrics) } returns 32f // 12.8 * 2.5 = 32

        assertEquals(30f, "12sp".toPixels(mockMetrics, asInt = false) as Float)
        assertEquals(32, "12.8sp".toPixels(mockMetrics, asInt = true) as Int) // 12.8 * 2.5 = 32, rounded if needed
    }
    
    // Example for other units (pt, in, mm) - requires specific TypedValue mocking
    @Test
    fun `pt unit conversion`() {
        mockkStatic(TypedValue::class)
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, 10f, mockMetrics) } returns 13.3f // Example value
        assertEquals(13.3f, "10pt".toPixels(mockMetrics, asInt = false) as Float)
    }


    @Test
    fun `percentage value horizontal`() {
        every { mockParentView.measuredWidth } returns 200
        assertEquals(100f, "50%".toPixels(mockMetrics, mockParentView, horizontal = true, asInt = false) as Float)
        assertEquals(50, "25%".toPixels(mockMetrics, mockParentView, horizontal = true, asInt = true) as Int)
    }

    @Test
    fun `percentage value vertical`() {
        every { mockParentView.measuredHeight } returns 300
        assertEquals(150f, "50%".toPixels(mockMetrics, mockParentView, horizontal = false, asInt = false) as Float)
        assertEquals(30, "10%".toPixels(mockMetrics, mockParentView, horizontal = false, asInt = true) as Int)
    }

    @Test
    fun `percentage value with null parent`() {
        assertEquals(0f, "50%".toPixels(mockMetrics, null, horizontal = true, asInt = false) as Float)
        assertEquals(0, "50%".toPixels(mockMetrics, null, horizontal = true, asInt = true) as Int)
    }

    @Test
    fun `percentage value with parent zero dimension`() {
        every { mockParentView.measuredWidth } returns 0
        assertEquals(0f, "50%".toPixels(mockMetrics, mockParentView, horizontal = true, asInt = false) as Float)
    }

    @ParameterizedTest(name = "Invalid format \"{0}\" should default to 0")
    @ValueSource(strings = ["", "  ", "abc", "16.x_dp", "16units", "%", "50%abc", "abc%"])
    fun `invalid formats`(input: String) {
        assertEquals(0f, input.toPixels(mockMetrics, asInt = false) as Float)
        assertEquals(0, input.toPixels(mockMetrics, asInt = true) as Int)
    }
    
    @Test
    fun `unsupported unit defaults to 0`() {
        assertEquals(0f, "100foo".toPixels(mockMetrics, asInt = false) as Float)
    }

    @Test
    fun `value with spaces`() {
        mockkStatic(TypedValue::class)
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25f, mockMetrics) } returns 50f
        assertEquals(50f, "  25dp  ".toPixels(mockMetrics, asInt = false) as Float)
    }

    @Test
    fun `float value with rounding for asInt true`() {
        mockkStatic(TypedValue::class)
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 10.4f, mockMetrics) } returns 10.4f
        every { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 10.6f, mockMetrics) } returns 10.6f

        assertEquals(10, "10.4px".toPixels(mockMetrics, asInt = true) as Int) // 10.4 + 0.5 = 10.9 -> 10
        assertEquals(11, "10.6px".toPixels(mockMetrics, asInt = true) as Int) // 10.6 + 0.5 = 11.1 -> 11
    }
}

// Note: To run these tests outside an Android environment (pure JUnit),
// mocking of Android static methods (Log, TypedValue) is crucial.
// Using Robolectric would provide a simulated Android environment where these could work more directly.
// For this conceptual exercise, mocking is assumed or handled as shown.
// The `DimensionConverter.kt` itself does not use a logger instance, but `android.util.Log`.
// If it were using `java.util.logging.Logger`, that would also be mockable.
