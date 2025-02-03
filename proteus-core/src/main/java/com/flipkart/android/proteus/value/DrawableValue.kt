package com.flipkart.android.proteus.value

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.*
import android.view.View
import com.flipkart.android.proteus.ProteusLayoutInflater
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.parser.ParseHelper
import com.flipkart.android.proteus.processor.ColorResourceProcessor
import kotlin.Array as array

abstract class DrawableValue : Value() {

    companion object {
        private const val TYPE = "type"
        private const val CHILDREN = "children"

        private const val DRAWABLE_SELECTOR = "selector"
        private const val DRAWABLE_SHAPE = "shape"
        private const val DRAWABLE_LAYER_LIST = "layer-list"
        private const val DRAWABLE_LEVEL_LIST = "level-list"
        private const val DRAWABLE_RIPPLE = "ripple"

        private const val TYPE_CORNERS = "corners"
        private const val TYPE_GRADIENT = "gradient"
        private const val TYPE_PADDING = "padding"
        private const val TYPE_SIZE = "size"
        private const val TYPE_SOLID = "solid"
        private const val TYPE_STROKE = "stroke"

        @JvmStatic
        fun valueOf(value: String): DrawableValue? = when {
            Color.isColor(value) -> ColorValue.valueOf(value)
            else -> UrlValue.valueOf(value)
        }

        @JvmStatic
        fun valueOf(value: ObjectValue, context: Context): DrawableValue? =
            when (value.asString(TYPE)) {
                DRAWABLE_SELECTOR -> StateListValue.valueOf(value.asArray(CHILDREN), context)
                DRAWABLE_SHAPE -> ShapeValue.valueOf(value, context)
                DRAWABLE_LAYER_LIST -> LayerListValue.valueOf(value.asArray(CHILDREN)!!, context)
                DRAWABLE_LEVEL_LIST -> LevelListValue.valueOf(value.asArray(CHILDREN)!!, context)
                DRAWABLE_RIPPLE -> RippleValue.valueOf(value, context)
                else -> null
            }

        @JvmStatic
        fun convertBitmapToDrawable(original: Bitmap, context: Context): Drawable {
            val displayMetrics = context.resources.displayMetrics
            val density =
                displayMetrics.density // Use displayMetrics.density for density-based scaling

            val (width, height) = original.width to original.height
            val (scaleWidth, scaleHeight) = density to density // Use density for scaling
            val matrix = Matrix().apply { postScale(scaleWidth, scaleHeight) }

            val resizedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true)
            return BitmapDrawable(context.resources, resizedBitmap)
        }
    }


    abstract fun apply(
        view: ProteusView,
        context: Context,
        loader: ProteusLayoutInflater.ImageLoader,
        callback: Callback
    )

    override fun copy(): Value = this

    fun interface Callback {
        fun apply(drawable: Drawable)
    }


    class ColorValue(val color: Value) : DrawableValue() {

        companion object {
            @JvmField
            val BLACK = ColorValue(Color.Int.BLACK)

            @JvmStatic
            fun valueOf(value: String): ColorValue = ColorValue(Color.valueOf(value))

            @JvmStatic
            fun valueOf(value: Value, context: Context): ColorValue =
                ColorValue(ColorResourceProcessor.staticCompile(value, context))

        }

        override fun apply(
            view: ProteusView,
            context: Context,
            loader: ProteusLayoutInflater.ImageLoader,
            callback: Callback
        ) {
            val drawable = ColorDrawable(ColorResourceProcessor.evaluate(color, view)!!.color)
            callback.apply(drawable)
        }
    }

    class ShapeValue() : DrawableValue() {

        private var shape: Int = 0
        private var gradient: Gradient? = null
        private var elements: array<DrawableElement>? = null

        private constructor(value: ObjectValue, context: Context) : this() {
            shape = getShape(value.asString(SHAPE))

            var gradient: Gradient?
            var elements: array<DrawableElement?>?
            value[CHILDREN]?.asArray?.let { children ->

                val tempElements = arrayOfNulls<DrawableElement>(children.size())
                var gradientTemp: Gradient? = null
                children.forEachIndexed { index, value ->
                    val child = value.asObject
                    when (child.asString(TYPE)) {
                        TYPE_CORNERS -> tempElements[index] = Corners.valueOf(child, context)
                        TYPE_SIZE -> tempElements[index] = Size.valueOf(child, context)
                        TYPE_SOLID -> tempElements[index] = Solid.valueOf(child, context)
                        TYPE_STROKE -> tempElements[index] = Stroke.valueOf(child, context)
                        TYPE_GRADIENT -> {
                            gradientTemp = Gradient.valueOf(child, context)
                            tempElements[index] = gradientTemp
                        }
                    }

                }
                gradient = gradientTemp
                elements = tempElements
            } ?: run {
                elements = null
                gradient = null
            }
        }

        private constructor(
            shape: Int, gradient: Gradient?, elements: array<DrawableElement>?
        ) : this() {
            this.shape = shape
            this.gradient = gradient
            this.elements = elements
        }

        companion object {

            private const val SHAPE_NONE = -1

            private const val SHAPE_RECTANGLE = "rectangle"
            private const val SHAPE_OVAL = "oval"
            private const val SHAPE_LINE = "line"
            private const val SHAPE_RING = "ring"

            private const val SHAPE = "shape"

            @JvmStatic
            fun valueOf(value: ObjectValue, context: Context): ShapeValue =
                ShapeValue(value, context)


            @JvmStatic
            fun valueOf(
                shape: Int, gradient: Gradient?, elements: array<DrawableElement>?
            ): ShapeValue = ShapeValue(shape, gradient, elements)

            private fun getShape(shape: String?): Int = when (shape) {
                SHAPE_RECTANGLE -> GradientDrawable.RECTANGLE
                SHAPE_OVAL -> GradientDrawable.OVAL
                SHAPE_LINE -> GradientDrawable.LINE
                SHAPE_RING -> GradientDrawable.RING
                else -> SHAPE_NONE
            }

        }

        override fun apply(
            view: ProteusView,
            context: Context,
            loader: ProteusLayoutInflater.ImageLoader,
            callback: Callback
        ) {
            val drawable = gradient?.init(view) ?: GradientDrawable()
            if (shape != -1) {
                drawable.shape = shape
            }
            elements?.forEach { it.apply(view, drawable) }

            callback.apply(drawable)
        }


    }

    class LayerListValue() : DrawableValue() {

        private lateinit var ids: IntArray
        private lateinit var layers: array<Value>

        private constructor(layers: Array, context: Context) : this() {
            val size = layers.size()
            this.ids = IntArray(size)
            this.layers = array(size) { Primitive(0) }
            layers.forEachIndexed { index, value ->
                val (resId, drawable) = parseLayer(value.asObject, context)
                this.ids[index] = resId
                this.layers[index] = drawable
            }
        }

        private constructor(ids: IntArray, layers: array<Value>) : this() {
            this.ids = ids
            this.layers = layers
        }

        companion object {
            private const val ID_STR = "id"
            private const val DRAWABLE_STR = "drawable"

            @JvmStatic
            fun valueOf(layers: Array, context: Context) = LayerListValue(layers, context)

            @JvmStatic
            fun valueOf(ids: IntArray, layers: array<Value>) = LayerListValue(ids, layers)


            private fun parseLayer(layer: ObjectValue, context: Context): Pair<Int, Value> {
                val id = layer.asString(ID_STR)
                val resId = id?.let { ParseHelper.getAndroidXmlResId(it) } ?: View.NO_ID
                val drawable = layer[DRAWABLE_STR]
                val out = DrawableResourceProcessor.staticCompile(drawable, context)
                return Pair(resId, out)
            }

        }


        override fun apply(
            view: ProteusView,
            context: Context,
            loader: ProteusLayoutInflater.ImageLoader,
            callback: Callback
        ) {
            val drawables =
                layers.map { DrawableResourceProcessor.evaluate(it, view) }.toTypedArray()
            val layerDrawable = LayerDrawable(drawables)
            drawables.indices.forEach { layerDrawable.setId(it, ids[it]) }
            callback.apply(layerDrawable)
        }


    }


    class StateListValue() : DrawableValue() {

        private lateinit var states: array<IntArray>
        private lateinit var values: array<Value>

        private constructor(states: array<IntArray>, values: array<Value>) : this() {
            this.states = states
            this.values = values
        }

        private constructor(states: Array, context: Context) : this() {
            val stateValues = array(states.size()) {
                parseState(
                    states[it].asObject, context
                )
            }
            this.states = stateValues.map { it.first }.toTypedArray()
            this.values = stateValues.map { it.second }.toTypedArray()
        }

        companion object {
            private const val DRAWABLE_STR = "drawable"
            private val stateMap = mapOf(
                "state_pressed" to R.attr.state_pressed,
                "state_enabled" to R.attr.state_enabled,
                "state_focused" to R.attr.state_focused,
                "state_hovered" to R.attr.state_hovered,
                "state_selected" to R.attr.state_selected,
                "state_checkable" to R.attr.state_checkable,
                "state_checked" to R.attr.state_checked,
                "state_activated" to R.attr.state_activated,
                "state_window_focused" to R.attr.state_window_focused
            )

            @JvmStatic
            fun valueOf(states: Array?, context: Context): StateListValue =
                StateListValue(states!!, context)

            @JvmStatic
            fun valueOf(states: array<IntArray>, values: array<Value>): StateListValue =
                StateListValue(states, values)


            private fun parseState(value: ObjectValue, context: Context): Pair<IntArray, Value> {
                val drawable = DrawableResourceProcessor.staticCompile(value[DRAWABLE_STR], context)
                val states = mutableListOf<Int>()
                value.forEach { key, entryValue ->
                    stateMap[key]?.let {
                        states.add(if (ParseHelper.parseBoolean(entryValue)) it else -it)
                    } ?: throw IllegalArgumentException("$key is not a valid state")

                }
                return Pair(states.toIntArray(), drawable)
            }

        }

        override fun apply(
            view: ProteusView,
            context: Context,
            loader: ProteusLayoutInflater.ImageLoader,
            callback: Callback
        ) {
            val stateListDrawable = StateListDrawable()
            states.indices.forEach {
                stateListDrawable.addState(
                    states[it], DrawableResourceProcessor.evaluate(values[it], view)
                )
            }
            callback.apply(stateListDrawable)
        }
    }

    class LevelListValue() : DrawableValue() {

        private lateinit var levels: array<Level>

        constructor(levels: Array, context: Context) : this() {
            this.levels = array(levels.size()) { Level.ZERO_LEVEL }
            levels.forEachIndexed { index, value ->
                this.levels[index] = Level(value.asObject, context)
            }
        }

        constructor(arrayLevels: array<Level>) : this() {
            this.levels = arrayLevels
        }

        companion object {
            @JvmStatic
            fun valueOf(levels: Array, context: Context): LevelListValue {
                return LevelListValue(levels, context)
            }

            @JvmStatic
            fun value(levels: array<Level>) = LevelListValue(levels)
        }

        override fun apply(
            view: ProteusView,
            context: Context,
            loader: ProteusLayoutInflater.ImageLoader,
            callback: Callback
        ) {
            val levelListDrawable = LevelListDrawable()
            levels.forEach { it.apply(view, levelListDrawable) }
            callback.apply(levelListDrawable)
        }

    }

    data class Level(
        val minLevel: Int, val maxLevel: Int, val drawable: Value
    ) {
        companion object {
            const val MIN_LEVEL = "minLevel"
            const val MAX_LEVEL = "maxLevel"
            const val DRAWABLE = "drawable"

            @JvmStatic
            val ZERO_LEVEL = Level(0, 0, Primitive(0))

            @JvmStatic
            fun valueOf(
                minLevel: Int, maxLevel: Int, drawable: Value, context: Context
            ): Level = Level(
                minLevel, maxLevel, DrawableResourceProcessor.staticCompile(drawable, context)
            )

        }

        constructor(value: ObjectValue, context: Context) : this(
            value.asInteger(MIN_LEVEL)!!,
            value.asInteger(MAX_LEVEL)!!,
            DrawableResourceProcessor.staticCompile(value[DRAWABLE], context)
        )

        fun apply(view: ProteusView, levelListDrawable: LevelListDrawable) {
            levelListDrawable.addLevel(
                minLevel, maxLevel, DrawableResourceProcessor.evaluate(drawable, view)
            )
        }

    }


    class RippleValue() : DrawableValue() {

        private lateinit var color: Value
        private var mask: Value? = null
        private var content: Value? = null
        private var defaultBackground: Value? = null

        constructor(
            color: Value, mask: Value?, content: Value?, defaultBackground: Value?
        ) : this() {
            this.color = color
            this.mask = mask
            this.content = content
            this.defaultBackground = defaultBackground
        }

        private constructor(value: ObjectValue, context: Context) : this() {
            color = value[COLOR]!!
            mask = value[MASK]?.let { DrawableResourceProcessor.staticCompile(it, context) }
            content = value[CONTENT]?.let { DrawableResourceProcessor.staticCompile(it, context) }
            defaultBackground = value[DEFAULT_BACKGROUND]?.let {
                DrawableResourceProcessor.staticCompile(
                    it, context
                )
            }
        }

        companion object {

            private const val COLOR = "color"
            private const val MASK = "mask"
            private const val CONTENT = "content"
            private const val DEFAULT_BACKGROUND = "defaultBackground"

            @JvmStatic
            fun valueOf(
                color: Value, mask: Value?, content: Value?, defaultBackground: Value?
            ) = RippleValue(color, mask, content, defaultBackground)

            @JvmStatic
            fun valueOf(value: ObjectValue, context: Context): RippleValue =
                RippleValue(value, context)
        }

        override fun apply(
            view: ProteusView,
            context: Context,
            loader: ProteusLayoutInflater.ImageLoader,
            callback: Callback
        ) {
            val colorStateList: ColorStateList =
                ColorResourceProcessor.evaluate(color, view)?.colors ?: ColorStateList(
                    arrayOf(intArrayOf()),
                    intArrayOf(ColorResourceProcessor.evaluate(color, view)?.color!!)
                )

            val contentDrawable = content?.let { DrawableResourceProcessor.evaluate(it, view) }
            val maskDrawable = mask?.let { DrawableResourceProcessor.evaluate(it, view) }

            val resultDrawable = RippleDrawable(colorStateList, contentDrawable, maskDrawable)
            callback.apply(resultDrawable)

        }
    }


    class UrlValue() : DrawableValue() {

        private lateinit var url: String

        constructor(url: String) : this() {
            this.url = url
        }

        companion object {
            @JvmStatic
            fun valueOf(url: String): UrlValue = UrlValue(url)
        }

        override fun apply(
            view: ProteusView,
            context: Context,
            loader: ProteusLayoutInflater.ImageLoader,
            callback: Callback
        ) {
            loader.getBitmap(view, url, object : AsyncCallback() {
                override fun apply(drawable: Drawable) {
                    callback.apply(drawable)
                }

                override fun apply(bitmap: Bitmap) {
                    callback.apply(convertBitmapToDrawable(bitmap, view.asView.context))
                }
            })
        }
    }


    abstract class DrawableElement : Value() {
        abstract fun apply(view: ProteusView, drawable: GradientDrawable)
        override fun copy(): Value = this
    }

    class Gradient() : DrawableElement() {

        private var angle: Int? = null
        private var centerX: Float? = null
        private var centerY: Float? = null
        private var centerColor: Value? = null
        private var endColor: Value? = null
        private var gradientRadius: Value? = null
        private var startColor: Value? = null
        private var gradientType: Int? = null
        private var useLevel: Boolean? = null

        private constructor(gradient: ObjectValue, context: Context) : this() {
            angle = gradient.asInteger(ANGLE)
            centerX = gradient.asFloat(CENTER_X)
            centerY = gradient.asFloat(CENTER_Y)

            startColor =
                gradient[START_COLOR]?.let { ColorResourceProcessor.staticCompile(it, context) }
            centerColor =
                gradient[CENTER_COLOR]?.let { ColorResourceProcessor.staticCompile(it, context) }
            endColor =
                gradient[END_COLOR]?.let { ColorResourceProcessor.staticCompile(it, context) }
            gradientRadius = gradient[GRADIENT_RADIUS]?.let {
                DimensionAttributeProcessor.staticCompile(
                    it, context
                )
            }
            gradientType = getGradientType(gradient.asString(GRADIENT_TYPE))
            useLevel = gradient.asBoolean(USE_LEVEL)
        }

        companion object {
            const val ANGLE = "angle"
            const val CENTER_X = "centerX"
            const val CENTER_Y = "centerY"
            const val CENTER_COLOR = "centerColor"
            const val END_COLOR = "endColor"
            const val GRADIENT_RADIUS = "gradientRadius"
            const val START_COLOR = "startColor"
            const val GRADIENT_TYPE = "gradientType"
            const val USE_LEVEL = "useLevel"


            private const val GRADIENT_TYPE_NONE = -1

            private const val LINEAR_GRADIENT = "linear"
            private const val RADIAL_GRADIENT = "radial"
            private const val SWEEP_GRADIENT = "sweep"

            @JvmStatic
            fun valueOf(gradient: ObjectValue, context: Context): Gradient =
                Gradient(gradient, context)

            private fun getGradientType(type: String?) = when (type) {
                LINEAR_GRADIENT -> GradientDrawable.LINEAR_GRADIENT
                RADIAL_GRADIENT -> GradientDrawable.RADIAL_GRADIENT
                SWEEP_GRADIENT -> GradientDrawable.SWEEP_GRADIENT
                else -> GRADIENT_TYPE_NONE
            }

            private fun getOrientation(angle: Int?): GradientDrawable.Orientation =
                when (angle?.rem(360) ?: 0) {
                    0 -> GradientDrawable.Orientation.LEFT_RIGHT
                    45 -> GradientDrawable.Orientation.BL_TR
                    90 -> GradientDrawable.Orientation.BOTTOM_TOP
                    135 -> GradientDrawable.Orientation.BR_TL
                    180 -> GradientDrawable.Orientation.RIGHT_LEFT
                    225 -> GradientDrawable.Orientation.TR_BL
                    270 -> GradientDrawable.Orientation.TOP_BOTTOM
                    315 -> GradientDrawable.Orientation.TL_BR
                    else -> GradientDrawable.Orientation.LEFT_RIGHT
                }
        }

        fun init(colors: IntArray?, angle: Int?): GradientDrawable =
            colors?.let { GradientDrawable(getOrientation(angle), it) } ?: GradientDrawable()

        fun init(view: ProteusView): GradientDrawable {

            val colors = if (centerColor != null) {
                intArrayOf(
                    ColorResourceProcessor.evaluate(startColor, view)?.color!!,
                    ColorResourceProcessor.evaluate(centerColor, view)?.color!!,
                    ColorResourceProcessor.evaluate(endColor, view)?.color!!
                )
            } else {
                intArrayOf(
                    ColorResourceProcessor.evaluate(startColor, view)?.color!!,
                    ColorResourceProcessor.evaluate(endColor, view)?.color!!
                )
            }
            return GradientDrawable(getOrientation(angle), colors)

        }

        override fun apply(view: ProteusView, drawable: GradientDrawable) {
            centerX?.let { centerY?.let { drawable.setGradientCenter(it, it) } }
            gradientRadius?.let {
                drawable.setGradientRadius(
                    DimensionAttributeProcessor.evaluate(
                        it, view
                    )
                )
            }
            if (gradientType != GRADIENT_TYPE_NONE) {
                drawable.gradientType = gradientType!!
            }

        }
    }

    class Corners() : DrawableElement() {

        private var radius: Value? = null
        private var topLeftRadius: Value? = null
        private var topRightRadius: Value? = null
        private var bottomLeftRadius: Value? = null
        private var bottomRightRadius: Value? = null

        private constructor(corner: ObjectValue, context: Context) : this() {
            radius = DimensionAttributeProcessor.staticCompile(corner[RADIUS], context)
            topLeftRadius =
                DimensionAttributeProcessor.staticCompile(corner[TOP_LEFT_RADIUS], context)
            topRightRadius =
                DimensionAttributeProcessor.staticCompile(corner[TOP_RIGHT_RADIUS], context)
            bottomLeftRadius =
                DimensionAttributeProcessor.staticCompile(corner[BOTTOM_LEFT_RADIUS], context)
            bottomRightRadius =
                DimensionAttributeProcessor.staticCompile(corner[BOTTOM_RIGHT_RADIUS], context)
        }

        companion object {
            const val RADIUS = "radius"
            const val TOP_LEFT_RADIUS = "topLeftRadius"
            const val TOP_RIGHT_RADIUS = "topRightRadius"
            const val BOTTOM_LEFT_RADIUS = "bottomLeftRadius"
            const val BOTTOM_RIGHT_RADIUS = "bottomRightRadius"

            @JvmStatic
            fun valueOf(corner: ObjectValue, context: Context) = Corners(corner, context)
        }

        override fun apply(view: ProteusView, drawable: GradientDrawable) {
            radius?.let { drawable.cornerRadius = DimensionAttributeProcessor.evaluate(it, view) }

            val fTopLeftRadius = DimensionAttributeProcessor.evaluate(topLeftRadius, view)
            val fTopRightRadius = DimensionAttributeProcessor.evaluate(topRightRadius, view)
            val fBottomRightRadius = DimensionAttributeProcessor.evaluate(bottomRightRadius, view)
            val fBottomLeftRadius = DimensionAttributeProcessor.evaluate(bottomLeftRadius, view)

            if (fTopLeftRadius != 0f || fTopRightRadius != 0f || fBottomRightRadius != 0f || fBottomLeftRadius != 0f) {
                // The corner radii are specified in clockwise order (see Path.addRoundRect())
                drawable.cornerRadii = floatArrayOf(
                    fTopLeftRadius,
                    fTopLeftRadius,
                    fTopRightRadius,
                    fTopRightRadius,
                    fBottomRightRadius,
                    fBottomRightRadius,
                    fBottomLeftRadius,
                    fBottomLeftRadius
                )
            }

        }
    }

    class Solid() : DrawableElement() {

        private lateinit var color: Value

        private constructor(value: ObjectValue, context: Context) : this() {
            color = ColorResourceProcessor.staticCompile(value[COLOR], context)
        }

        companion object {
            const val COLOR = "color"

            @JvmStatic
            fun valueOf(value: ObjectValue, context: Context) = Solid(value, context)
        }

        override fun apply(view: ProteusView, drawable: GradientDrawable) {
            val result = ColorResourceProcessor.evaluate(color, view)
            if (result?.colors != null) {
                drawable.color = result.colors
            } else {
                drawable.setColor(result?.color!!)
            }
        }
    }

    class Size() : DrawableElement() {

        private lateinit var width: Value
        private lateinit var height: Value

        private constructor(value: ObjectValue, context: Context) : this() {
            width = DimensionAttributeProcessor.staticCompile(value[WIDTH], context)
            height = DimensionAttributeProcessor.staticCompile(value[HEIGHT], context)
        }

        companion object {
            const val WIDTH = "width"
            const val HEIGHT = "height"

            @JvmStatic
            fun valueOf(value: ObjectValue, context: Context) = Size(value, context)
        }

        override fun apply(view: ProteusView, drawable: GradientDrawable) {
            drawable.setSize(
                DimensionAttributeProcessor.evaluate(width, view).toInt(),
                DimensionAttributeProcessor.evaluate(height, view).toInt()
            )
        }
    }

    class Stroke() : DrawableElement() {

        private lateinit var width: Value
        private lateinit var color: Value
        private var dashWidth: Value? = null
        private var dashGap: Value? = null

        private constructor(value: ObjectValue, context: Context) : this() {
            width = DimensionAttributeProcessor.staticCompile(value[WIDTH], context)
            color = ColorResourceProcessor.staticCompile(value[COLOR], context)
            dashWidth = DimensionAttributeProcessor.staticCompile(value[DASH_WIDTH], context)
            dashGap = DimensionAttributeProcessor.staticCompile(value[DASH_GAP], context)
        }

        companion object {
            const val WIDTH = "width"
            const val COLOR = "color"
            const val DASH_WIDTH = "dashWidth"
            const val DASH_GAP = "dashGap"

            @JvmStatic
            fun valueOf(stroke: ObjectValue, context: Context) = Stroke(stroke, context)
        }

        override fun apply(view: ProteusView, drawable: GradientDrawable) {
            if (dashWidth == null) {
                drawable.setStroke(DimensionAttributeProcessor.evaluate(width, view).toInt(), color)
            } else if (dashGap != null) {
                drawable.setStroke(
                    DimensionAttributeProcessor.evaluate(width, view).toInt(),
                    color,
                    DimensionAttributeProcessor.evaluate(dashWidth, view),
                    DimensionAttributeProcessor.evaluate(dashGap, view)
                )
            }
        }
    }

    abstract class AsyncCallback {
        private var recycled = false
        fun setBitmap(bitmap: Bitmap) {
            if (recycled) throw ProteusInflateException("Cannot make calls to a recycled instance!")
            apply(bitmap)
            recycled = true
        }

        fun setDrawable(drawable: Drawable) {
            if (recycled) throw ProteusInflateException("Cannot make calls to a recycled instance!")
            apply(drawable)
            recycled = true
        }

        protected abstract fun apply(drawable: Drawable)
        protected abstract fun apply(bitmap: Bitmap)
    }
}