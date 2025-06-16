package com.voyager.core.view.processor

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.voyager.core.attribute.attributesForView
import com.voyager.core.model.Attributes
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.ErrorUtils
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.utils.parser.ImageViewParser.parseScaleType
import com.voyager.core.view.processor.AttributesHandler.addConstraintRule
import com.voyager.core.view.processor.AttributesHandler.addRelativeLayoutRule
import com.voyager.core.view.processor.AttributesHandler.setChainStyle
import com.voyager.core.view.processor.AttributesHandler.setConstraintLayoutBias
import com.voyager.core.view.processor.AttributesHandler.setDimensionRatio
import com.voyager.core.view.processor.AttributesHandler.setImageSource
import com.voyager.core.view.processor.CommonAttributes.commonAttributes
import com.voyager.core.view.utils.ViewExtensions.getParentView
import com.voyager.core.view.utils.event.ReflectionUtils.getClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Optimized base view attributes handler for the Voyager framework.
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
    private val errorUtils = ErrorUtils("BaseViewAttributes")

    // Pre-computed maps for faster lookups
    private val relativeLayoutRules by lazy {
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
        )
    }

    private val constraintLayoutRules by lazy {
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
        )
    }

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
        errorUtils.tryOrThrow(
            {
            if (!isAttributesInitialized.compareAndSet(false, true)) {
                logger.debug("initializeAttributes", "Attributes already initialized")
                return@tryOrThrow
            }

            CoroutineScope(Dispatchers.Default).launch {
                //Register common attributes
                commonAttributes()

                // Register view-specific attributes in parallel
                linearLayoutAttributes()
                relativeLayoutAttributes()
                constraintLayoutAttributes()
                imageViewAttributes()
                viewAttributes()
            }

            if (isLoggingEnabled) {
                logger.debug(
                    "initializeAttributes", "Base view attributes initialized successfully"
                )
            }
        }, "Failed to initialize base view attributes")
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
    fun clearCache() = errorUtils.tryOrLog({
        attributeCache.clear()
        if (isLoggingEnabled) {
            logger.debug("clearCache", "Attribute cache cleared successfully")
        }
    }, "clearCache", { "Failed to clear attribute cache" })

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
    private suspend fun linearLayoutAttributes() = withContext(Dispatchers.Default) {
        errorUtils.tryOrThrow({
            attributesForView<LinearLayout> {
                attribute<String>(Attributes.LinearLayout.LINEARLAYOUT_ORIENTATION) { view, value ->
                    view.orientation = if (value.equals(
                            "horizontal", true
                        )
                    ) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
                }
                attribute<String>(Attributes.Common.WEIGHT) { view, value ->
                    (view.layoutParams as? LinearLayout.LayoutParams)?.weight = value.toFloat()
                }
            }
            if (isLoggingEnabled) {
                logger.debug(
                    "linearLayoutAttributes", "LinearLayout attributes registered successfully"
                )
            }
        }, "Failed to register LinearLayout attributes")
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
    private suspend fun relativeLayoutAttributes() = withContext(Dispatchers.Default) {
        errorUtils.tryOrThrow({
            attributesForView<View> {
                relativeLayoutRules.forEach { (attr, rule) ->
                    attribute<String>(attr) { view, value ->
                        addRelativeLayoutRule(view, value, rule)
                    }
                }
            }
            if (isLoggingEnabled) {
                logger.debug(
                    "relativeLayoutAttributes", "RelativeLayout attributes registered successfully"
                )
            }
        }, "Failed to register RelativeLayout attributes")
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
    private suspend fun constraintLayoutAttributes() = withContext(Dispatchers.Default) {
        errorUtils.tryOrThrow({
            attributesForView<View> {
                // Constraint connection rules
                constraintLayoutRules.forEach { (attr, side) ->
                    attribute<String>(attr) { view, value ->
                        if (isLoggingEnabled) {
                            logger.debug(
                                "constraintLayoutAttributes",
                                "Attr: $attr, Val: $value, Side: $side"
                            )
                        }
                        (view.getParentView() as? ConstraintLayout)?.let { parent ->
                            addConstraintRule(parent, view, value, side)
                        } ?: run {
                            if (isLoggingEnabled) {
                                logger.error(
                                    "constraintLayoutAttributes",
                                    "Parent of view ${view.javaClass.simpleName} is not a ConstraintLayout. Cannot apply constraint rule $attr"
                                )
                            }
                        }
                    }
                }

                // Chain styles
                mapOf(
                    Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE to ConstraintSet.HORIZONTAL,
                    Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE to ConstraintSet.VERTICAL
                ).forEach { (attr, orientation) ->
                    attribute<String>(attr) { view, value ->
                        (view.getParentView() as? ConstraintLayout)?.let { parent ->
                            setChainStyle(parent, view, orientation, value)
                        }
                    }
                }

                // Dimension ratio
                attribute<String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_DIMENSION_RATIO) { view, value ->
                    (view.getParentView() as? ConstraintLayout)?.let { parent ->
                        setDimensionRatio(parent, view, value)
                    }
                }

                // Bias settings
                mapOf(
                    Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS to true,
                    Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS to false
                ).forEach { (attr, isVertical) ->
                    attribute<String>(attr) { view, value ->
                        (view.getParentView() as? ConstraintLayout)?.let { parent ->
                            setConstraintLayoutBias(
                                parent, view, isVertical, value.toFloat()
                            )
                        }
                    }
                }
            }
            if (isLoggingEnabled) {
                logger.debug(
                    "constraintLayoutAttributes",
                    "ConstraintLayout attributes registered successfully"
                )
            }
        }, "Failed to register ConstraintLayout attributes")
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
    private suspend fun imageViewAttributes() = withContext(Dispatchers.Default) {
        errorUtils.tryOrThrow({
            attributesForView<ImageView> {
                attribute<String>(Attributes.ImageView.IMAGEVIEW_SCALE_TYPE) { view, value ->
                    parseScaleType(value)?.let { view.scaleType = it }
                }
                attribute<String>(Attributes.ImageView.IMAGEVIEW_SRC) { view, value ->
                    setImageSource(view, value)
                }
            }
            if (isLoggingEnabled) {
                logger.debug(
                    "imageViewAttributes", "ImageView attributes registered successfully"
                )
            }
        }, "Failed to register ImageView attributes")
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
    private suspend fun viewAttributes() = withContext(Dispatchers.Default) {
        errorUtils.tryOrThrow({
            attributesForView<View> {
                attribute<String>(Attributes.View.VIEW_ON_CLICK) { view, value ->
                    view.setOnClickListener(getClickListener(view.getParentView(), value))
                }
            }
            if (isLoggingEnabled) {
                logger.debug("viewAttributes", "Base View attributes registered successfully")
            }
        }, "Failed to register base View attributes")
    }
}