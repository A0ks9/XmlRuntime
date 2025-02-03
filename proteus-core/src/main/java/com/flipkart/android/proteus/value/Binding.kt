package com.flipkart.android.proteus.value

import android.content.Context
import android.util.Log
import androidx.collection.LruCache
import com.flipkart.android.proteus.Function
import com.flipkart.android.proteus.FunctionManager
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.processor.AttributeProcessor
import com.flipkart.android.proteus.toolbox.Result
import com.flipkart.android.proteus.toolbox.Utils
import java.util.StringTokenizer
import java.util.regex.Pattern
import kotlin.Array as array

abstract class Binding : Value() {

    companion object {
        const val BINDING_PREFIX_0 = '@'
        const val BINDING_PREFIX_1 = '{'
        const val BINDING_SUFFIX = '}'
        const val INDEX = "\$index"
        const val ARRAY_DATA_LENGTH_REFERENCE = "\$length"
        const val ARRAY_DATA_LAST_INDEX_REFERENCE = "\$last"
        val BINDING_PATTERN: Pattern =
            Pattern.compile("@\\{fn:(\\S+?)\\(((?:(?<!\\\\)'.*?(?<!\\\\)'|.?)+)\\)\\}|@\\{(.+)\\}")
        val FUNCTION_ARGS_DELIMITER: Pattern = Pattern.compile(",(?=(?:[^']*'[^']*')*[^']*$)")
        const val DATA_PATH_DELIMITERS = ".]"
        const val DELIMITER_OBJECT = '.'
        const val DELIMITER_ARRAY_OPENING = '['
        const val DELIMITER_ARRAY_CLOSING = ']'

        @JvmStatic
        fun isBindingValue(value: String): Boolean =
            value.length > 3 && value[0] == BINDING_PREFIX_0 && value[1] == BINDING_PREFIX_1 && value[value.length - 1] == BINDING_SUFFIX


        @JvmStatic
        fun valueOf(value: String, context: Context, manager: FunctionManager): Binding =
            BINDING_PATTERN.matcher(value).run {
                if (find()) {
                    group(3)?.let { DataBinding.valueOf(it) } ?: FunctionBinding.valueOf(
                        group(1)!!, group(2)!!, context, manager
                    )
                } else {
                    throw IllegalArgumentException("$value is not a binding")
                }
            }
    }

    abstract fun evaluate(context: Context, data: Value, index: Int): Value

    abstract override fun toString(): String

    override fun copy(): Binding = this

    class DataBinding private constructor(val tokens: array<Token>) : Binding() {

        companion object {
            private val DATA_BINDING_CACHE = LruCache<String, DataBinding>(64)

            @JvmStatic
            fun valueOf(path: String): DataBinding = DATA_BINDING_CACHE[path] ?: run {
                val tokens = mutableListOf<Token>()
                StringTokenizer(path, DATA_PATH_DELIMITERS, true).apply {
                    while (hasMoreTokens()) {
                        when (val token = nextToken()) {
                            DELIMITER_OBJECT.toString() -> continue
                            DELIMITER_ARRAY_CLOSING.toString() -> correctPreviousToken(tokens)
                            else -> tokens.add(Token(token, false, false))
                        }
                    }
                }
                DataBinding(tokens.toTypedArray()).also { DATA_BINDING_CACHE.put(path, it) }
            }

            private fun assign(
                tokens: array<Token>, value: Value, data: Value, dataIndex: Int
            ) {
                var current = data
                var index = dataIndex

                for (i in 0 until tokens.size - 1) {
                    val token = tokens[i]
                    if (token.isArrayIndex) {
                        try {
                            index = getArrayIndex(token.value, dataIndex)
                        } catch (e: NumberFormatException) {
                            return
                        }
                        current = getArrayItem(current.asArray, index, token.isArray)
                    } else if (token.isArray) {
                        current = getArray(current, token.value, index)
                    } else {
                        current = getObject(current, token, index)
                    }
                }

                val token = tokens.last()

                if (token.isArrayIndex) {
                    val arrayIndex = getArrayIndex(token.value, dataIndex) // Capture the index here

                    getArrayItem(
                        current.asArray, arrayIndex, false
                    ).run {
                        current.asArray.removeAt(arrayIndex) // Use the captured index
                        current.asArray.add(arrayIndex, value) // Use the captured index
                    }
                } else {
                    current.asObject[token.value] = value
                }
            }

            private fun getObject(parent: Value, token: Token, index: Int): Value {
                return when (parent) {
                    is Array -> {
                        parent.getOrNull(index)?.asObject ?: ObjectValue().apply {
                            parent.asArray.removeAt(index)
                            parent.asArray.add(index, this)
                        }
                    }

                    is ObjectValue -> {
                        parent[token.value]?.asObject ?: ObjectValue().apply {
                            parent.asObject[token.value] = this
                        }
                    }

                    else -> {
                        throw IllegalArgumentException("parent type is not supported: $parent")
                    }
                }
            }

            private fun getArray(parent: Value, token: String, index: Int): Array {
                return when (parent) {
                    is Array -> parent.getOrNull(index)?.asArray ?: Array().apply {
                        parent.asArray.removeAt(index)
                        parent.asArray.add(index, this)
                    }

                    else -> parent.asObject[token]?.asArray ?: Array().apply {
                        parent.asObject[token] = this
                    }
                }
            }

            private fun getArrayItem(array: Array, index: Int, isArray: Boolean): Value =
                (0..index).fold(array) { acc, i ->
                    if (acc.size() <= i) {
                        acc.apply { add(if (isArray) Array() else ObjectValue()) }
                    } else {
                        acc
                    }
                }[index]

            private fun getArrayIndex(token: String, dataIndex: Int): Int =
                if (INDEX == token) dataIndex else token.toInt()

            private fun correctPreviousToken(tokens: MutableList<Token>) {
                tokens.removeAt(tokens.size - 1).apply {
                    val index = value.indexOf(DELIMITER_ARRAY_OPENING)
                    val prefix = value.substring(0, index)
                    val suffix = value.substring(index + 1)
                    if (prefix.isEmpty()) {
                        tokens.add(Token(value, true, false))
                    } else {
                        tokens.add(Token(prefix, true, false))
                    }
                    tokens.add(Token(suffix, false, true))
                }
            }


            private fun resolve(tokens: array<Token>, data: Value?, index: Int): Result {
                var elementToReturn = data
                for (segment in tokens) {
                    if (elementToReturn?.isNull == true) {
                        return Result.NULL_EXCEPTION
                    }

                    if (elementToReturn == null) {
                        return Result.NO_SUCH_DATA_PATH_EXCEPTION
                    }

                    elementToReturn = when (segment.value) {
                        "" -> elementToReturn
                        INDEX -> if (elementToReturn.isArray) {
                            elementToReturn.asArray.getOrNull(index)
                                ?: return Result.NO_SUCH_DATA_PATH_EXCEPTION
                        } else return Result.INVALID_DATA_PATH_EXCEPTION

                        ARRAY_DATA_LENGTH_REFERENCE -> if (elementToReturn.isArray) Primitive(
                            elementToReturn.asArray.size()
                        ) else return Result.INVALID_DATA_PATH_EXCEPTION

                        ARRAY_DATA_LAST_INDEX_REFERENCE -> if (elementToReturn.isArray) {
                            elementToReturn.asArray.takeIf { it.size() > 0 }?.lastOrNull()
                                ?: return Result.NO_SUCH_DATA_PATH_EXCEPTION
                        } else return Result.INVALID_DATA_PATH_EXCEPTION

                        else -> {
                            if (elementToReturn.isArray) {
                                try {
                                    elementToReturn.asArray.getOrNull(segment.value.toInt())
                                        ?: return Result.NO_SUCH_DATA_PATH_EXCEPTION

                                } catch (e: NumberFormatException) {
                                    return Result.INVALID_DATA_PATH_EXCEPTION
                                }
                            } else if (elementToReturn.isObject) {
                                elementToReturn.asObject.getOrNull(segment.value)
                                    ?: return Result.NO_SUCH_DATA_PATH_EXCEPTION
                            } else if (elementToReturn.isPrimitive) {
                                return Result.INVALID_DATA_PATH_EXCEPTION
                            } else {
                                return Result.NO_SUCH_DATA_PATH_EXCEPTION
                            }
                        }
                    }
                }
                return if (elementToReturn?.isNull == true) Result.NULL_EXCEPTION else Result.success(
                    elementToReturn!!
                )
            }
        }

        override fun evaluate(context: Context, data: Value, index: Int): Value =
            resolve(tokens, data, index).let { if (it.isSuccess()) it.value else Null }

        override fun toString(): String = buildString {
            append(BINDING_PREFIX_0)
            append(BINDING_PREFIX_1)
            append(Utils.join(tokens.map { it.value }.toTypedArray(), DELIMITER_OBJECT.toString()))
            append(BINDING_SUFFIX)
        }

        fun assign(value: Value, data: Value, index: Int) = assign(tokens, value, data, index)
    }

    class FunctionBinding(
        val function: Function, val arguments: array<Value>?
    ) : Binding() {
        companion object {
            @JvmStatic
            fun valueOf(
                name: String, args: String, context: Context, manager: FunctionManager
            ): FunctionBinding {
                val function = manager[name]
                val arguments = FUNCTION_ARGS_DELIMITER.split(args).map {
                    it.trim().let { token ->
                        if (token.isNotEmpty() && token[0] == '\'') {
                            Primitive(token.substring(1, token.length - 1))
                        } else {
                            AttributeProcessor.staticPreCompile(Primitive(token), context, manager)
                                ?: Primitive(token)
                        }
                    }
                }.toTypedArray()
                return FunctionBinding(function, arguments)
            }

            private fun resolve(
                context: Context, inArgs: array<Value>?, data: Value, index: Int
            ): array<Value> {
                return inArgs?.map { AttributeProcessor.evaluate(context, it, data, index)!! }
                    ?.toTypedArray() ?: emptyArray()
            }
        }

        override fun evaluate(context: Context, data: Value, index: Int): Value =
            resolve(context, arguments, data, index).let {
                try {
                    this.function.call(context, data, index, *it)
                } catch (e: Exception) {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(Utils.LIB_NAME, e.message, e)
                    }
                    Null
                }
            }

        override fun toString(): String = String.format("@{fn:%s(%s)}",
            function.getName(),
            arguments?.let { Utils.join(it, ",", Utils.STYLE_SINGLE) })
    }

    data class Token(
        val value: String, val isArray: Boolean, val isArrayIndex: Boolean
    ) {
        companion object {
            fun getValues(tokens: array<Token>): array<String> =
                array(tokens.size) { index -> tokens[index].value }
        }
    }
}