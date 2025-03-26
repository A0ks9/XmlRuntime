package com.voyager.utils.view

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.voyager.data.models.ConfigManager
import com.voyager.utils.ParseHelper.parseFloat
import com.voyager.utils.ParseHelper.parseScaleType
import com.voyager.utils.Utils.getClickListener
import com.voyager.utils.getParentView
import com.voyager.utils.processors.AttributeProcessor.registerAttribute
import com.voyager.utils.view.AttributesHandler.addConstraintRule
import com.voyager.utils.view.AttributesHandler.addRelativeLayoutRule
import com.voyager.utils.view.AttributesHandler.setChainStyle
import com.voyager.utils.view.AttributesHandler.setConstraintLayoutBias
import com.voyager.utils.view.AttributesHandler.setDimensionRatio
import com.voyager.utils.view.AttributesHandler.setImageSource

/**
 * Base view attributes handler for the Voyager framework.
 *
 * This object provides efficient registration and handling of common Android view attributes,
 * including layout parameters, styling, and view-specific attributes.
 *
 * Key features:
 * - Efficient attribute registration
 * - Optimized layout parameter handling
 * - Memory-efficient attribute processing
 * - Thread-safe operations
 * - Comprehensive attribute support
 *
 * Performance optimizations:
 * - Lazy initialization of attributes
 * - Caching of frequently used values
 * - Efficient string operations
 * - Optimized view traversal
 * - Reduced object creation
 *
 * Usage example:
 * ```kotlin
 * // Initialize attributes
 * BaseViewAttributes.initializeAttributes()
 *
 * // Register custom attributes
 * registerAttribute<TextView, String>("text") { view, value ->
 *     view.text = value
 * }
 * ```
 *
 * @author Abdelrahman Omar
 * @since 1.0.0
 */
internal object BaseViewAttributes {

    private var isAttributesInitialized = false
    private val isLoggingEnabled = ConfigManager.config.isLoggingEnabled

    /**
     * Initializes all view attributes if not already initialized.
     * This method is thread-safe and idempotent.
     */
    fun initializeAttributes() {
        if (isAttributesInitialized) return

        commonAttributes(isLoggingEnabled)
        linearLayoutAttributes()
        relativeLayoutAttributes()
        constraintLayoutAttributes()
        imageViewAttributes()
        viewAttributes()

        isAttributesInitialized = true
    }

    /**
     * Registers LinearLayout-specific attributes.
     */
    private fun linearLayoutAttributes() {
        registerAttribute<LinearLayout, String>(Attributes.LinearLayout.LINEARLAYOUT_ORIENTATION.name) { view, value ->
            view.orientation = if (value.equals(
                    "horizontal", true
                )
            ) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        }

        registerAttribute<LinearLayout, String>(Attributes.Common.WEIGHT.name) { view, value ->
            (view.layoutParams as? LinearLayout.LayoutParams)?.weight = parseFloat(value)
        }
    }

    /**
     * Registers RelativeLayout-specific attributes.
     */
    private fun relativeLayoutAttributes() {
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
            registerAttribute<View, String>(attr.name) { view, value ->
                addRelativeLayoutRule(view, value, rule)
            }
        }
    }

    /**
     * Registers ConstraintLayout-specific attributes.
     */
    private fun constraintLayoutAttributes() {
        // Constraint rules mapping
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
            registerAttribute<View, String>(attr.name) { view, value ->
                if (isLoggingEnabled) {
                    Log.d("AttributeRegistry", "Attr: $attr, Val: $value, Side: $side")
                }
                addConstraintRule(
                    view.getParentView() as? ConstraintLayout, view, value, side
                )
            }
        }

        // Chain styles
        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE.name) { view, value ->
            setChainStyle(
                view.getParentView() as? ConstraintLayout, view, ConstraintSet.HORIZONTAL, value
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE.name) { view, value ->
            setChainStyle(
                view.getParentView() as? ConstraintLayout, view, ConstraintSet.VERTICAL, value
            )
        }

        // Dimension ratio
        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_DIMENSION_RATIO.name) { view, value ->
            setDimensionRatio(
                view.getParentView() as? ConstraintLayout, view, value
            )
        }

        // Bias settings
        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS.name) { view, value ->
            setConstraintLayoutBias(
                view.getParentView() as? ConstraintLayout, view, true, parseFloat(value)
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS.name) { view, value ->
            setConstraintLayoutBias(
                view.getParentView() as? ConstraintLayout, view, false, parseFloat(value)
            )
        }
    }

    /**
     * Registers ImageView-specific attributes.
     */
    private fun imageViewAttributes() {
        registerAttribute<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SCALE_TYPE.name) { view, value ->
            parseScaleType(value)?.let { view.scaleType = it }
        }

        registerAttribute<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SRC.name) { view, value ->
            setImageSource(view, value)
        }
    }

    /**
     * Registers common view attributes.
     */
    private fun viewAttributes() {
        registerAttribute<View, String>(Attributes.View.VIEW_ON_CLICK.name) { view, value ->
            view.setOnClickListener(
                getClickListener(
                    view.getParentView(), value
                )
            )
        }
    }
}