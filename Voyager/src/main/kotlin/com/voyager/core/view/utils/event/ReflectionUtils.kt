package com.voyager.core.view.utils.event

import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.voyager.utils.Utils.getGeneratedViewInfo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException


internal object ReflectionUtils {
    /**
     * Private object holding string constants used internally for parsing method arguments.
     */
    private object ReflectionConstants {
        const val QUOT_ENTITY = "&quot;" // HTML entity for quote
        const val QUOTE_CHAR = "\""      // Standard quote character
        const val ARGS_JSON_ARRAY_PREFIX = "[" // For constructing a valid JSON array string
        const val ARGS_SUFFIX_TO_DROP = ")]"   // To remove ")]" from "method(arg1,arg2)]"
        const val ARGS_SEPARATOR_REGEX = "[(]" // To split "methodName(args)"
    }

    /**
     * Creates an [View.OnClickListener] that delegates to a specified method.
     *
     * Performance Considerations:
     * - Reflection overhead on click
     * - Efficient delegate lookup
     * - Minimal object creation
     * - Thread-safe operations
     *
     * @param parent The parent ViewGroup containing the delegate
     * @param methodName The method name to call, optionally with arguments
     * @return An OnClickListener that calls the specified method
     */
    fun getClickListener(parent: ViewGroup?, methodName: String): OnClickListener {
        return OnClickListener { clickedView ->
            val delegate = parent?.getGeneratedViewInfo()?.delegate
            if (delegate == null) return@OnClickListener
            invokeMethod(delegate, methodName, withViewArgument = false, viewContext = clickedView)
        }
    }

    /**
     * Data class to hold the result of parsing a method string.
     */
    private data class ParsedMethod(val actualMethodName: String, val arguments: Array<Any>?) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ParsedMethod

            if (actualMethodName != other.actualMethodName) return false
            if (!arguments.contentEquals(other.arguments)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = actualMethodName.hashCode()
            result = 31 * result + (arguments?.contentHashCode() ?: 0)
            return result
        }
    }

    /**
     * Parses a method string into its name and arguments.
     *
     * Performance Considerations:
     * - Efficient string parsing
     * - JSON array construction
     * - Error handling
     * - Minimal object creation
     *
     * @param methodNameString The method string to parse
     * @return A ParsedMethod containing the method name and arguments
     */
    private fun parseMethodNameAndArguments(methodNameString: String): ParsedMethod {
        if (methodNameString.endsWith(ReflectionConstants.ARGS_SUFFIX_TO_DROP.last())) {
            val parts =
                methodNameString.split(ReflectionConstants.ARGS_SEPARATOR_REGEX.toRegex(), 2)
            if (parts.size == 2) {
                val actualMethodName = parts[0]
                try {
                    val argString = parts[1].dropLast(1)
                        .replace(ReflectionConstants.QUOT_ENTITY, ReflectionConstants.QUOTE_CHAR)
                    if (argString.isBlank()) {
                        return ParsedMethod(actualMethodName, emptyArray())
                    }
                    val jsonArrayStr =
                        "${ReflectionConstants.ARGS_JSON_ARRAY_PREFIX}$argString${ReflectionConstants.ARGS_SUFFIX_TO_DROP.last()}"
                    val jsonArray = JSONArray(jsonArrayStr)
                    val parsedArgs = Array<Any>(jsonArray.length()) { i ->
                        val item = jsonArray.get(i)
                        (if (item == JSONObject.NULL) null else item) as Any
                    }
                    return ParsedMethod(actualMethodName, parsedArgs)
                } catch (e: JSONException) {
                    return ParsedMethod(parts[0], null)
                }
            }
        }
        return ParsedMethod(methodNameString, null)
    }

    /**
     * Core reflection logic to find and invoke a method.
     *
     * Performance Considerations:
     * - Reflection overhead
     * - Method lookup caching
     * - Argument type resolution
     * - Error handling
     *
     * @param delegate The object containing the method
     * @param methodName The name of the method to call
     * @param arguments The arguments to pass to the method
     * @param argumentTypes The types of the arguments
     * @return The result of the method call
     */
    private fun performReflectionCall(
        delegate: Any,
        methodName: String,
        arguments: Array<Any>?,
        argumentTypes: Array<Class<*>>?,
    ): Any? {
        val method = if (argumentTypes == null) {
            delegate.javaClass.getMethod(methodName)
        } else {
            delegate.javaClass.getMethod(methodName, *argumentTypes)
        }
        return method.invoke(delegate, *(arguments ?: emptyArray()))
    }

    /**
     * Invokes a method on a delegate object using reflection.
     *
     * Performance Considerations:
     * - Reflection overhead
     * - Method lookup and caching
     * - Argument parsing
     * - Error handling and retry
     *
     * @param delegate The object containing the method
     * @param methodNameString The method name with optional arguments
     * @param withViewArgument Whether to include the view as an argument
     * @param viewContext The view context to pass as an argument
     */
    fun invokeMethod(
        delegate: Any?,
        methodNameString: String,
        withViewArgument: Boolean,
        viewContext: View?,
    ) {
        if (delegate == null) return

        val (parsedMethodName, parsedArgs) = parseMethodNameAndArguments(methodNameString)

        var finalArgs = parsedArgs
        if (withViewArgument && viewContext != null) {
            finalArgs = if (parsedArgs == null) arrayOf(viewContext) else parsedArgs + viewContext
        }

        val finalArgTypes = finalArgs?.map { arg ->
            when (arg) {
                is Int -> Int::class.java
                else -> arg.javaClass
            }
        }?.toTypedArray()

        try {
            performReflectionCall(delegate, parsedMethodName, finalArgs, finalArgTypes)
        } catch (e: Exception) {
            when (e) {
                is NoSuchMethodException, is IllegalAccessException, is InvocationTargetException -> {
                    if (!withViewArgument && parsedArgs == null && viewContext != null) {
                        invokeMethod(
                            delegate,
                            parsedMethodName,
                            withViewArgument = true,
                            viewContext = viewContext
                        )
                    }
                }

                else -> {
                }
            }
        }
    }
}