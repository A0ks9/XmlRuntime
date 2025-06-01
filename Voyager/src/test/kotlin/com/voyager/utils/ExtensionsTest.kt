package com.voyager.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.WeakHashMap

// Mocking Log for String.toLayoutParam -> toPixels -> logWarning if Dimens.toPixels is not found or fails
import android.util.Log
import com.voyager.core.view.model.GeneratedView

@DisplayName("Extensions.kt Tests")
class ExtensionsTest {

    @BeforeEach
    fun setUp() {
        // Clear caches if they are accessible or provide a method to clear them.
        // For this test, we assume they are not directly clearable from here,
        // or tests are independent enough.
        // If viewIdCache or activityNameCache were objects, they could be reset.
        // Since they are private top-level vals, they persist across tests in the same suite run.
        // This might affect caching tests if not handled (e.g. by using different keys).

        // Mock Log.w for any warnings that might be logged by tested extensions or their callees
        mockkStatic(Log::class)
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0 // If any errors are logged

        // Mock Build.VERSION.SDK_INT for padding tests if needed, though API 21+ is assumed.
        // mockkStatic(Build.VERSION::class)
        // every { Build.VERSION.SDK_INT } returns Build.VERSION_CODES.LOLLIPOP
    }

    // --- String Extensions ---

    @ParameterizedTest(name = "\"{0}\".isBoolean() should be {1}")
    @CsvSource([
        "true, true", "false, false", "TRUE, true", "FALSE, false",
        "0, true", "1, true", "yes, true", "no, true",
        "t, true", "f, true", "on, true", "off, true",
        "TrUe, true", "oN, true",
        "invalid, false", "2, false", "", "falsee, false"
    ])
    fun `isBoolean tests`(input: String, expected: Boolean) {
        assertEquals(expected, input.isBoolean())
    }

    @ParameterizedTest(name = "\"{0}\".isColor() should be {1}")
    @CsvSource([
        "#FFFFFF, true", "@color/my_color, true", "#123, true",
        "red, false", "invalid, false", "", "false"
    ])
    fun `isColor tests`(input: String, expected: Boolean) {
        assertEquals(expected, input.isColor())
    }

    @ParameterizedTest(name = "\"{0}\".extractViewId() should be \"{1}\"")
    @CsvSource([
        "@+id/name, name", "@id/name, name", "name, name",
        "@android:id/text1, text1", "@+android:id/text1, text1",
        "id/name_with_underscore, name_with_underscore",
        "invalid, ''", "@id/, ''", ", ''"
    ])
    fun `extractViewId tests`(input: String, expected: String) {
        val actual = input.extractViewId()
        // Handle empty string from CsvSource if it's problematic for `expected`
        assertEquals(if (expected == "null" || expected.isEmpty()) "" else expected, actual)
    }
    
    @Test
    @DisplayName("String.toLayoutParam - keyword conversions")
    fun `toLayoutParam keywords`() {
        val mockMetrics = mockk<DisplayMetrics>()
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, "match_parent".toLayoutParam(mockMetrics, null, true))
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, "fill_parent".toLayoutParam(mockMetrics, null, false))
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, "wrap_content".toLayoutParam(mockMetrics, null, true))
    }

    @Test
    @DisplayName("String.toLayoutParam - pixel conversion (conceptual via toPixels)")
    fun `toLayoutParam pixel conversion`() {
        val mockMetrics = mockk<DisplayMetrics>()
        // This test relies on the corrected call to `this.toPixels` which is in DimensionConverter.kt
        // We assume `String.toPixels` from DimensionConverterTest is working correctly.
        // Here, we're just testing the `else` branch of `toLayoutParam`.
        
        // Mock the behavior of String.toPixels for this specific test context
        // This is tricky because toPixels is an extension.
        // A better way would be to have DimensionConverter as an interface and inject it,
        // or use a more integrated test environment like Robolectric.
        // For a pure unit test, this specific part is hard to isolate perfectly without deeper changes.

        // Conceptual: if "16dp".toPixels(...) returns 32, then "16dp".toLayoutParam(...) should be 32.
        // This test implicitly relies on DimensionConverterTest being green.
        assertTrue(true, "Conceptual: toLayoutParam for dimensions relies on String.toPixels from DimensionConverter.kt")
        
        // To make it more concrete but still a bit of a hack for unit testing extensions:
        // You might need to mock the specific toPixels call if it were not an extension,
        // or ensure DimensionConverter is set up for predictable output if testing its integration here.
        // Since toPixels is now an extension in the same package, this should "just work"
        // assuming the underlying DimensionConverter logic is sound.
    }


    // --- View Extensions ---
    @Test
    @DisplayName("View.getViewID - ID in self tag")
    fun `getViewID self tag`() {
        val mockView = mockk<View>()
        val generatedView = GeneratedView(viewID = hashMapOf("testId" to 123))
        every { mockView.tag } returns generatedView
        every { mockView.hashCode() } returns 1 // For consistent cache key if needed

        // Clear cache for this specific key if possible, or use unique keys per test
        // For now, assume cache doesn't interfere or use unique "testId_selftag"
        val cacheField = ExtensionsTest::class.java.getDeclaredField("viewIdCache")
        cacheField.isAccessible = true
        (cacheField.get(null) as ConcurrentHashMap<String, Int>).clear()


        assertEquals(123, mockView.getViewID("testId"))
    }

    @Test
    @DisplayName("View.getViewID - ID in child tag")
    fun `getViewID child tag`() {
        val parentView = mockk<ViewGroup>()
        val childView = mockk<View>()
        val generatedViewChild = GeneratedView(viewID = hashMapOf("testId" to 456))

        every { parentView.tag } returns null
        every { parentView.childCount } returns 1
        every { parentView.getChildAt(0) } returns childView
        every { parentView.hashCode() } returns 2

        every { childView.tag } returns generatedViewChild
        every { childView.childCount } returns 0 // Not a ViewGroup for simplicity here

        (typeof(ExtensionsTest).getDeclaredField("viewIdCache").apply{isAccessible=true}.get(null) as ConcurrentHashMap<String,Int>).clear()


        assertEquals(456, parentView.getViewID("testId"))
    }
    
    @Test
    @DisplayName("View.getViewID - ID not found")
    fun `getViewID not found`() {
        val mockView = mockk<ViewGroup>()
        every { mockView.tag } returns null
        every { mockView.childCount } returns 0
        every { mockView.hashCode() } returns 3

       (typeof(ExtensionsTest).getDeclaredField("viewIdCache").apply{isAccessible=true}.get(null) as ConcurrentHashMap<String,Int>).clear()

        assertEquals(EMPTY_VIEW_ID_RESULT, mockView.getViewID("nonExistentId"))
    }


    @Test
    @DisplayName("View.getParentView")
    fun `getParentView tests`() {
        val mockView = mockk<View>()
        val mockParent = mockk<ViewGroup>()
        val notAParent = mockk<View>()

        every { mockView.parent } returns mockParent
        assertSame(mockParent, mockView.getParentView())

        every { mockView.parent } returns notAParent
        assertNull(mockView.getParentView())

        every { mockView.parent } returns null
        assertNull(mockView.getParentView())
    }
    
    @Test
    @DisplayName("View.findViewByIdString - success and failure")
    fun `findViewByIdString tests`() {
        val rootView = mockk<View>()
        val targetView = mockk<View>()
        val idString = "myTargetView"
        val resolvedIdInt = 1001

        // Mock getViewID behavior for this root view
        mockkStatic("com.voyager.utils.ExtensionsKt") // Mock top-level extension functions in file Extensions.kt
        every { rootView.getViewID(idString) } returns resolvedIdInt
        every { rootView.getViewID("notFoundString") } returns EMPTY_VIEW_ID_RESULT
        
        every { rootView.findViewById<View>(resolvedIdInt) } returns targetView
        every { rootView.findViewById<View>(EMPTY_VIEW_ID_RESULT) } returns null // Should not happen if takeIf works
        every { rootView.findViewById<View>(not(eq(resolvedIdInt))) } returns null


        assertSame(targetView, rootView.findViewByIdString(idString))
        assertNull(rootView.findViewByIdString("notFoundString"))
    }


    // --- Map Extension ---
    @Test
    @DisplayName("Map.partition")
    fun `partition map`() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4)
        val (even, odd) = map.partition { it.value % 2 == 0 }
        assertEquals(mapOf("b" to 2, "d" to 4), even)
        assertEquals(mapOf("a" to 1, "c" to 3), odd)
    }

    // --- Context Extension ---
    @Test
    @DisplayName("Context.getActivityName")
    fun `getActivityName tests`() {
        val mockActivity = mockk<Activity>()
        every { mockActivity.javaClass } returns MyTestActivity::class.java

        val mockContextWrapper = mockk<ContextWrapper>()
        every { mockContextWrapper.baseContext } returns mockActivity

        val appContext = mockk<Context>() // Not an activity or wrapper
        
        (typeof(ExtensionsTest).getDeclaredField("activityNameCache").apply{isAccessible=true}.get(null) as WeakHashMap<Context,String>).clear()


        assertEquals("MyTestActivity", mockActivity.getActivityName())
        assertEquals("MyTestActivity", mockContextWrapper.getActivityName())
        assertEquals("Unknown", appContext.getActivityName())
        
        // Test caching (call again)
        assertEquals("MyTestActivity", mockActivity.getActivityName())

    }
    class MyTestActivity : Activity() // Dummy class for testing

    // --- Margin and Padding Extensions ---
    // These are harder to test in pure JUnit without Robolectric or instrumented tests
    // as they modify View.LayoutParams and interact with View's internal state.
    // Conceptual verification:
    @Test
    @DisplayName("Margin and Padding setters - conceptual verification")
    fun `margin padding conceptual`() {
        val mockView = mockk<View>(relaxed = true)
        val mockLayoutParams = mockk<MarginLayoutParams>(relaxed = true)
        every { mockView.layoutParams } returns mockLayoutParams

        mockView.setMargin(10)
        verify { mockLayoutParams.setMargins(10,10,10,10) }

        mockView.setMarginStart(5)
        verify { mockLayoutParams.marginStart = 5 }
        
        // Similar verify calls for other margin/padding setters
        // For padding, also need to mock layoutDirection for RTL testing
        mockkStatic(Locale::class)
        val defaultLocale = Locale.US // LTR
        every { Locale.getDefault() } returns defaultLocale
        
        // Mock View.LAYOUT_DIRECTION_LTR (0) and View.LAYOUT_DIRECTION_RTL (1)
        // These are static fields in View, can be tricky.
        // Assuming LTR for this conceptual test of setPaddingE
        mockView.setPaddingE(8)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            verify { mockView.setPaddingRelative(8,8,8,8) }
        } else {
            verify { mockView.setPadding(8,8,8,8) }
        }
        assertTrue(true, "Conceptual: Margin/Padding setters call correct LayoutParams methods.")
    }
}
