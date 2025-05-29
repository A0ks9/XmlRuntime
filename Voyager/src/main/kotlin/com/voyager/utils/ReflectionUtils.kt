/**
 * `ReflectionUtils` is an internal utility object providing helper functions for
 * reflection-based event handling within the Voyager framework.
 *
 * This object encapsulates the logic for:
 * - Creating `OnClickListener` instances that delegate to methods on a target object via reflection.
 * - Parsing method strings that may include arguments.
 * - Invoking methods using Java reflection with error handling and a fallback mechanism.
 *
 * **Performance Warning:** Methods in this utility rely heavily on reflection, which is
 * significantly slower than direct method calls. It should be used judiciously, especially in
 * performance-sensitive code paths. Consider using interfaces and direct lambda calls for
 * better performance where possible.
 *
 * This utility is intended for use only within the Voyager module.
 *
 * @see com.voyager.utils.Utils.getClickListener
 * @author Abdelrahman Omar (based on original Utils.kt content)
 * @since 1.0.0
 */
package com.voyager.utils

import android.view.View
import android.view.ViewGroup
// GeneratedView is in the same package, so direct import might not be strictly necessary
// but can be added for explicitness if preferred: import com.voyager.utils.GeneratedView
import java.lang.reflect.InvocationTargetException
import java.util.logging.Level
import java.util.logging.Logger
import org.json.JSONArray
import org.json.JSONException

internal object ReflectionUtils {

    private val logger by lazy { Logger.getLogger(ReflectionUtils::class.java.name) }

    /**
     * Private object holding string constants used internally for parsing method arguments from strings.
     */
    private object ReflectionConstants {
        const val QUOT_ENTITY = "&quot;" // HTML entity for quote
        const val QUOTE_CHAR = "\""      // Standard quote character
        const val ARGS_JSON_ARRAY_PREFIX = "[" // For constructing a valid JSON array string
        const val ARGS_SUFFIX_TO_DROP = ")]"   // To remove ")]" from "method(arg1,arg2)]"
        const val ARGS_SEPARATOR_REGEX = "[(]" // To split "methodName(args)"
    }

    /**
     * Creates an [View.OnClickListener] that, when triggered, invokes a specified method
     * on a delegate object. The delegate is retrieved from the `tag` of the [parent] `ViewGroup`
     * (expected to be a [GeneratedView] instance containing the delegate).
     *
     * **Performance Warning:** This method relies on [invokeMethod], which uses reflection.
     *
     * @param parent The parent [ViewGroup] whose tag should hold a [GeneratedView] object
     *               containing the delegate instance. If `null`, the listener will do nothing.
     * @param methodName The name of the method to invoke on the delegate. This name can include
     *                   arguments in parentheses, e.g., "onItemClick(1, 'someValue')".
     * @return An [View.OnClickListener].
     * @see [invokeMethod]
     * @see [View.getGeneratedViewInfo]
     */
    fun getClickListener(parent: ViewGroup?, methodName: String): View.OnClickListener {
        return View.OnClickListener { clickedView ->
            val delegate = parent?.getGeneratedViewInfo()?.delegate
            if (delegate == null) {
                logger.warning("No delegate found for click listener with parent: $parent, methodName: $methodName")
                return@OnClickListener
            }
            invokeMethod(delegate, methodName, withViewArgument = false, viewContext = clickedView)
        }
    }

    /**
     * Data class to hold the result of parsing a method string.
     */
    private data class ParsedMethod(val actualMethodName: String, val arguments: Array<Any>?)

    /**
     * Parses a method string (e.g., "methodName(arg1, 'arg2')") into its name and an array of arguments.
     */
    private fun parseMethodNameAndArguments(methodNameString: String): ParsedMethod {
        if (methodNameString.endsWith(ReflectionConstants.ARGS_SUFFIX_TO_DROP.last())) {
            val parts = methodNameString.split(ReflectionConstants.ARGS_SEPARATOR_REGEX.toRegex(), 2)
            if (parts.size == 2) {
                val actualMethodName = parts[0]
                try {
                    val argString = parts[1].dropLast(1)
                        .replace(ReflectionConstants.QUOT_ENTITY, ReflectionConstants.QUOTE_CHAR)
                    if (argString.isBlank()) {
                        return ParsedMethod(actualMethodName, emptyArray())
                    }
                    val jsonArrayStr = "${ReflectionConstants.ARGS_JSON_ARRAY_PREFIX}$argString${ReflectionConstants.ARGS_SUFFIX_TO_DROP.last()}"
                    val jsonArray = JSONArray(jsonArrayStr)
                    val parsedArgs = Array<Any>(jsonArray.length()) { i ->
                        val item = jsonArray.get(i)
                        if (item == JSONObject.NULL) null else item
                    }
                    return ParsedMethod(actualMethodName, parsedArgs)
                } catch (e: JSONException) {
                    logger.log(Level.SEVERE, "Failed to parse arguments from method string: $methodNameString", e)
                    return ParsedMethod(parts[0], null)
                }
            }
        }
        return ParsedMethod(methodNameString, null)
    }

    /**
     * Core reflection logic to find and invoke a method on a delegate object.
     */
    private fun performReflectionCall(
        delegate: Any,
        methodName: String,
        arguments: Array<Any>?,
        argumentTypes: Array<Class<*>>?
    ): Any? {
        val method = if (argumentTypes == null) {
            delegate.javaClass.getMethod(methodName)
        } else {
            delegate.javaClass.getMethod(methodName, *argumentTypes)
        }
        return method.invoke(delegate, *(arguments ?: emptyArray()))
    }

    /**
     * Invokes a method on the [delegate] object using reflection.
     * (Detailed KDoc moved from Utils.kt, including performance warning)
     */
    fun invokeMethod(
        delegate: Any?,
        methodNameString: String,
        withViewArgument: Boolean,
        viewContext: View?
    ) {
        if (delegate == null) {
            logger.warning("Delegate is null, cannot invoke method: $methodNameString")
            return
        }

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
                    logger.log(
                        Level.WARNING,
                        "Reflection call failed for $parsedMethodName on ${delegate.javaClass.name} with args: ${finalArgs?.contentToString()}",
                        e
                    )
                    if (!withViewArgument && parsedArgs == null && viewContext != null) {
                        logger.info("Retrying $parsedMethodName on ${delegate.javaClass.name} by adding View argument.")
                        invokeMethod(delegate, parsedMethodName, withViewArgument = true, viewContext = viewContext)
                    }
                }
                else -> {
                    logger.log(Level.SEVERE, "Unexpected exception during reflection call for $parsedMethodName on ${delegate.javaClass.name}", e)
                }
            }
        }
    }
}
