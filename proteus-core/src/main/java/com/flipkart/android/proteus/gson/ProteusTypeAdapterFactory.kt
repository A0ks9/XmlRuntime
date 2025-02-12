/*
 * Copyright 2019 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.android.proteus.gson

import android.annotation.SuppressLint
import android.content.Context
import com.flipkart.android.proteus.FunctionManager
import com.flipkart.android.proteus.Proteus
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.processor.AttributeProcessor
import com.flipkart.android.proteus.value.*
import com.google.gson.*
import com.google.gson.internal.JsonReaderInternalAccess
import com.google.gson.internal.LazilyParsedNumber
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import kotlin.Array as array

/**
 * ProteusTypeAdapterFactory creates custom Gson type adapters to convert between JSON
 * and our internal [Value] types (Primitive, Array, ObjectValue, Layout, etc.).
 *
 * This Kotlin version uses concise language features, minimizes unnecessary allocations,
 * and aims for efficiency without impacting UI performance.
 */
class ProteusTypeAdapterFactory(val context: Context) : TypeAdapterFactory {

    // ------------------------------------------------------------------------------------------
    // Companion object for static constants and helper functions.
    // ------------------------------------------------------------------------------------------
    companion object {
        // Holder for the current Proteus instance.
        val PROTEUS_INSTANCE_HOLDER = ProteusInstanceHolder()

        // Delimiters for array serialization.
        const val ARRAYS_DELIMITER = "|"
        const val ARRAY_DELIMITER = ","

        /**
         * Serialize an integer array to a string by joining elements with [ARRAY_DELIMITER].
         */
        fun writeArrayOfInts(array: IntArray): String =
            array.joinToString(separator = ARRAY_DELIMITER)

        /**
         * Serialize a 2D integer array to a string.
         */
        fun writeArrayOfIntArrays(arrays: array<IntArray>): String =
            arrays.joinToString(separator = ARRAYS_DELIMITER) { writeArrayOfInts(it) }

        /**
         * Deserialize a string into an integer array.
         */
        fun readArrayOfInts(string: String): IntArray =
            string.split(ARRAY_DELIMITER).map { it.toInt() }.toIntArray()

        /**
         * Deserialize a string into a 2D integer array.
         */
        fun readArrayOfIntArrays(string: String): array<IntArray> =
            string.split(ARRAYS_DELIMITER).map { readArrayOfInts(it) }.toTypedArray()

        /**
         * Compiles a string into a [Value]. If the string represents a binding (e.g. "@{...}"),
         * it returns a [Binding] value; otherwise, a [Primitive] value.
         */
        fun compileString(context: Context, string: String): Value =
            if (Binding.isBindingValue(string)) Binding.valueOf(
                string, context, PROTEUS_INSTANCE_HOLDER.proteus!!.functions
            )
            else Primitive(string)
    }

    // ------------------------------------------------------------------------------------------
    // TypeAdapters for our internal Value types.
    // ------------------------------------------------------------------------------------------

    /**
     * A TypeAdapter for [Value]. It reads a JSON token and creates the appropriate [Value]
     * instance (Primitive, Array, ObjectValue, or Layout). Writing is not supported here;
     * use [COMPILED_VALUE_TYPE_ADAPTER] instead.
     */
    val VALUE_TYPE_ADAPTER: TypeAdapter<Value> = object : TypeAdapter<Value>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Value?) {
            throw UnsupportedOperationException("Use COMPILED_VALUE_TYPE_ADAPTER instead")
        }

        @Throws(IOException::class)
        override fun read(reader: JsonReader): Value = when (reader.peek()) {
            JsonToken.STRING -> compileString(context, reader.nextString())
            JsonToken.NUMBER -> {
                val number = reader.nextString()
                Primitive(LazilyParsedNumber(number))
            }

            JsonToken.BOOLEAN -> Primitive(reader.nextBoolean())
            JsonToken.NULL -> {
                reader.nextNull()
                Null
            }

            JsonToken.BEGIN_ARRAY -> {
                val array = Array()
                reader.beginArray()
                while (reader.hasNext()) {
                    array.add(read(reader))
                }
                reader.endArray()
                array
            }

            JsonToken.BEGIN_OBJECT -> {
                val obj = ObjectValue()
                reader.beginObject()
                if (reader.hasNext()) {
                    // Read the first property to check for a layout type.
                    val name = reader.nextName()
                    if (ProteusConstants.TYPE == name && reader.peek() == JsonToken.STRING) {
                        val type = reader.nextString()
                        if (PROTEUS_INSTANCE_HOLDER.isLayout(type)) {
                            // Delegate to the layout adapter if type indicates a layout.
                            val layout = LAYOUT_TYPE_ADAPTER.read(
                                type, PROTEUS_INSTANCE_HOLDER.proteus!!, reader
                            )
                            reader.endObject()
                            layout
                        } else {
                            obj[name] = compileString(context, type)
                        }
                    } else {
                        obj[name] = read(reader)
                    }
                }
                // Read remaining object properties.
                while (reader.hasNext()) {
                    obj[reader.nextName()] = read(reader)
                }
                reader.endObject()
                obj
            }

            else -> throw IllegalArgumentException("Unexpected token: ${reader.peek()}")
        }
    }.nullSafe()

    /**
     * Adapter for [Primitive] values.
     */
    val PRIMITIVE_TYPE_ADAPTER: TypeAdapter<Primitive> = object : TypeAdapter<Primitive>() {
        override fun write(out: JsonWriter, value: Primitive?) {
            VALUE_TYPE_ADAPTER.write(out, value)
        }

        override fun read(reader: JsonReader): Primitive? {
            val value = VALUE_TYPE_ADAPTER.read(reader)
            return if (value?.isPrimitive == true) value.asPrimitive else null
        }
    }.nullSafe()

    /**
     * Adapter for [ObjectValue] values.
     */
    val OBJECT_TYPE_ADAPTER: TypeAdapter<ObjectValue> = object : TypeAdapter<ObjectValue>() {
        override fun write(out: JsonWriter, value: ObjectValue?) {
            VALUE_TYPE_ADAPTER.write(out, value)
        }

        override fun read(reader: JsonReader): ObjectValue? {
            val value = VALUE_TYPE_ADAPTER.read(reader)
            return if (value?.isObject == true) value.asObject else null
        }
    }.nullSafe()

    /**
     * Adapter for [Array] values.
     */
    val ARRAY_TYPE_ADAPTER: TypeAdapter<Array> = object : TypeAdapter<Array>() {
        override fun write(out: JsonWriter, value: Array?) {
            VALUE_TYPE_ADAPTER.write(out, value)
        }

        override fun read(reader: JsonReader): Array? {
            val value = VALUE_TYPE_ADAPTER.read(reader)
            return if (value?.isArray == true) value.asArray else null
        }
    }.nullSafe()

    /**
     * Adapter for [Null] values.
     */
    val NULL_TYPE_ADAPTER: TypeAdapter<Null> = object : TypeAdapter<Null>() {
        override fun write(out: JsonWriter, value: Null?) {
            VALUE_TYPE_ADAPTER.write(out, value)
        }

        override fun read(reader: JsonReader): Null? {
            val value = VALUE_TYPE_ADAPTER.read(reader)
            return if (value?.isNull == true) value.asNull else null
        }
    }.nullSafe()

    /**
     * An instance of LayoutTypeAdapter to handle [Layout] values.
     */
    val LAYOUT_TYPE_ADAPTER = LayoutTypeAdapter()

    /**
     * Adapter that compiles a [Value] into its JSON representation.
     *
     * It handles primitives, objects, arrays, and uses a custom adapter for other types.
     */
    val COMPILED_VALUE_TYPE_ADAPTER: TypeAdapter<Value> = object : TypeAdapter<Value>() {
        private val TYPE_KEY = "\$t"
        private val VALUE_KEY = "\$v"

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Value?) {
            when {
                value == null || value.isNull -> out.nullValue()
                value.isPrimitive -> {
                    val prim = value.asPrimitive
                    when {
                        prim.isNumber() -> out.value(prim.asNumber())
                        prim.isBoolean() -> out.value(prim.asBoolean())
                        else -> out.value(prim.asString())
                    }
                }

                value.isObject -> {
                    out.beginObject()
                    for ((key, v) in value.asObject.entrySet()) {
                        out.name(key)
                        write(out, v)
                    }
                    out.endObject()
                }

                value.isArray -> {
                    out.beginArray()
                    for (v in value.asArray) {
                        write(out, v)
                    }
                    out.endArray()
                }

                else -> {
                    // Use a registered custom adapter if available.
                    val adapter = getCustomValueTypeAdapter(value::class.java)
                    out.beginObject()
                    out.name(TYPE_KEY).value(adapter.type.toString())
                    out.name(VALUE_KEY)
                    adapter.writeValue(out, value)
                    out.endObject()
                }
            }
        }

        @SuppressLint("CheckResult")
        @Throws(IOException::class)
        override fun read(reader: JsonReader): Value = when (reader.peek()) {
            JsonToken.STRING -> compileString(context, reader.nextString())
            JsonToken.NUMBER -> {
                val num = reader.nextString()
                Primitive(LazilyParsedNumber(num))
            }

            JsonToken.BOOLEAN -> Primitive(reader.nextBoolean())
            JsonToken.NULL -> {
                reader.nextNull()
                Null
            }

            JsonToken.BEGIN_ARRAY -> {
                val array = Array()
                reader.beginArray()
                while (reader.hasNext()) {
                    array.add(read(reader))
                }
                reader.endArray()
                array
            }

            JsonToken.BEGIN_OBJECT -> {
                val obj = ObjectValue()
                reader.beginObject()
                // Check for custom adapter marker.
                if (reader.hasNext()) {
                    val name = reader.nextName()
                    if (TYPE_KEY == name && reader.peek() == JsonToken.NUMBER) {
                        val typeInt = reader.nextString().toInt()
                        val adapter = getCustomValueTypeAdapter(typeInt)
                        reader.nextName() // move to the VALUE_KEY
                        val customValue = adapter.read(reader)
                        reader.endObject()
                        customValue
                    } else {
                        obj[name] = read(reader)
                    }
                }
                while (reader.hasNext()) {
                    obj[reader.nextName()] = read(reader)
                }
                reader.endObject()
                obj
            }

            else -> throw IllegalArgumentException("Unexpected token: ${reader.peek()}")
        }
    }

    // ------------------------------------------------------------------------------------------
    // A map to keep track of custom adapters registered for specific [Value] classes.
    // ------------------------------------------------------------------------------------------
    private val customAdapterMap = CustomValueTypeAdapterMap()

    // ------------------------------------------------------------------------------------------
    // Constructor – register the default module.
    // ------------------------------------------------------------------------------------------
    init {
        DefaultModule.create().register(this)
    }

    // ------------------------------------------------------------------------------------------
    // TypeAdapterFactory implementation: return the proper adapter for the given type.
    // ------------------------------------------------------------------------------------------
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val clazz = type.rawType
        @Suppress("UNCHECKED_CAST") return when (clazz) {
            Primitive::class.java -> PRIMITIVE_TYPE_ADAPTER as TypeAdapter<T>
            ObjectValue::class.java -> OBJECT_TYPE_ADAPTER as TypeAdapter<T>
            Array::class.java -> ARRAY_TYPE_ADAPTER as TypeAdapter<T>
            Null::class.java -> NULL_TYPE_ADAPTER as TypeAdapter<T>
            Layout::class.java -> LAYOUT_TYPE_ADAPTER as TypeAdapter<T>
            Value::class.java -> VALUE_TYPE_ADAPTER as TypeAdapter<T>
            else -> null
        }
    }

    /**
     * Registers a custom adapter creator for a given [Value] subclass.
     */
    fun register(clazz: Class<out Value>, creator: CustomValueTypeAdapterCreator<out Value>) {
        customAdapterMap.register(clazz, creator)
    }

    /**
     * Retrieve a custom adapter by the [Value] class.
     */
    fun getCustomValueTypeAdapter(clazz: Class<out Value>): CustomValueTypeAdapter<out Value> =
        customAdapterMap.get(clazz)

    /**
     * Retrieve a custom adapter by its assigned type integer.
     */
    fun getCustomValueTypeAdapter(type: Int): CustomValueTypeAdapter<out Value> =
        customAdapterMap.get(type)

    /**
     * Returns the Android context.
     */
    fun getContext(): Context = context

    // ------------------------------------------------------------------------------------------
    // Interface for modules that register custom adapters.
    // ------------------------------------------------------------------------------------------
    interface Module {
        fun register(factory: ProteusTypeAdapterFactory)
    }

    // ------------------------------------------------------------------------------------------
    // A holder for the Proteus instance.
    // ------------------------------------------------------------------------------------------
    class ProteusInstanceHolder {
        var proteus: Proteus? = null

        fun isLayout(type: String): Boolean = proteus?.has(type) == true
    }

    // ------------------------------------------------------------------------------------------
    // LayoutTypeAdapter converts JSON into a Layout object.
    // ------------------------------------------------------------------------------------------
    inner class LayoutTypeAdapter : TypeAdapter<Layout>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Layout?) {
            VALUE_TYPE_ADAPTER.write(out, value)
        }

        @Throws(IOException::class)
        override fun read(reader: JsonReader): Layout? {
            val value = VALUE_TYPE_ADAPTER.read(reader)
            return if (value != null && value.isLayout) value.asLayout else null
        }

        /**
         * Reads a layout from JSON given its type and the current Proteus instance.
         */
        @Throws(IOException::class)
        fun read(type: String, proteus: Proteus, reader: JsonReader): Layout {
            val attributes = mutableListOf<Layout.Attribute>()
            var data: Map<String, Value>? = null
            val extras = ObjectValue()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (ProteusConstants.DATA == name) {
                    data = readData(reader)
                } else {
                    // Retrieve attribute information using the proteus view parser.
                    val attr = proteus.getAttributeId(name, type)
                    if (attr != null) {
                        val manager: FunctionManager = PROTEUS_INSTANCE_HOLDER.proteus!!.functions
                        val value = attr.processor.precompile(
                            VALUE_TYPE_ADAPTER.read(reader), context, manager
                        )
                        attributes.add(Layout.Attribute(attr.id, value!!))
                    } else {
                        extras[name] = VALUE_TYPE_ADAPTER.read(reader)
                    }
                }
            }
            return Layout(
                type,
                if (attributes.isNotEmpty()) attributes else null,
                data,
                if (extras.entrySet().isNotEmpty()) extras else null
            )
        }

        /**
         * Reads a JSON object into a Map<String, Value>.
         */
        @Throws(IOException::class)
        fun readData(reader: JsonReader): Map<String, Value> {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return HashMap()
            }
            if (reader.peek() != JsonToken.BEGIN_OBJECT) {
                throw JsonSyntaxException("data must be a Map<String, String>.")
            }
            val data = LinkedHashMap<String, Value>()
            reader.beginObject()
            while (reader.hasNext()) {
                // Promote the name to value (internal Gson API usage)
                JsonReaderInternalAccess.INSTANCE.promoteNameToValue(reader)
                val key = reader.nextString()
                var value = VALUE_TYPE_ADAPTER.read(reader)
                // Precompile attributes if possible.
                val compiled = AttributeProcessor.staticPreCompile(
                    value, context, PROTEUS_INSTANCE_HOLDER.proteus!!.functions
                )
                if (compiled != null) value = compiled
                if (data.put(key, value) != null) {
                    throw JsonSyntaxException("duplicate key: $key")
                }
            }
            reader.endObject()
            return data
        }
    }

    // ------------------------------------------------------------------------------------------
    // A map to keep track of custom adapters using both the Value class and an index.
    // ------------------------------------------------------------------------------------------
    private inner class CustomValueTypeAdapterMap {
        private val types: MutableMap<Class<out Value>, CustomValueTypeAdapter<out Value>> =
            HashMap()
        private var adapters: array<CustomValueTypeAdapter<out Value>?> = arrayOf()

        /**
         * Registers an adapter for a given Value class. The adapter is assigned an integer type
         * equal to the current adapter count.
         */
        fun register(
            clazz: Class<out Value>, creator: CustomValueTypeAdapterCreator<out Value>
        ): CustomValueTypeAdapter<out Value> {
            types[clazz]?.let { return it }
            val adapter = creator.create(adapters.size, this@ProteusTypeAdapterFactory)
            adapters = adapters.copyOf(adapters.size + 1)
            adapters[adapters.lastIndex] = adapter
            types[clazz] = adapter
            return adapter
        }

        /**
         * Retrieves an adapter by the Value class.
         */
        fun get(clazz: Class<out Value>): CustomValueTypeAdapter<out Value> = types[clazz]
            ?: throw IllegalArgumentException("${clazz.name} is not a known value type! Register the class first.")

        /**
         * Retrieves an adapter by its assigned integer type.
         */
        fun get(index: Int): CustomValueTypeAdapter<out Value> =
            if (index in adapters.indices) adapters[index]!!
            else throw IllegalArgumentException("$index is not a known value type!")
    }
}
