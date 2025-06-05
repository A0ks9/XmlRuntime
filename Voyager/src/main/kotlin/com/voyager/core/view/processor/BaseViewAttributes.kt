package com.voyager.core.view.processor

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.voyager.core.attribute.AttributeRegistry.register
import com.voyager.core.model.Attributes
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.utils.parser.ImageViewParser.parseScaleType
import com.voyager.core.utils.parser.NumericalParser.parseFloat
import com.voyager.core.view.processor.AttributesHandler.addConstraintRule
import com.voyager.core.view.processor.AttributesHandler.addRelativeLayoutRule
import com.voyager.core.view.processor.AttributesHandler.setChainStyle
import com.voyager.core.view.processor.AttributesHandler.setConstraintLayoutBias
import com.voyager.core.view.processor.AttributesHandler.setDimensionRatio
import com.voyager.core.view.processor.AttributesHandler.setImageSource
import com.voyager.core.view.processor.CommonAttributes.commonAttributes
import com.voyager.core.view.utils.ViewExtensions.getParentView
import com.voyager.core.view.utils.event.ReflectionUtils.getClickListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Base view attributes handler for the Voyager framework.
 * Provides efficient and thread-safe attribute registration for Android views.
 *
 * Key Features:
 * - Core Android view attribute registration
 * - Layout-specific attribute handling
 * - Thread-safe operations
 * - Performance optimized
 * - Memory efficient
 * - Comprehensive error handling
 * - Attribute validation
 * - Cache management
 * - Resource cleanup
 *
 * Performance Optimizations:
 * - Efficient attribute registration
 * - Minimal object creation
 * - Safe resource handling
 * - Optimized layout parameter management
 * - Attribute caching
 * - Resource pooling
 *
 * Best Practices:
 * 1. Initialize attributes only once
 * 2. Handle null values appropriately
 * 3. Consider view lifecycle
 * 4. Use thread-safe operations
 * 5. Consider memory leaks
 * 6. Implement proper error handling
 * 7. Validate attributes before use
 * 8. Clean up resources properly
 *
 * Example Usage:
 * ```kotlin
 * // Initialize base view attributes
 * BaseViewAttributes.initializeAttributes()
 *
 * // Register custom attributes
 * register<View, String>("customAttribute") { view, value ->
 *     view.setCustomValue(value)
 * }
 *
 * // Clear attribute cache
 * BaseViewAttributes.clearCache()
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
internal object BaseViewAttributes {
    private val logger = LoggerFactory.getLogger(BaseViewAttributes::class.java.simpleName)
    private val isAttributesInitialized = AtomicBoolean(false)
    private val isLoggingEnabled by lazy { ConfigManager.config.isLoggingEnabled }
    private val attributeCache = ConcurrentHashMap<String, Any>()

    /**
     * Initializes all base view attributes if not already initialized.
     * Thread-safe operation with efficient attribute registration.
     *
     * Performance Considerations:
     * - Initialize attributes only once
     * - Use efficient attribute handlers
     * - Consider attribute dependencies
     * - Handle attribute validation
     * - Cache attribute values
     *
     * @throws IllegalStateException if initialization fails
     */
    fun initializeAttributes() {
        try {
            if (!isAttributesInitialized.compareAndSet(false, true)) {
                logger.debug("initializeAttributes", "Attributes already initialized")
                return
            }

            // Register attributes for different view types
            commonAttributes(isLoggingEnabled)
            linearLayoutAttributes()
            relativeLayoutAttributes()
            constraintLayoutAttributes()
            imageViewAttributes()
            viewAttributes()

            logger.debug("initializeAttributes", "Base view attributes initialized successfully")
        } catch (e: Exception) {
            isAttributesInitialized.set(false)
            throw IllegalStateException(
                "Failed to initialize base view attributes: ${e.message}", e
            )
        }
    }

    /**
     * Clears the attribute cache.
     * Thread-safe operation that removes all cached attribute values.
     *
     * Performance Considerations:
     * - Clear cache only when necessary
     * - Consider memory impact
     * - Handle concurrent access
     */
    fun clearCache() {
        try {
            attributeCache.clear()
            logger.debug("clearCache", "Attribute cache cleared successfully")
        } catch (e: Exception) {
            logger.error("clearCache", "Failed to clear attribute cache: ${e.message}")
        }
    }

    /**
     * Registers attributes specific to [LinearLayout].
     * Thread-safe operation with efficient attribute handling.
     *
     * Performance Considerations:
     * - Efficient attribute registration
     * - Minimal object creation
     * - Safe resource handling
     * - Optimized layout parameter management
     *
     * @throws IllegalStateException if registration fails
     */
    private fun linearLayoutAttributes() {
        try {
            register<LinearLayout, String>(Attributes.LinearLayout.LINEARLAYOUT_ORIENTATION) { view, value ->
                view.orientation = if (value.equals("horizontal", true)) {
                    LinearLayout.HORIZONTAL
                } else {
                    LinearLayout.VERTICAL
                }
            }

            register<LinearLayout, String>(Attributes.Common.WEIGHT) { view, value ->
                (view.layoutParams as? LinearLayout.LayoutParams)?.weight = parseFloat(value)
            }

            logger.debug(
                "linearLayoutAttributes", "LinearLayout attributes registered successfully"
            )
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to register LinearLayout attributes: ${e.message}", e
            )
        }
    }

    /**
     * Registers attributes specific to [RelativeLayout].
     * Thread-safe operation with efficient attribute handling.
     *
     * Performance Considerations:
     * - Efficient attribute registration
     * - Minimal object creation
     * - Safe resource handling
     * - Optimized layout parameter management
     *
     * @throws IllegalStateException if registration fails
     */
    private fun relativeLayoutAttributes() {
        try {
            mapOf(
                Attributes.View.VIEW_ABOVE to RelativeLayout.ABOVE,
                Attributes.View.VIEW_BELOW to RelativeLayout.BELOW,
                Attributes.View.VIEW_TO_LEFT_OF to RelativeLayout.LEFT_OF,
                Attributes.View.VIEW_TO_RIGHT_OF to RelativeLayout.RIGHT_OF,
                Attributes.View.VIEW_ALIGN_TOP to RelativeLayout.ALIGN_TOP,
                Attributes.View.VIEW_ALIGN_BOTTOM to RelativeLayout.ALIGN_BOTTOM,
                Attributes.View.VIEW_ALIGN_PARENT_TOP to RelativeLayout.ALIGN_PARENT_TOP,
                Attributes.View.VIEW_ALIGN_PARENT_BOTTOM to RelativeLayout.ALIGN_PARENT_BOTTOM,
                Attributes.View.VIEW_ALIGN_START to RelativeLayout.ALIGN_START,
                Attributes.View.VIEW_ALIGN_END to RelativeLayout.ALIGN_END,
                Attributes.View.VIEW_ALIGN_PARENT_START to RelativeLayout.ALIGN_PARENT_START,
                Attributes.View.VIEW_ALIGN_PARENT_END to RelativeLayout.ALIGN_PARENT_END
            ).forEach { (attr, rule) ->
                register<View, String>(attr) { view, value ->
                    addRelativeLayoutRule(view, value, rule)
                }
            }

            logger.debug(
                "relativeLayoutAttributes", "RelativeLayout attributes registered successfully"
            )
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to register RelativeLayout attributes: ${e.message}", e
            )
        }
    }

    /**
     * Registers attributes specific to [ConstraintLayout].
     * Thread-safe operation with efficient attribute handling.
     *
     * Performance Considerations:
     * - Efficient attribute registration
     * - Minimal object creation
     * - Safe resource handling
     * - Optimized layout parameter management
     *
     * @throws IllegalStateException if registration fails
     */
    private fun constraintLayoutAttributes() {
        try {
            // Constraint connection rules mapping
            mapOf(
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF to (ConstraintSet.LEFT to ConstraintSet.LEFT),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF to (ConstraintSet.LEFT to ConstraintSet.RIGHT),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF to (ConstraintSet.RIGHT to ConstraintSet.LEFT),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF to (ConstraintSet.RIGHT to ConstraintSet.RIGHT),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_TOP_OF to (ConstraintSet.TOP to ConstraintSet.TOP),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF to (ConstraintSet.TOP to ConstraintSet.BOTTOM),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF to (ConstraintSet.BOTTOM to ConstraintSet.TOP),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF to (ConstraintSet.BOTTOM to ConstraintSet.BOTTOM),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_START_OF to (ConstraintSet.START to ConstraintSet.START),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_START_TO_END_OF to (ConstraintSet.START to ConstraintSet.END),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_START_OF to (ConstraintSet.END to ConstraintSet.START),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_END_TO_END_OF to (ConstraintSet.END to ConstraintSet.END),
                Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF to (ConstraintSet.BASELINE to ConstraintSet.BASELINE)
            ).forEach { (attr, side) ->
                register<View, String>(attr) { view, value ->
                    if (isLoggingEnabled) {
                        logger.debug(
                            "constraintLayoutAttributes", "Attr: $attr, Val: $value, Side: $side"
                        )
                    }
                    val parent = view.getParentView() as? ConstraintLayout
                    if (parent != null) {
                        addConstraintRule(parent, view, value, side)
                    } else if (isLoggingEnabled) {
                        logger.error(
                            "constraintLayoutAttributes",
                            "Parent of view ${view.javaClass.simpleName} is not a ConstraintLayout. Cannot apply constraint rule $attr"
                        )
                    }
                }
            }

            // Chain styles
            register<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE) { view, value ->
                val parent = view.getParentView() as? ConstraintLayout
                if (parent != null) setChainStyle(parent, view, ConstraintSet.HORIZONTAL, value)
            }

            register<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE) { view, value ->
                val parent = view.getParentView() as? ConstraintLayout
                if (parent != null) setChainStyle(parent, view, ConstraintSet.VERTICAL, value)
            }

            // Dimension ratio
            register<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_DIMENSION_RATIO) { view, value ->
                val parent = view.getParentView() as? ConstraintLayout
                if (parent != null) setDimensionRatio(parent, view, value)
            }

            // Bias settings
            register<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS) { view, value ->
                val parent = view.getParentView() as? ConstraintLayout
                if (parent != null) setConstraintLayoutBias(parent, view, true, parseFloat(value))
            }

            register<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS) { view, value ->
                val parent = view.getParentView() as? ConstraintLayout
                if (parent != null) setConstraintLayoutBias(parent, view, false, parseFloat(value))
            }

            logger.debug(
                "constraintLayoutAttributes", "ConstraintLayout attributes registered successfully"
            )
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to register ConstraintLayout attributes: ${e.message}", e
            )
        }
    }

    /**
     * Registers attributes specific to [ImageView].
     * Thread-safe operation with efficient attribute handling.
     *
     * Performance Considerations:
     * - Efficient attribute registration
     * - Minimal object creation
     * - Safe resource handling
     * - Optimized image loading
     *
     * @throws IllegalStateException if registration fails
     */
    private fun imageViewAttributes() {
        try {
            register<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SCALE_TYPE) { view, value ->
                parseScaleType(value)?.let { view.scaleType = it }
            }

            register<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SRC) { view, value ->
                setImageSource(view, value)
            }

            logger.debug("imageViewAttributes", "ImageView attributes registered successfully")
        } catch (e: Exception) {
            throw IllegalStateException("Failed to register ImageView attributes: ${e.message}", e)
        }
    }

    /**
     * Registers common attributes applicable to the base [View] class.
     * Thread-safe operation with efficient attribute handling.
     *
     * Performance Considerations:
     * - Efficient attribute registration
     * - Minimal object creation
     * - Safe resource handling
     * - Optimized event handling
     *
     * @throws IllegalStateException if registration fails
     */
    private fun viewAttributes() {
        try {
            register<View, String>(Attributes.View.VIEW_ON_CLICK) { view, value ->
                view.setOnClickListener(getClickListener(view.getParentView(), value))
            }

            logger.debug("viewAttributes", "Base View attributes registered successfully")
        } catch (e: Exception) {
            throw IllegalStateException("Failed to register base View attributes: ${e.message}", e)
        }
    }
}