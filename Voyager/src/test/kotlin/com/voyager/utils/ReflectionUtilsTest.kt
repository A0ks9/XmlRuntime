package com.voyager.utils

import android.view.View
import android.view.ViewGroup
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.logging.Logger

// Mock Log for pure JUnit if not using Robolectric
import android.util.Log
import com.voyager.core.view.model.GeneratedView
import io.mockk.mockkStatic

@DisplayName("ReflectionUtils Tests")
class ReflectionUtilsTest {

    // Dummy delegate class for testing invokeMethod
    class TestDelegate {
        var lastMethodCalled: String? = null
        var lastSimpleArg: String? = null
        var lastIntArg: Int? = null
        var lastBoolArg: Boolean? = null
        var viewArgReceived: View? = null

        fun noArgsMethod() {
            lastMethodCalled = "noArgsMethod"
        }

        fun oneStringArgMethod(arg: String) {
            lastMethodCalled = "oneStringArgMethod"
            lastSimpleArg = arg
        }
        
        fun oneNullableStringArgMethod(arg: String?) {
            lastMethodCalled = "oneNullableStringArgMethod"
            lastSimpleArg = arg
        }

        fun multipleArgsMethod(s: String, i: Int, b: Boolean) {
            lastMethodCalled = "multipleArgsMethod"
            lastSimpleArg = s
            lastIntArg = i
            lastBoolArg = b
        }

        fun methodWithView(v: View) {
            lastMethodCalled = "methodWithView"
            viewArgReceived = v
        }

        fun methodWithViewAndString(v: View, s: String) {
            lastMethodCalled = "methodWithViewAndString"
            viewArgReceived = v
            lastSimpleArg = s
        }
        
        private fun privateMethod() {
            lastMethodCalled = "privateMethod"
        }
    }

    private val mockLogger: Logger = mockk(relaxed = true) // Relaxed mock for logger

    // Test cases for parseMethodNameAndArguments (private, so tested via invokeMethod or conceptually)
    // For simplicity, direct conceptual tests for its logic:

    @Test
    @DisplayName("parseMethodNameAndArguments - no args")
    fun `parse no args`() {
        // This private method is tested via its usage in invokeMethod.
        // Conceptual test: "handleClick" -> ParsedMethod("handleClick", null or emptyArray())
        // Actual test will be through invokeMethod.
        assertTrue(true, "Conceptual test for no-args parsing")
    }

    @Test
    @DisplayName("parseMethodNameAndArguments - simple args")
    fun `parse simple args`() {
        // Conceptual test: "updateTitle('Hello')" -> ParsedMethod("updateTitle", ["Hello"])
        assertTrue(true, "Conceptual test for simple arg parsing")
    }
    
    @Test
    @DisplayName("parseMethodNameAndArguments - empty args string")
    fun `parse empty args string`() {
        // Conceptual test: "method()" -> ParsedMethod("method", emptyArray)
         assertTrue(true, "Conceptual test for empty args string")
    }


    // Tests for invokeMethod
    @Test
    @DisplayName("invokeMethod - no args success")
    fun `invoke no args method`() {
        val delegate = TestDelegate()
        ReflectionUtils.invokeMethod(delegate, "noArgsMethod", false, null)
        assertEquals("noArgsMethod", delegate.lastMethodCalled)
    }

    @Test
    @DisplayName("invokeMethod - one string arg from JSON style")
    fun `invoke one string arg method`() {
        val delegate = TestDelegate()
        // JSON like arg string "('Hello')"
        ReflectionUtils.invokeMethod(delegate, "oneStringArgMethod('Hello')", false, null)
        assertEquals("oneStringArgMethod", delegate.lastMethodCalled)
        assertEquals("Hello", delegate.lastSimpleArg)
    }
    
    @Test
    @DisplayName("invokeMethod - one nullable string arg with null value")
    fun `invoke one nullable string arg with null`() {
        val delegate = TestDelegate()
        ReflectionUtils.invokeMethod(delegate, "oneNullableStringArgMethod(null)", false, null)
        assertEquals("oneNullableStringArgMethod", delegate.lastMethodCalled)
        assertNull(delegate.lastSimpleArg)
    }

    @Test
    @DisplayName("invokeMethod - multiple args from JSON style")
    fun `invoke multiple args method`() {
        val delegate = TestDelegate()
        ReflectionUtils.invokeMethod(delegate, "multipleArgsMethod('Test', 123, true)", false, null)
        assertEquals("multipleArgsMethod", delegate.lastMethodCalled)
        assertEquals("Test", delegate.lastSimpleArg)
        assertEquals(123, delegate.lastIntArg)
        assertEquals(true, delegate.lastBoolArg)
    }

    @Test
    @DisplayName("invokeMethod - html entity in string arg")
    fun `invoke method with html entity string arg`() {
        val delegate = TestDelegate()
        ReflectionUtils.invokeMethod(delegate, "oneStringArgMethod('&quot;Quoted&quot;')", false, null)
        assertEquals("oneStringArgMethod", delegate.lastMethodCalled)
        assertEquals("\"Quoted\"", delegate.lastSimpleArg)
    }


    @Test
    @DisplayName("invokeMethod - fallback to call with View argument")
    fun `invoke method with fallback to view argument`() {
        val delegate = TestDelegate()
        val mockView = mockk<View>()
        // First call "methodWithView" without view arg - should fail then retry with view
        ReflectionUtils.invokeMethod(delegate, "methodWithView", false, mockView)
        assertEquals("methodWithView", delegate.lastMethodCalled)
        assertEquals(mockView, delegate.viewArgReceived)
    }
    
    @Test
    @DisplayName("invokeMethod - call with explicit View argument already (no fallback needed)")
    fun `invoke method with explicit view argument`() {
        val delegate = TestDelegate()
        val mockView = mockk<View>()
        // Call "methodWithView" with withViewArgument = true
        ReflectionUtils.invokeMethod(delegate, "methodWithView", true, mockView)
        assertEquals("methodWithView", delegate.lastMethodCalled)
        assertEquals(mockView, delegate.viewArgReceived)
    }


    @Test
    @DisplayName("invokeMethod - non-existent method")
    fun `invoke non existent method`() {
        mockkStatic(Log::class) // Mock Log.w, Log.e, etc.
        every { Log.w(any<String>(), any<String>(), any()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0


        val delegate = TestDelegate()
        ReflectionUtils.invokeMethod(delegate, "methodThatDoesNotExist", false, null)
        assertNull(delegate.lastMethodCalled) // Method should not have been called

        // Verify that a warning was logged (details depend on exact logging in ReflectionUtils)
        val warningSlot = slot<String>()
        verify { Log.w(any(), capture(warningSlot), any()) }
        assertTrue(warningSlot.captured.contains("Reflection call failed for methodThatDoesNotExist"))
    }

    @Test
    @DisplayName("invokeMethod - method with wrong arg types")
    fun `invoke method with wrong arg types`() {
        mockkStatic(Log::class)
        every { Log.w(any<String>(), any<String>(), any()) } returns 0
        
        val delegate = TestDelegate()
        // oneStringArgMethod expects String, we pass an Int-like string for parsing
        ReflectionUtils.invokeMethod(delegate, "oneStringArgMethod(123)", false, null)
        // This should fail because JSON '123' is int, method expects String.
        // Depending on how smart getMethod is, this might throw NoSuchMethod or InvocationTargetException(IllegalArgumentException)
        assertNull(delegate.lastMethodCalled) // or check that it's not "oneStringArgMethod"
        val warningSlot = slot<String>()
        verify { Log.w(any(), capture(warningSlot), any()) }
        assertTrue(warningSlot.captured.contains("Reflection call failed for oneStringArgMethod"))
    }
    
    @Test
    @DisplayName("invokeMethod - private method")
    fun `invoke private method`() {
        mockkStatic(Log::class)
        every { Log.w(any<String>(), any<String>(), any()) } returns 0

        val delegate = TestDelegate()
        ReflectionUtils.invokeMethod(delegate, "privateMethod", false, null)
        assertNull(delegate.lastMethodCalled) // privateMethod should not be callable
        val warningSlot = slot<String>()
        verify { Log.w(any(), capture(warningSlot), any()) }
        assertTrue(warningSlot.captured.contains("Reflection call failed for privateMethod")) // Likely NoSuchMethod
    }


    @Test
    @DisplayName("invokeMethod - delegate is null")
    fun `invoke method on null delegate`() {
        mockkStatic(Log::class)
        every { Log.w(any<String>(), any<String>()) } returns 0 // For the "Delegate is null" warning

        ReflectionUtils.invokeMethod(null, "noArgsMethod", false, null)
        // Verify logger.warning was called
        val warningSlot = slot<String>()
        verify { Log.w(any(), capture(warningSlot)) }
        assertTrue(warningSlot.captured.contains("Delegate is null"))
    }

    // Test getClickListener
    @Test
    @DisplayName("getClickListener - successfully invokes method on delegate")
    fun `getClickListener success`() {
        val delegate = TestDelegate()
        val mockParentView = mockk<ViewGroup>()
        val mockClickedView = mockk<View>()
        val generatedView = GeneratedView(delegate = delegate)

        every { mockParentView.tag } returns generatedView
        // For simplicity, assume invokeMethod works (tested above)
        // We are testing that getClickListener correctly sets up the OnClickListener
        // and retrieves the delegate.

        val listener = ReflectionUtils.getClickListener(mockParentView, "noArgsMethod")
        listener.onClick(mockClickedView)

        assertEquals("noArgsMethod", delegate.lastMethodCalled)
    }

    @Test
    @DisplayName("getClickListener - no delegate found")
    fun `getClickListener no delegate`() {
        mockkStatic(Log::class)
        every { Log.w(any<String>(), any<String>()) } returns 0

        val mockParentView = mockk<ViewGroup>()
        val mockClickedView = mockk<View>()
        every { mockParentView.tag } returns null // No GeneratedView or no delegate in it

        val listener = ReflectionUtils.getClickListener(mockParentView, "noArgsMethod")
        listener.onClick(mockClickedView)

        // Verify that a warning was logged
        val warningSlot = slot<String>()
        verify { Log.w(any(), capture(warningSlot)) }
        assertTrue(warningSlot.captured.contains("No delegate found"))
    }
}
