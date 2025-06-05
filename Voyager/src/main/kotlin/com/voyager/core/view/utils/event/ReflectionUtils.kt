package com.voyager.core.view.utils.event

import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.view.utils.ViewExtensions.getGeneratedViewInfo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap

/**
 * Utility object for handling reflection-based operations in the Voyager framework.
 * 
 * Key features:
 * - Method invocation through reflection
 * - Click listener generation
 * - JSON argument parsing
 * - Thread-safe operations
 * - Comprehensive error handling
 * 
 * Performance optimizations:
 * - Method caching
 * - Efficient JSON parsing
 * - Minimal object creation
 * - Fast reflection operations
 * 
 * Best practices:
 * - Use reflection sparingly
 * - Handle reflection errors gracefully
 * - Implement proper error handling
 * - Use appropriate logging
 * 
 * Example usage:
 * ```kotlin
 * // Create a click listener that calls a method
 * val clickListener = ReflectionUtils.getClickListener(parentView, "onSubmitClick")
 * button.setOnClickListener(clickListener)
 * 
 * // Invoke a method with arguments
 * ReflectionUtils.invokeMethod(delegate, "processData(arg1,arg2)", false, null)
 * ```
 */
internal object ReflectionUtils {

    private val logger = LoggerFactory.getLogger(ReflectionUtils::class.java.simpleName)

    // Cache for method lookups to improve performance
    private val methodCache = ConcurrentHashMap<String, java.lang.reflect.Method>()

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
     * Performance considerations:
     * - Reflection overhead on click
     * - Efficient delegate lookup
     * - Minimal object creation
     * - Thread-safe operations
     * 
     * Error handling:
     * - Safe delegate access
     * - Graceful fallback for missing methods
     * - Proper logging
     *
     * @param parent The parent ViewGroup containing the delegate
     * @param methodName The method name to call, optionally with arguments
     * @return An OnClickListener that calls the specified method
     */
    fun getClickListener(parent: ViewGroup?, methodName: String): OnClickListener {
        return OnClickListener { clickedView ->
            try {
                val delegate = parent?.getGeneratedViewInfo()?.delegate
                if (delegate == null) {
                    logger.warn(
                        "getClickListener",
                        "Delegate is null for method $methodName"
                    )
                    return@OnClickListener
                }
                invokeMethod(delegate, methodName, withViewArgument = false, viewContext = clickedView)
            } catch (e: Exception) {
                logger.error(
                    "getClickListener",
                    "Failed to invoke method $methodName: ${e.message}",
                    e
                )
            }
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
     * Performance considerations:
     * - Efficient string parsing
     * - JSON array construction
     * - Error handling
     * - Minimal object creation
     * 
     * Error handling:
     * - Safe string parsing
     * - Graceful JSON handling
     * - Proper logging
     *
     * @param methodNameString The method string to parse
     * @return A ParsedMethod containing the method name and arguments
     */
    private fun parseMethodNameAndArguments(methodNameString: String): ParsedMethod {
        try {
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
                        logger.error(
                            "parseMethodNameAndArguments",
                            "Failed to parse JSON arguments: ${e.message}",
                            e
                        )
                        return ParsedMethod(parts[0], null)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(
                "parseMethodNameAndArguments",
                "Failed to parse method string '$methodNameString': ${e.message}",
                e
            )
        }
        return ParsedMethod(methodNameString, null)
    }

    /**
     * Core reflection logic to find and invoke a method.
     *
     * Performance considerations:
     * - Reflection overhead
     * - Method lookup caching
     * - Argument type resolution
     * - Error handling
     * 
     * Error handling:
     * - Safe method lookup
     * - Graceful argument handling
     * - Proper logging
     *
     * @param delegate The object containing the method
     * @param methodName The name of the method to call
     * @param arguments The arguments to pass to the method
     * @param argumentTypes The types of the arguments
     * @return The result of the method call
     * @throws NoSuchMethodException if the method is not found
     * @throws IllegalAccessException if the method is not accessible
     * @throws InvocationTargetException if the method throws an exception
     */
    private fun performReflectionCall(
        delegate: Any,
        methodName: String,
        arguments: Array<Any>?,
        argumentTypes: Array<Class<*>>?,
    ): Any? {
        try {
            val cacheKey = "${delegate.javaClass.name}.$methodName"
            val method = methodCache.getOrPut(cacheKey) {
                if (argumentTypes == null) {
                    delegate.javaClass.getMethod(methodName)
                } else {
                    delegate.javaClass.getMethod(methodName, *argumentTypes)
                }
            }
            return method.invoke(delegate, *(arguments ?: emptyArray()))
        } catch (e: Exception) {
            logger.error(
                "performReflectionCall",
                "Failed to invoke method $methodName: ${e.message}",
                e
            )
            throw e
        }
    }

    /**
     * Invokes a method on a delegate object using reflection.
     *
     * Performance considerations:
     * - Reflection overhead
     * - Method lookup and caching
     * - Argument parsing
     * - Error handling and retry
     * 
     * Error handling:
     * - Safe method invocation
     * - Graceful argument handling
     * - Proper logging
     * - Retry with view argument
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
        if (delegate == null) {
            logger.warn(
                "invokeMethod",
                "Delegate is null for method $methodNameString"
            )
            return
        }

        try {
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
                            logger.info(
                                "invokeMethod",
                                "Retrying method $parsedMethodName with view argument"
                            )
                            invokeMethod(
                                delegate,
                                parsedMethodName,
                                withViewArgument = true,
                                viewContext = viewContext
                            )
                        } else {
                            throw e
                        }
                    }
                    else -> throw e
                }
            }
        } catch (e: Exception) {
            logger.error(
                "invokeMethod",
                "Failed to invoke method $methodNameString: ${e.message}",
                e
            )
        }
    }
}