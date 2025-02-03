package com.flipkart.android.proteus

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import com.flipkart.android.proteus.parser.IncludeParser
import com.flipkart.android.proteus.parser.ViewParser
import com.flipkart.android.proteus.parser.custom.*
import com.flipkart.android.proteus.processor.AttributeProcessor

/**
 * A builder class for configuring and creating a [Proteus] instance.
 * It registers parsers, functions, and modules for the Proteus layout engine.
 */
open class ProteusBuilder {

    companion object {
        /**
         * The Default Module of Proteus.
         */
        val DEFAULT_MODULE = Module { builder ->
            builder.apply {
                //register default parsers
                register(ViewParser<View>())
                register(IncludeParser<View>())
                register(ViewGroupParser<ViewGroup>())
                register(RelativeLayoutParser<RelativeLayout>())
                register(LinearLayoutParser<LinearLayout>())
                register(FrameLayoutParser<AspectRatioFrameLayout>())
                register(ScrollViewParser<ScrollView>())
                register(HorizontalScrollViewParser<HorizontalScrollView>())
                register(ImageViewParser<ImageView>())
                register(TextViewParser<TextView>())
                register(EditTextParser<EditText>())
                register(ButtonParser<Button>())
                register(ImageButtonParser<ImageButton>())
                register(WebViewParser<WebView>())
                register(RatingBarParser<FixedRatingBar>())
                register(CheckBoxParser<CheckBox>())
                register(ProgressBarParser<ProgressBar>())
                register(HorizontalProgressBarParser<HorizontalProgressBar>())


                // Register default functions
                register(Function.DATE)
                register(Function.FORMAT)
                register(Function.JOIN)
                register(Function.NUMBER)

                register(Function.ADD)
                register(Function.SUBTRACT)
                register(Function.MULTIPLY)
                register(Function.DIVIDE)
                register(Function.MODULO)


                register(Function.AND)
                register(Function.OR)

                register(Function.NOT)


                register(Function.EQUALS)
                register(Function.LESS_THAN)
                register(Function.GREATER_THAN)
                register(Function.LESS_THAN_OR_EQUALS)
                register(Function.GREATER_THAN_OR_EQUALS)

                register(Function.TERNARY)


                register(Function.CHAR_AT)
                register(Function.CONTAINS)
                register(Function.IS_EMPTY)
                register(Function.LENGTH)
                register(Function.TRIM)


                register(Function.MAX)
                register(Function.MIN)


                register(Function.SLICE)
            }
        }
    }

    private val processors = mutableMapOf<String, MutableMap<String, AttributeProcessor<*>>>()
    private val parsers = mutableMapOf<String, ViewTypeParser<*>>()
    private val functions = mutableMapOf<String, Function>()

    init {
        DEFAULT_MODULE.registerWith(this)
    }

    /**
     * Registers a map of attribute processors for a specific view type.
     *
     * @param type The view type (e.g., "TextView").
     * @param processors A map of attribute processors.
     * @return The current [ProteusBuilder] instance.
     */
    fun register(type: String, processors: Map<String, AttributeProcessor<*>>): ProteusBuilder {
        getExtraAttributeProcessors(type).putAll(processors)
        return this
    }

    /**
     * Registers a single attribute processor for a specific view type.
     *
     * @param type The view type (e.g., "TextView").
     * @param name The attribute name (e.g., "text").
     * @param processor The attribute processor.
     * @return The current [ProteusBuilder] instance.
     */
    fun register(type: String, name: String, processor: AttributeProcessor<*>): ProteusBuilder {
        getExtraAttributeProcessors(type)[name] = processor
        return this
    }

    /**
     * Registers a [ViewTypeParser] for a specific view type.
     *
     * @param parser The [ViewTypeParser] to register.
     * @return The current [ProteusBuilder] instance.
     */
    fun register(parser: ViewTypeParser<*>): ProteusBuilder {
        val parentType = parser.getParentType()
        if (parentType != null && !parsers.containsKey(parentType)) {
            throw IllegalStateException("$parentType is not a registered type parser")
        }
        parsers[parser.getType()] = parser
        return this
    }

    /**
     * Registers a [Function] for use in expressions.
     *
     * @param function The [Function] to register.
     * @return The current [ProteusBuilder] instance.
     */
    fun register(function: Function): ProteusBuilder {
        functions[function.getName()] = function
        return this
    }

    /**
     * Registers a [Module] to configure the builder.
     *
     * @param module The [Module] to register.
     * @return The current [ProteusBuilder] instance.
     */
    fun register(module: Module): ProteusBuilder {
        module.registerWith(this)
        return this
    }

    /**
     * Retrieves a registered [ViewTypeParser] by type.
     *
     * @param type The view type (e.g., "TextView").
     * @return The registered [ViewTypeParser], or `null` if not found.
     */
    fun get(type: String): ViewTypeParser<*>? = parsers[type]

    /**
     * Builds and returns a [Proteus] instance.
     *
     * @return A configured [Proteus] instance.
     */
    fun build(): Proteus {
        val types = parsers.mapValues { (_, parser) -> prepare(parser) }
        return Proteus(FunctionManager(functions), types)
    }

    /**
     * Prepares a [Proteus.Type] instance for a given [ViewTypeParser].
     *
     * @param parser The [ViewTypeParser] to prepare.
     * @return A [Proteus.Type] instance.
     */
    @Suppress("UNCHECKED_CAST")
    protected fun <V : View> prepare(parser: ViewTypeParser<V>): Proteus.Type {
        val name = parser.getType()
        val parent = parser.getParentType()?.let { parsers[it] } as ViewTypeParser<V>
        val extras = processors[name] as Map<String, AttributeProcessor<V>>
        return Proteus.Type(-1, name, parser, parser.prepare(parent, extras))
    }

    /**
     * Retrieves or creates a map of extra attribute processors for a specific view type.
     *
     * @param type The view type (e.g., "TextView").
     * @return A mutable map of attribute processors.
     */
    protected fun getExtraAttributeProcessors(type: String): MutableMap<String, AttributeProcessor<*>> {
        return processors.getOrPut(type) { mutableMapOf() }
    }

    /**
     * A module interface for configuring a [ProteusBuilder].
     */
    fun interface Module {
        /**
         * Registers parsers, functions, and processors with the builder.
         *
         * @param builder The [ProteusBuilder] to configure.
         */
        fun registerWith(builder: ProteusBuilder)
    }
}