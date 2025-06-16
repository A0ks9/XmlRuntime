package com.voyager.core.view.processor

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.method.DigitsKeyListener
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.utils.parser.BooleanParser.isBoolean
import com.voyager.core.utils.parser.BooleanParser.parseBoolean
import com.voyager.core.utils.parser.ColorParser.getColor
import com.voyager.core.utils.parser.ColorParser.isColor
import com.voyager.core.utils.parser.PorterDuffParser.parsePorterDuff
import com.voyager.core.utils.parser.ResourceParser.getDrawable
import com.voyager.core.view.utils.RelativeLayoutUtils.addRelativeLayoutRule
import com.voyager.core.view.utils.RelativeLayoutUtils.parseRelativeLayoutBoolean
import com.voyager.core.view.utils.ViewExtensions.DrawablePosition
import com.voyager.core.view.utils.ViewExtensions.getParentView
import com.voyager.core.view.utils.ViewExtensions.getViewID
import com.voyager.core.utils.StringUtils.extractViewId
import com.voyager.core.view.utils.text.ReverseTransformation
import com.voyager.core.view.utils.toLayoutParam
import java.util.concurrent.ConcurrentHashMap

/**
 * A high-performance handler for processing Android view attributes in the Voyager framework.
 *
 * This object provides efficient methods for applying various attributes to Android views,
 * including layout parameters, styling, transformations, and resource handling.
 *
 * Key features:
 * - Efficient resource handling and caching
 * - Optimized layout parameter management
 * - Memory-efficient drawable processing
 * - Thread-safe operations
 * - Comprehensive attribute support
 * - Error handling and logging
 * - Resource cleanup
 * - Cache management
 *
 * Performance optimizations:
 * - Lazy initialization of resources
 * - Caching of frequently used values
 * - Efficient string operations
 * - Optimized drawable handling
 * - Reduced object creation
 * - Thread-safe caching
 * - Resource pooling
 *
 * Best practices:
 * 1. Handle null values appropriately
 * 2. Consider view lifecycle
 * 3. Use thread-safe operations
 * 4. Consider memory leaks
 * 5. Implement proper error handling
 * 6. Validate attributes before use
 * 7. Clean up resources properly
 * 8. Use caching effectively
 *
 * Usage example:
 * ```kotlin
 * // Apply layout parameters
 * AttributesHandler.setSize(view, "match_parent", true)
 * AttributesHandler.setMargin(view, 16)
 *
 * // Apply styling
 * AttributesHandler.handleBackground(view, "#FF0000", context)
 * AttributesHandler.handleTint(view, "#00FF00")
 *
 * // Apply transformations
 * AttributesHandler.handleTransformationMethod(textView, "password")
 *
 * // Clear cache
 * AttributesHandler.clearCache()
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
internal object AttributesHandler {
    private const val TAG = "AttributesHandler"
    private const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
    private val config = ConfigManager.config
    private val logger = LoggerFactory.getLogger(TAG)
    private val resourceCache = ConcurrentHashMap<String, Any>()

    // Transformation constants
    private const val TRANSFORM_ALL_CAPS = "allCaps"
    private const val TRANSFORM_PASSWORD = "password"
    private const val TRANSFORM_SINGLE_LINE = "singleLine"
    private const val TRANSFORM_DIGITS = "digits"
    private const val TRANSFORM_REVERSE = "reverse"

    // Resource type constants
    private const val RESOURCE_TYPE_DRAWABLE = "drawable"
    private const val RESOURCE_TYPE_COLOR = "color"
    private const val RESOURCE_TYPE_ATTR = "attr"

    // URL protocol constants
    private const val PROTOCOL_HTTP = "http:"
    private const val PROTOCOL_HTTPS = "https:"
    private const val PROTOCOL_DOUBLE_SLASH = "//"

    /**
     * Clears all cached resources.
     * Thread-safe operation that removes all cached values.
     *
     * Performance Considerations:
     * - Clear cache only when necessary
     * - Consider memory impact
     * - Handle concurrent access
     */
    fun clearCache() {
        try {
            resourceCache.clear()
            logger.debug("clearCache", "Resource cache cleared successfully")
        } catch (e: Exception) {
            logger.error("clearCache", "Failed to clear resource cache: ${e.message}")
        }
    }

    /**
     * Gets a cached resource value.
     * Thread-safe operation that retrieves a cached value.
     *
     * @param key The cache key
     * @return The cached value, or null if not found
     */
    fun <T> getCachedResource(key: String): T? {
        return try {
            @Suppress("UNCHECKED_CAST")
            resourceCache[key] as? T
        } catch (e: Exception) {
            logger.error("getCachedResource", "Failed to get cached resource for key $key: ${e.message}")
            null
        }
    }

    /**
     * Sets a cached resource value.
     * Thread-safe operation that caches a value.
     *
     * @param key The cache key
     * @param value The value to cache
     */
    fun <T> setCachedResource(key: String, value: T) {
        try {
            resourceCache[key] = value as Any
        } catch (e: Exception) {
            logger.error("setCachedResource", "Failed to set cached resource for key $key: ${e.message}")
        }
    }

    /**
     * Sets an image source for an ImageView, supporting both local resources and remote URLs.
     * Thread-safe operation with efficient resource handling.
     *
     * Performance Considerations:
     * - Efficient resource loading
     * - Minimal object creation
     * - Safe resource handling
     * - Optimized image loading
     *
     * @param view The target ImageView
     * @param imageSource The source URL or resource reference
     * @throws IllegalArgumentException if the image source format is invalid
     */
    fun setImageSource(view: ImageView, imageSource: String) {
        try {
            val processedSource = when {
                imageSource.startsWith(PROTOCOL_DOUBLE_SLASH) -> PROTOCOL_HTTP + imageSource
                imageSource.startsWith(PROTOCOL_HTTP) || imageSource.startsWith(PROTOCOL_HTTPS) -> imageSource
                imageSource.startsWith("@$RESOURCE_TYPE_DRAWABLE/") -> {
                    val drawable = getDrawable(view, imageSource.removePrefix("@$RESOURCE_TYPE_DRAWABLE/"))
                    if (drawable != null) {
                        view.setImageDrawable(drawable)
                        return
                    }
                    view.setImageResource(android.R.drawable.ic_menu_report_image)
                    return
                }
                else -> throw IllegalArgumentException("Unsupported image source format: $imageSource")
            }

            Glide.with(view.context).load(processedSource).into(view)
        } catch (e: Exception) {
            logger.error("setImageSource", "Failed to set image source: ${e.message}")
            view.setImageResource(android.R.drawable.ic_menu_report_image)
        }
    }

    /**
     * Sets the size (width or height) of a view using layout parameters.
     *
     * @param view The target view
     * @param size The size value (e.g., "match_parent", "wrap_content", "100dp")
     * @param isWidthDimension Whether to set width (true) or height (false)
     */
    fun setSize(view: View, size: String, isWidthDimension: Boolean) {
        val layoutParams = view.layoutParams ?: ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val dimension = size.toLayoutParam(
            view.resources.displayMetrics, view.getParentView(), isWidthDimension
        )
        if (isWidthDimension) layoutParams.width = dimension else layoutParams.height = dimension
        view.layoutParams = layoutParams
    }

    /**
     * Adds a rule to a RelativeLayout for a view.
     *
     * @param view The target view
     * @param anchor The anchor view ID or boolean value
     * @param layoutRule The layout rule to apply
     */
    fun addRelativeLayoutRule(view: View, anchor: String, layoutRule: Int) {
        (view.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            val anchorId = when {
                anchor.isBoolean() -> parseRelativeLayoutBoolean(parseBoolean(anchor))
                else -> view.getParentView()?.getViewID(anchor.extractViewId())!!
            }
            addRelativeLayoutRule(view, layoutRule, anchorId)
        }
    }

    /**
     * Adds a constraint rule to a ConstraintLayout.
     *
     * @param layout The parent ConstraintLayout
     * @param view The target view
     * @param anchor The anchor view ID
     * @param constraintEdges Pair of constraint edges (source, target)
     */
    fun addConstraintRule(
        layout: ConstraintLayout?,
        view: View,
        anchor: String,
        constraintEdges: Pair<Int, Int>,
    ) {
        val constraintSet = ConstraintSet().apply {
            clone(layout)
        }

        val sourceId = view.id
        val targetViewId = when (anchor) {
            "parent" -> ConstraintSet.PARENT_ID
            else -> layout?.getViewID(anchor.extractViewId()) ?: ConstraintSet.PARENT_ID
        }

        if (config.isLoggingEnabled) {
            Log.d(TAG, "Adding constraint: view=$sourceId, target=$targetViewId")
        }

        constraintSet.connect(sourceId, constraintEdges.first, targetViewId, constraintEdges.second)
        constraintSet.applyTo(layout)
    }

    /**
     * Handles the background of a view, supporting various formats.
     *
     * @param view The target view
     * @param background The background value (color, drawable, or resource reference)
     * @param context The application context
     */
    fun handleBackground(view: View, background: String?, context: Context) {
        background?.let {
            when {
                it.isColor() -> view.setBackgroundColor(getColor(it, context, Color.TRANSPARENT))
                it.startsWith("@$RESOURCE_TYPE_DRAWABLE/") -> {
                    getDrawable(
                        view, it.removePrefix("@$RESOURCE_TYPE_DRAWABLE/")
                    )?.let { drawable ->
                        view.background = drawable
                    } ?: run {
                        if (config.isLoggingEnabled) Log.w(TAG, "Drawable not found: $it")
                    }
                }

                else -> {
                    if (config.isLoggingEnabled) Log.w(TAG, "Unsupported background format: $it")
                    view.background = Color.TRANSPARENT.toDrawable()
                }
            }
        }
    }

    /**
     * Handles the foreground of a view, supporting various formats.
     *
     * @param view The target view
     * @param source The foreground source
     * @param context The application context
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun handleForeground(view: View, source: String, context: Context) {
        when {
            source.isColor() -> view.foreground =
                getColor(source, context, Color.TRANSPARENT).toDrawable()

            source.startsWith("?$RESOURCE_TYPE_ATTR") -> {
                val attributeName = source.removePrefix("?$RESOURCE_TYPE_ATTR")
                val attributeId = config.provider.getResId(RESOURCE_TYPE_ATTR, attributeName)
                if (attributeId != -1) {
                    view.foreground = getResourceFromAttribute(attributeId, context) {
                        ContextCompat.getDrawable(context, it.resourceId)
                    }
                }
            }

            source.startsWith("@") -> {
                getDrawableFromResource(source, context)?.let { drawable ->
                    view.foreground = drawable
                    if (drawable is AnimationDrawable) drawable.start()
                }
            }

            source == "ripple" -> {
                view.foreground = getResourceFromAttribute(
                    android.R.attr.selectableItemBackground, context
                ) { ContextCompat.getDrawable(context, it.resourceId) }
                view.isClickable = true
            }

            else -> view.foreground = null
        }
    }

    /**
     * Handles the transformation method for a TextView.
     *
     * @param view The target TextView
     * @param transformation The transformation type
     */
    fun handleTransformationMethod(view: TextView, transformation: String) {
        view.transformationMethod = when (transformation) {
            TRANSFORM_ALL_CAPS -> {
                view.isAllCaps = true
                null
            }

            TRANSFORM_PASSWORD -> PasswordTransformationMethod.getInstance()
            TRANSFORM_SINGLE_LINE -> SingleLineTransformationMethod.getInstance()
            TRANSFORM_DIGITS -> {
                view.keyListener = DigitsKeyListener.getInstance("0123456789")
                null
            }

            TRANSFORM_REVERSE -> ReverseTransformation()
            else -> null
        }
    }

    /**
     * Handles scrollbar visibility for a view.
     *
     * @param view The target view
     * @param type The scrollbar type ("horizontal" or "vertical")
     */
    fun handleScrollbar(view: View, type: String) {
        when (type.lowercase()) {
            "h", "horizontal" -> view.isHorizontalScrollBarEnabled = true
            "v", "vertical" -> view.isVerticalScrollBarEnabled = true
            else -> throw IllegalArgumentException("Invalid scrollbar type: $type")
        }
    }

    /**
     * Handles drawable positioning for a TextView.
     *
     * @param view The target TextView
     * @param drawableRes The drawable resource
     * @param position The position to place the drawable
     */
    fun handleDrawablePosition(
        view: TextView,
        drawableRes: String,
        position: DrawablePosition,
    ) {
        val drawable = getDrawableFromResource(drawableRes, view.context)
        val drawables = view.compoundDrawablesRelative

        val updatedDrawables = when (position) {
            DrawablePosition.START -> arrayOf(drawable, drawables[1], drawables[2], drawables[3])
            DrawablePosition.END -> arrayOf(drawables[0], drawable, drawables[2], drawables[3])
            DrawablePosition.TOP -> arrayOf(drawables[0], drawables[1], drawable, drawables[3])
            DrawablePosition.BOTTOM -> arrayOf(drawables[0], drawables[1], drawables[2], drawable)
        }

        view.setCompoundDrawablesRelativeWithIntrinsicBounds(
            updatedDrawables[0], updatedDrawables[1], updatedDrawables[2], updatedDrawables[3]
        )
    }

    /**
     * Handles tinting for a view.
     *
     * @param view The target view
     * @param colorValue The color value
     */
    fun handleTint(view: View, colorValue: String) {
        val color = getColor(colorValue, view.context, Color.BLACK)

        when (view) {
            is ImageView -> view.setColorFilter(color)
            is TextView -> applyTextViewTint(view, color)
            else -> if (config.isLoggingEnabled) {
                Log.w(TAG, "Tint not supported for ${view::class.java}")
            }
        }
    }

    /**
     * Handles tint mode for a view.
     *
     * @param view The target view
     * @param modeValue The tint mode value
     */
    fun handleTintMode(view: View, modeValue: String) {
        val mode = parsePorterDuff(modeValue)

        when (view) {
            is ImageView -> view.imageTintMode = mode
            is CheckBox, is RadioButton, is Switch -> view.buttonTintMode = mode
            is TextView -> applyTextViewTintMode(view, mode)
            else -> if (config.isLoggingEnabled) {
                Log.w(TAG, "TintMode not supported for ${view::class.java}")
            }
        }
    }

    private fun applyTextViewTint(view: TextView, color: Int) {
        view.compoundDrawablesRelative.map { drawable ->
            drawable?.mutate()?.apply { setTint(color) }
        }.toTypedArray().let { drawables ->
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawables[0], drawables[1], drawables[2], drawables[3]
            )
        }
    }

    private fun applyTextViewTintMode(view: TextView, mode: PorterDuff.Mode) {
        view.compoundDrawablesRelative.map { drawable ->
            drawable?.mutate()?.apply { setTintMode(mode) }
        }.toTypedArray().let { drawables ->
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawables[0], drawables[1], drawables[2], drawables[3]
            )
        }
    }

    private inline fun <T> getResourceFromAttribute(
        attributeId: Int,
        context: Context,
        resourceCallback: (TypedValue) -> T,
    ): T = TypedValue().apply {
        context.theme.resolveAttribute(attributeId, this, true)
    }.let(resourceCallback)

    private fun getDrawableFromResource(resource: String, context: Context): Drawable? {
        if (!resource.startsWith("@")) return null

        val (type, name) = resource.removePrefix("@").split("/", limit = 2)
        val resourceId = config.provider.getResId(type, name)

        return when (type) {
            RESOURCE_TYPE_COLOR -> ContextCompat.getColor(context, resourceId).toDrawable()
            else -> ContextCompat.getDrawable(context, resourceId)
        }
    }

    /**
     * Sets the chain style for a view in a ConstraintLayout.
     *
     * @param layout The parent ConstraintLayout
     * @param view The target view
     * @param orientation The chain orientation (horizontal or vertical)
     * @param style The chain style (spread, spread_inside, or packed)
     */
    fun setChainStyle(
        layout: ConstraintLayout?,
        view: View,
        orientation: Int,
        style: String,
    ) {
        if (layout == null) return

        val styleValue = when (style.lowercase()) {
            "spread" -> ConstraintSet.CHAIN_SPREAD
            "spread_inside" -> ConstraintSet.CHAIN_SPREAD_INSIDE
            "packed" -> ConstraintSet.CHAIN_PACKED
            else -> ConstraintSet.CHAIN_SPREAD
        }

        ConstraintSet().apply {
            clone(layout)
            when (orientation) {
                ConstraintSet.HORIZONTAL -> setHorizontalChainStyle(view.id, styleValue)
                ConstraintSet.VERTICAL -> setVerticalChainStyle(view.id, styleValue)
            }
            applyTo(layout)
        }
    }

    /**
     * Sets the dimension ratio for a view in a ConstraintLayout.
     *
     * @param layout The parent ConstraintLayout
     * @param view The target view
     * @param ratio The dimension ratio (e.g., "16:9" or "0.5")
     */
    fun setDimensionRatio(
        layout: ConstraintLayout?,
        view: View,
        ratio: String,
    ) {
        if (layout == null) return

        ConstraintSet().apply {
            clone(layout)
            setDimensionRatio(view.id, ratio)
            applyTo(layout)
        }
    }

    /**
     * Sets the bias for a view in a ConstraintLayout.
     *
     * @param layout The parent ConstraintLayout
     * @param view The target view
     * @param isVertical Whether to set vertical (true) or horizontal (false) bias
     * @param bias The bias value (0.0 to 1.0)
     */
    fun setConstraintLayoutBias(
        layout: ConstraintLayout?,
        view: View,
        isVertical: Boolean,
        bias: Float,
    ) {
        if (layout == null) return

        ConstraintSet().apply {
            clone(layout)
            if (isVertical) {
                setVerticalBias(view.id, bias)
            } else {
                setHorizontalBias(view.id, bias)
            }
            applyTo(layout)
        }
    }

    /**
     * Handles foreground tint application to a view with optimized performance and memory usage.
     *
     * This method efficiently applies foreground tint to a view based on the provided tint source.
     * It supports color values, attribute references, and handles null cases efficiently.
     *
     * Performance optimizations:
     * - Early return for empty tint
     * - Efficient string operations
     * - Optimized color state list creation
     * - Reduced object allocations
     *
     * @param targetView The view to apply the tint to
     * @param tintSource The tint source (color value or attribute reference)
     * @param applicationContext The application context for resource resolution
     * @throws IllegalArgumentException if the tint format is invalid
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun handleForegroundTint(targetView: View, tintSource: String, applicationContext: Context) {
        if (tintSource.isEmpty()) {
            targetView.foregroundTintList = null
            return
        }

        targetView.foregroundTintList = when {
            tintSource.isColor() -> ColorStateList.valueOf(
                getColor(
                    tintSource, applicationContext, Color.TRANSPARENT
                )
            )

            tintSource.startsWith("?attr/") -> {
                val attributeId =
                    config.provider.getResId("attr", tintSource.removePrefix("?attr/"))
                if (attributeId != -1) {
                    getResourceFromAttribute(attributeId, applicationContext) {
                        ColorStateList.valueOf(it.data)
                    }
                } else null
            }

            else -> throw IllegalArgumentException("Invalid tint format: $tintSource")
        }
    }

    /**
     * Efficiently loads a font from system fonts or assets with optimized performance.
     * Thread-safe operation with efficient resource handling.
     *
     * Performance Considerations:
     * - Efficient font loading
     * - Minimal object creation
     * - Safe resource handling
     * - Optimized asset loading
     *
     * @param context The context for asset access
     * @param fontName The name of the font to load
     * @return The loaded Typeface or null if loading fails
     */
    fun loadFontFromAttribute(context: Context, fontName: String): Typeface? {
        return try {
            val cacheKey = "font:$fontName"
            getCachedResource<Typeface>(cacheKey) ?: when (fontName.substringAfter("/")) {
                "sans-serif" -> Typeface.SANS_SERIF
                "serif" -> Typeface.SERIF
                "monospace" -> Typeface.MONOSPACE
                else -> Typeface.createFromAsset(context.assets, "fonts/$fontName.ttf")
            }.also { setCachedResource(cacheKey, it) }
        } catch (e: Exception) {
            logger.error("loadFontFromAttribute", "Failed to load font: $fontName", e)
            null
        }
    }
}