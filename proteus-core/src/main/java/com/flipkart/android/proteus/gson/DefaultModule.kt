package com.flipkart.android.proteus.gson

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

import android.annotation.SuppressLint
import com.flipkart.android.proteus.value.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * DefaultModule registers custom JSON adapters (for reading and writing)
 * to convert between JSON and various value objects. This implementation
 * uses efficient Kotlin idioms to minimize overhead and avoid unnecessary
 * allocations.
 */
class DefaultModule private constructor() : ProteusTypeAdapterFactory.Module {

    // ---------------------------------------------------------------------
    // ATTRIBUTE_RESOURCE adapter:
    // Writes the attribute ID and reads a string then converts it to an Int.
    // ---------------------------------------------------------------------
    val ATTRIBUTE_RESOURCE = object : CustomValueTypeAdapterCreator<AttributeResource>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<AttributeResource>(type) {
                override fun write(out: JsonWriter, value: AttributeResource) {
                    out.value(value.attributeId)
                }

                override fun read(`in`: JsonReader): AttributeResource =
                    AttributeResource.valueOf(`in`.nextString().toInt())
            }
    }

    // ---------------------------------------------------------------------
    // BINDING adapter:
    // Serializes Binding as a string and deserializes using the factory context.
    // ---------------------------------------------------------------------
    val BINDING = object : CustomValueTypeAdapterCreator<Binding>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<Binding>(type) {
                override fun write(out: JsonWriter, value: Binding) {
                    out.value(value.toString())
                }

                override fun read(`in`: JsonReader): Binding = Binding.valueOf(
                    `in`.nextString(),
                    factory.context,
                    ProteusTypeAdapterFactory.PROTEUS_INSTANCE_HOLDER.proteus!!.functions
                )
            }
    }

    // ---------------------------------------------------------------------
    // COLOR_INT adapter:
    // Writes the color integer value and converts a read string into a Color.Int.
    // ---------------------------------------------------------------------
    val COLOR_INT = object : CustomValueTypeAdapterCreator<Color.Int>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<Color.Int>(type) {
                override fun write(out: JsonWriter, value: Color.Int) {
                    out.value(value.value)
                }

                override fun read(`in`: JsonReader): Color.Int =
                    Color.Int.valueOf(`in`.nextString().toInt())
            }
    }

    // ---------------------------------------------------------------------
    // COLOR_STATE_LIST adapter:
    // Writes the state list as an object with two properties ("s" and "c")
    // and reads them back to construct a Color.StateList.
    // ---------------------------------------------------------------------
    val COLOR_STATE_LIST = object : CustomValueTypeAdapterCreator<Color.StateList>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<Color.StateList>(type) {
                private val KEY_STATES = "s"
                private val KEY_COLORS = "c"
                override fun write(out: JsonWriter, value: Color.StateList) {
                    out.beginObject()
                    out.name(KEY_STATES)
                    // Use a helper that returns a String representation of the int[][].
                    out.value(ProteusTypeAdapterFactory.writeArrayOfIntArrays(value.states))
                    out.name(KEY_COLORS)
                    out.value(ProteusTypeAdapterFactory.writeArrayOfInts(value.colors))
                    out.endObject()
                }

                @SuppressLint("CheckResult")
                override fun read(`in`: JsonReader): Color.StateList {
                    `in`.beginObject()
                    `in`.nextName() // Expects KEY_STATES
                    val states = ProteusTypeAdapterFactory.readArrayOfIntArrays(`in`.nextString())
                    `in`.nextName() // Expects KEY_COLORS
                    val colors = ProteusTypeAdapterFactory.readArrayOfInts(`in`.nextString())
                    `in`.endObject()
                    return Color.StateList.valueOf(states, colors)
                }
            }
    }

    // ---------------------------------------------------------------------
    // DIMENSION adapter:
    // Serializes a Dimension by converting it to a string, and reverses that.
    // ---------------------------------------------------------------------
    val DIMENSION = object : CustomValueTypeAdapterCreator<Dimension>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<Dimension>(type) {
                override fun write(out: JsonWriter, value: Dimension) {
                    out.value(value.toString())
                }

                override fun read(`in`: JsonReader): Dimension =
                    Dimension.valueOf(`in`.nextString())
            }
    }

    // ---------------------------------------------------------------------
    // DRAWABLE_COLOR adapter:
    // Delegates writing/reading of the inner color value to the compiled adapter.
    // ---------------------------------------------------------------------
    val DRAWABLE_COLOR = object : CustomValueTypeAdapterCreator<DrawableValue.ColorValue>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<DrawableValue.ColorValue>(type) {
                override fun write(out: JsonWriter, value: DrawableValue.ColorValue) {
                    factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, value.color)
                }

                override fun read(`in`: JsonReader): DrawableValue.ColorValue =
                    DrawableValue.ColorValue.valueOf(
                        factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`), factory.context
                    )
            }
    }

    // ---------------------------------------------------------------------
    // DRAWABLE_LAYER_LIST adapter:
    // Uses mutable lists to avoid repeated array copying and then converts to arrays.
    // ---------------------------------------------------------------------
    val DRAWABLE_LAYER_LIST =
        object : CustomValueTypeAdapterCreator<DrawableValue.LayerListValue>() {
            override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
                object : CustomValueTypeAdapter<DrawableValue.LayerListValue>(type) {
                    private val KEY_IDS = "i"
                    private val KEY_LAYERS = "l"
                    override fun write(out: JsonWriter, value: DrawableValue.LayerListValue) {
                        out.beginObject()
                        out.name(KEY_IDS)
                        // Write each ID
                        out.beginArray()
                        value.getIds().forEach { out.value(it) }
                        out.endArray()
                        out.name(KEY_LAYERS)
                        out.beginArray()
                        value.getLayers().forEach { layer ->
                            factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, layer)
                        }
                        out.endArray()
                        out.endObject()
                    }

                    @SuppressLint("CheckResult")
                    override fun read(`in`: JsonReader): DrawableValue.LayerListValue {
                        `in`.beginObject()
                        `in`.nextName() // KEY_IDS
                        val ids = mutableListOf<Int>()
                        `in`.beginArray()
                        while (`in`.hasNext()) {
                            ids.add(`in`.nextString().toInt())
                        }
                        `in`.endArray()
                        `in`.nextName() // KEY_LAYERS
                        val layers = mutableListOf<Value>()
                        `in`.beginArray()
                        while (`in`.hasNext()) {
                            layers.add(factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`))
                        }
                        `in`.endArray()
                        `in`.endObject()
                        return DrawableValue.LayerListValue.valueOf(
                            ids.toIntArray(), layers.toTypedArray()
                        )
                    }
                }
        }

    // ---------------------------------------------------------------------
    // DRAWABLE_LEVEL_LIST adapter:
    // Reads levels from an array of objects and uses a mutable list to accumulate them.
    // ---------------------------------------------------------------------
    val DRAWABLE_LEVEL_LIST =
        object : CustomValueTypeAdapterCreator<DrawableValue.LevelListValue>() {
            override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
                object : CustomValueTypeAdapter<DrawableValue.LevelListValue>(type) {
                    private val KEY_MIN_LEVEL = "i"
                    private val KEY_MAX_LEVEL = "a"
                    private val KEY_DRAWABLE = "d"
                    override fun write(out: JsonWriter, value: DrawableValue.LevelListValue) {
                        out.beginArray()
                        value.getLevels().forEach { level ->
                            out.beginObject()
                            out.name(KEY_MIN_LEVEL).value(level.minLevel)
                            out.name(KEY_MAX_LEVEL).value(level.maxLevel)
                            out.name(KEY_DRAWABLE)
                            factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, level.drawable)
                            out.endObject()
                        }
                        out.endArray()
                    }

                    @SuppressLint("CheckResult")
                    override fun read(`in`: JsonReader): DrawableValue.LevelListValue {
                        val levels = mutableListOf<DrawableValue.Level>()
                        `in`.beginArray()
                        while (`in`.hasNext()) {
                            `in`.beginObject()
                            `in`.nextName() // KEY_MIN_LEVEL
                            val minLevel = `in`.nextString().toInt()
                            `in`.nextName() // KEY_MAX_LEVEL
                            val maxLevel = `in`.nextString().toInt()
                            `in`.nextName() // KEY_DRAWABLE
                            val drawable = factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`)
                            // Create level using factory context to optimize resource lookups.
                            levels.add(
                                DrawableValue.Level.valueOf(
                                    minLevel, maxLevel, drawable, factory.context
                                )
                            )
                            `in`.endObject()
                        }
                        `in`.endArray()
                        return DrawableValue.LevelListValue.value(levels.toTypedArray())
                    }
                }
        }

    // ---------------------------------------------------------------------
    // DRAWABLE_SHAPE adapter:
    // Uses a mock implementation – intended to be replaced with real logic.
    // ---------------------------------------------------------------------
    val DRAWABLE_SHAPE = object : CustomValueTypeAdapterCreator<DrawableValue.ShapeValue>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<DrawableValue.ShapeValue>(type) {
                override fun write(out: JsonWriter, value: DrawableValue.ShapeValue) {
                    out.value("#00000000") // Mock value; replace with real shape serialization.
                }

                override fun read(`in`: JsonReader): DrawableValue.ShapeValue {
                    `in`.skipValue()
                    return DrawableValue.ShapeValue.valueOf(0, null, null)
                }
            }
    }

    // ---------------------------------------------------------------------
    // DRAWABLE_RIPPLE adapter:
    // Writes only non-null properties and uses a when block for clarity.
    // ---------------------------------------------------------------------
    val DRAWABLE_RIPPLE = object : CustomValueTypeAdapterCreator<DrawableValue.RippleValue>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<DrawableValue.RippleValue>(type) {
                private val KEY_COLOR = "c"
                private val KEY_MASK = "m"
                private val KEY_CONTENT = "t"
                private val KEY_DEFAULT_BACKGROUND = "d"
                override fun write(out: JsonWriter, value: DrawableValue.RippleValue) {
                    out.beginObject()
                    out.name(KEY_COLOR)
                    factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, value.color)
                    value.mask?.let {
                        out.name(KEY_MASK)
                        factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, it)
                    }
                    value.content?.let {
                        out.name(KEY_CONTENT)
                        factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, it)
                    }
                    value.defaultBackground?.let {
                        out.name(KEY_DEFAULT_BACKGROUND)
                        factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, it)
                    }
                    out.endObject()
                }

                override fun read(`in`: JsonReader): DrawableValue.RippleValue {
                    var color: Value? = null
                    var mask: Value? = null
                    var content: Value? = null
                    var defaultBackground: Value? = null
                    `in`.beginObject()
                    while (`in`.hasNext()) {
                        when (val name = `in`.nextName()) {
                            KEY_COLOR -> color = factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`)
                            KEY_MASK -> mask = factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`)
                            KEY_CONTENT -> content = factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`)
                            KEY_DEFAULT_BACKGROUND -> defaultBackground =
                                factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`)

                            else -> throw IllegalStateException("Bad attribute '$name'")
                        }
                    }
                    `in`.endObject()
                    if (color == null) throw IllegalStateException("color is a required attribute in Ripple Drawable")
                    return DrawableValue.RippleValue.valueOf(
                        color, mask, content, defaultBackground
                    )
                }
            }
    }

    // ---------------------------------------------------------------------
    // DRAWABLE_STATE_LIST adapter:
    // Uses efficient iteration over the state list values.
    // ---------------------------------------------------------------------
    val DRAWABLE_STATE_LIST =
        object : CustomValueTypeAdapterCreator<DrawableValue.StateListValue>() {
            override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
                object : CustomValueTypeAdapter<DrawableValue.StateListValue>(type) {
                    private val KEY_STATES = "s"
                    private val KEY_VALUES = "v"
                    override fun write(out: JsonWriter, value: DrawableValue.StateListValue) {
                        out.beginObject()
                        out.name(KEY_STATES)
                        out.value(ProteusTypeAdapterFactory.writeArrayOfIntArrays(value.states))
                        out.name(KEY_VALUES)
                        out.beginArray()
                        value.getValues()
                            .forEach { factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, it) }
                        out.endArray()
                        out.endObject()
                    }

                    @SuppressLint("CheckResult")
                    override fun read(`in`: JsonReader): DrawableValue.StateListValue {
                        `in`.beginObject()
                        `in`.nextName() // KEY_STATES
                        val states =
                            ProteusTypeAdapterFactory.readArrayOfIntArrays(`in`.nextString())
                        `in`.nextName() // KEY_VALUES
                        val values = mutableListOf<Value>()
                        `in`.beginArray()
                        while (`in`.hasNext()) {
                            values.add(factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`))
                        }
                        `in`.endArray()
                        `in`.endObject()
                        return DrawableValue.StateListValue.valueOf(states, values.toTypedArray())
                    }
                }
        }

    // ---------------------------------------------------------------------
    // DRAWABLE_URL adapter:
    // Simple adapter that reads and writes URL strings.
    // ---------------------------------------------------------------------
    val DRAWABLE_URL = object : CustomValueTypeAdapterCreator<DrawableValue.UrlValue>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<DrawableValue.UrlValue>(type) {
                override fun write(out: JsonWriter, value: DrawableValue.UrlValue) {
                    out.value(value.url)
                }

                override fun read(`in`: JsonReader): DrawableValue.UrlValue =
                    DrawableValue.UrlValue.valueOf(`in`.nextString())
            }
    }

    // ---------------------------------------------------------------------
    // LAYOUT adapter:
    // Serializes a Layout object with its type, data map, attributes list, and extras.
    // Uses helper methods (declared as private functions) to keep the code modular.
    // ---------------------------------------------------------------------
    val LAYOUT = object : CustomValueTypeAdapterCreator<Layout>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<Layout>(type) {
                private val KEY_TYPE = "t"
                private val KEY_DATA = "d"
                private val KEY_ATTRIBUTES = "a"
                private val KEY_EXTRAS = "e"
                private val KEY_ATTRIBUTE_ID = "i"
                private val KEY_ATTRIBUTE_VALUE = "v"

                override fun write(out: JsonWriter, value: Layout) {
                    out.beginObject()
                    out.name(KEY_TYPE).value(value.type)
                    // Write data map if available.
                    value.data?.let { data ->
                        out.name(KEY_DATA)
                        out.beginObject()
                        data.forEach { (k, v) ->
                            out.name(k)
                            factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, v)
                        }
                        out.endObject()
                    }
                    // Write attributes if available.
                    value.attributes?.let { attrs ->
                        out.name(KEY_ATTRIBUTES)
                        out.beginArray()
                        attrs.forEach { attr ->
                            out.beginObject()
                            out.name(KEY_ATTRIBUTE_ID).value(attr.id)
                            out.name(KEY_ATTRIBUTE_VALUE)
                            factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, attr.value)
                            out.endObject()
                        }
                        out.endArray()
                    }
                    // Write extras if available.
                    value.extras?.let {
                        out.name(KEY_EXTRAS)
                        factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, it)
                    }
                    out.endObject()
                }

                override fun read(`in`: JsonReader): Layout {
                    var typeStr: String? = null
                    var data: Map<String, Value>? = null
                    var attributes: List<Layout.Attribute>? = null
                    var extras: ObjectValue? = null
                    `in`.beginObject()
                    while (`in`.hasNext()) {
                        when (`in`.nextName()) {
                            KEY_TYPE -> typeStr = `in`.nextString()
                            KEY_DATA -> data = readData(`in`, factory)
                            KEY_ATTRIBUTES -> attributes = readAttributes(`in`, factory)
                            KEY_EXTRAS -> extras = readExtras(`in`, factory)
                            else -> throw IllegalStateException("Bad attribute")
                        }
                    }
                    `in`.endObject()
                    if (typeStr == null) throw IllegalStateException("Layout must have type attribute!")
                    return Layout(typeStr, attributes, data, extras)
                }

                // Reads a JSON object into a map of values.
                private fun readData(
                    `in`: JsonReader, factory: ProteusTypeAdapterFactory
                ): Map<String, Value> {
                    val data = mutableMapOf<String, Value>()
                    `in`.beginObject()
                    while (`in`.hasNext()) {
                        val key = `in`.nextName()
                        val value = factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`)
                        data[key] = value
                    }
                    `in`.endObject()
                    return data
                }

                // Reads extras as an ObjectValue.
                private fun readExtras(
                    `in`: JsonReader, factory: ProteusTypeAdapterFactory
                ): ObjectValue = factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`).asObject

                // Reads a JSON array into a list of Layout.Attribute.
                @SuppressLint("CheckResult")
                private fun readAttributes(
                    `in`: JsonReader, factory: ProteusTypeAdapterFactory
                ): List<Layout.Attribute> {
                    val attributes = mutableListOf<Layout.Attribute>()
                    `in`.beginArray()
                    while (`in`.hasNext()) {
                        `in`.beginObject()
                        `in`.nextName() // Expect attribute ID.
                        val id = `in`.nextString().toInt()
                        `in`.nextName() // Expect attribute value.
                        val value = factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`)
                        attributes.add(Layout.Attribute(id, value))
                        `in`.endObject()
                    }
                    `in`.endArray()
                    return attributes
                }
            }
    }

    // ---------------------------------------------------------------------
    // NESTED_BINDING adapter:
    // Wraps a value using NestedBinding.
    // ---------------------------------------------------------------------
    val NESTED_BINDING = object : CustomValueTypeAdapterCreator<NestedBinding>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<NestedBinding>(type) {
                override fun write(out: JsonWriter, value: NestedBinding) {
                    factory.COMPILED_VALUE_TYPE_ADAPTER.write(out, value.value)
                }

                override fun read(`in`: JsonReader): NestedBinding =
                    NestedBinding.valueOf(factory.COMPILED_VALUE_TYPE_ADAPTER.read(`in`))
            }
    }

    // ---------------------------------------------------------------------
    // RESOURCE adapter:
    // Reads and writes a resource ID.
    // ---------------------------------------------------------------------
    val RESOURCE = object : CustomValueTypeAdapterCreator<Resource>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<Resource>(type) {
                override fun write(out: JsonWriter, value: Resource) {
                    out.value(value.resId)
                }

                override fun read(`in`: JsonReader): Resource =
                    Resource.valueOf(`in`.nextString().toInt())
            }
    }

    // ---------------------------------------------------------------------
    // STYLE_RESOURCE adapter:
    // Serializes both attribute ID and style ID, then recreates the object.
    // ---------------------------------------------------------------------
    val STYLE_RESOURCE = object : CustomValueTypeAdapterCreator<StyleResource>() {
        override fun create(type: Int, factory: ProteusTypeAdapterFactory) =
            object : CustomValueTypeAdapter<StyleResource>(type) {
                private val KEY_ATTRIBUTE_ID = "a"
                private val KEY_STYLE_ID = "s"
                override fun write(out: JsonWriter, value: StyleResource) {
                    out.beginObject()
                    out.name(KEY_ATTRIBUTE_ID).value(value.attributeId)
                    out.name(KEY_STYLE_ID).value(value.styleId)
                    out.endObject()
                }

                @SuppressLint("CheckResult")
                override fun read(`in`: JsonReader): StyleResource {
                    `in`.beginObject()
                    `in`.nextName()
                    val attributeId = `in`.nextString()
                    `in`.nextName()
                    val styleId = `in`.nextString()
                    `in`.endObject()
                    return StyleResource.valueOf(styleId.toInt(), attributeId.toInt())
                }
            }
    }

    // ---------------------------------------------------------------------
    // The register() method registers each custom adapter with the provided
    // ProteusTypeAdapterFactory, making them available during JSON layout parsing.
    // ---------------------------------------------------------------------
    override fun register(factory: ProteusTypeAdapterFactory) {
        factory.register(AttributeResource::class.java, ATTRIBUTE_RESOURCE)
        factory.register(Binding::class.java, BINDING)
        factory.register(Color.Int::class.java, COLOR_INT)
        factory.register(Color.StateList::class.java, COLOR_STATE_LIST)
        factory.register(Dimension::class.java, DIMENSION)
        // Registrations for gradient, corners, etc. are commented out:
        // factory.register(DrawableValue.Gradient::class.java, DRAWABLE_VALUE)
        // factory.register(DrawableValue.Corners::class.java, DRAWABLE_VALUE)
        // factory.register(DrawableValue.Solid::class.java, DRAWABLE_VALUE)
        // factory.register(DrawableValue.Size::class.java, DRAWABLE_VALUE)
        // factory.register(DrawableValue.Stroke::class.java, DRAWABLE_VALUE)
        factory.register(DrawableValue.ColorValue::class.java, DRAWABLE_COLOR)
        factory.register(DrawableValue.LayerListValue::class.java, DRAWABLE_LAYER_LIST)
        factory.register(DrawableValue.LevelListValue::class.java, DRAWABLE_LEVEL_LIST)
        factory.register(DrawableValue.RippleValue::class.java, DRAWABLE_RIPPLE)
        factory.register(DrawableValue.ShapeValue::class.java, DRAWABLE_SHAPE)
        factory.register(DrawableValue.StateListValue::class.java, DRAWABLE_STATE_LIST)
        factory.register(DrawableValue.UrlValue::class.java, DRAWABLE_URL)
        factory.register(Layout::class.java, LAYOUT)
        factory.register(NestedBinding::class.java, NESTED_BINDING)
        factory.register(Resource::class.java, RESOURCE)
        factory.register(StyleResource::class.java, STYLE_RESOURCE)
    }

    // ---------------------------------------------------------------------
    // Companion object provides a static factory method for instantiation.
    // ---------------------------------------------------------------------
    companion object {
        fun create() = DefaultModule()
    }
}
