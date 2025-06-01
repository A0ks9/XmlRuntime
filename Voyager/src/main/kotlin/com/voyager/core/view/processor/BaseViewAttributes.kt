package com.voyager.core.view.processor

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.voyager.core.attribute.AttributeRegistry.register
import com.voyager.core.model.Attributes
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.parser.ImageViewParser.parseScaleType
import com.voyager.core.utils.parser.NumericalParser.parseFloat
import com.voyager.core.view.processor.AttributesHandler.addConstraintRule
import com.voyager.core.view.processor.AttributesHandler.addRelativeLayoutRule
import com.voyager.core.view.processor.AttributesHandler.setChainStyle
import com.voyager.core.view.processor.AttributesHandler.setConstraintLayoutBias
import com.voyager.core.view.processor.AttributesHandler.setDimensionRatio
import com.voyager.core.view.processor.AttributesHandler.setImageSource
import com.voyager.core.view.processor.BaseViewAttributes.initializeAttributes
import com.voyager.core.view.utils.ViewExtensions.getParentView
import com.voyager.core.view.utils.event.ReflectionUtils.getClickListener
import com.voyager.core.utils.logging.LoggerFactory
import com.voyager.core.view.utils.id.ViewIdUtils.extractViewId

/**
 * Base view attributes handler for the Voyager framework.
 *
 * This object is responsible for registering the core Android view attributes
 * that can be applied to views during the XML/JSON inflation process.
 * It groups attribute registrations by common view types (e.g., LinearLayout, RelativeLayout,
 * ConstraintLayout, ImageView) and general View attributes.
 *
 * Attribute registration is done via the [register] function,
 * mapping attribute names (strings) to specific handler logic.
 *
 * The initialization of these attributes should happen once via the [initializeAttributes] method.
 */
internal object BaseViewAttributes {

    private val logger = LoggerFactory.getLogger(BaseViewAttributes::class.java.simpleName)

    private var isAttributesInitialized = false
    private val isLoggingEnabled = ConfigManager.config.isLoggingEnabled

    /**
     * Initializes all base view attributes if not already initialized.
     *
     * This method is idempotent and thread-safe, ensuring that attributes are registered
     * only once throughout the application lifecycle.
     */
    fun initializeAttributes() {
        if (isAttributesInitialized) return

        // Register attributes for different view types
        commonAttributes(isLoggingEnabled)
        linearLayoutAttributes()
        relativeLayoutAttributes()
        constraintLayoutAttributes()
        imageViewAttributes()
        viewAttributes()

        isAttributesInitialized = true
    }

    /**
     * Registers common attributes applicable to most views.
     *
     * Note: This function is currently internal and should be refactored or made private
     * if it's not intended for external use or if common attributes are handled differently.
     * For now, keeping it as is based on the original structure.
     *
     * @param isLoggingEnabled Whether logging is enabled.
     */
    private fun commonAttributes(isLoggingEnabled: Boolean) {
        // ID attribute
        register<View, String>(Attributes.Common.ID.name) { targetView, attributeValue ->
            targetView.getParentView()?.getGeneratedViewInfo()?.let { info ->
                if (isLoggingEnabled) Log.d(
                    "view", "ID: $attributeValue, viewID: ${attributeValue.extractViewId()}"
                )
                targetView.id = View.generateViewId().also {
                    info.viewID[attributeValue.extractViewId()] = it
                }
            }
        }

        // Width and height attributes
        widthAndHeightAttributes()

        // Gravity attribute
        register<View, String>(Attributes.Common.GRAVITY.name) { targetView, attributeValue ->
            val alignment = parseGravity(attributeValue)
            when (val layoutParams = targetView.layoutParams) {
                is LinearLayout.LayoutParams -> layoutParams.gravity = alignment
                is FrameLayout.LayoutParams -> layoutParams.gravity = alignment
                else -> try {
                    if (targetView is TextView) targetView.gravity = alignment
                } catch (e: Exception) {
                    if (isLoggingEnabled) Log.w(
                        "view", "Gravity not applicable: ${targetView.javaClass.simpleName}", e
                    )
                }
            }
        }

        // Margin and padding attributes
        marginAndPaddingAttributes()

        // Basic view attributes
        registerBasicViewAttributes(isLoggingEnabled)

        // Background and foreground attributes
        registerBackgroundAttributes()

        // Transform attributes
        registerTransformAttributes()

        // Layout attributes
        registerLayoutAttributes()

        // Text view specific attributes
        registerTextViewAttributes()

        // Accessibility attributes
        registerAccessibilityAttributes()

        // Drawable attributes
        registerDrawableAttributes()
    }

    /**
     * Registers attributes specific to [LinearLayout].
     *
     * Includes attributes like 'orientation' and 'layout_weight'.
     */
    private fun linearLayoutAttributes() {
        register<LinearLayout, String>(Attributes.LinearLayout.LINEARLAYOUT_ORIENTATION) { view, value ->
            view.orientation = if (value.equals(
                    "horizontal", true
                )
            ) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        }

        register<LinearLayout, String>(Attributes.Common.WEIGHT) { view, value ->
            (view.layoutParams as? LinearLayout.LayoutParams)?.weight = parseFloat(value)
        }
    }

    /**
     * Registers attributes specific to [RelativeLayout] and its layout parameters.
     *
     * This function uses a map to concisely register multiple relative positioning rules
     * like 'layout_above', 'layout_below', 'layout_alignTop', etc., by mapping the attribute
     * name to the corresponding [RelativeLayout] rule constant.
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
            register<View, String>(attr) { view, value ->
                addRelativeLayoutRule(view, value, rule)
            }
        }
    }

    /**
     * Registers attributes specific to [ConstraintLayout] and its layout parameters ([ConstraintSet]).
     *
     * This function registers various constraint-related attributes including connection rules
     * (e.g., 'layout_constraintStart_toStartOf'), chain styles, dimension ratios, and bias settings.
     * It uses maps to concisely define and register multiple similar constraint rules.
     */
    private fun constraintLayoutAttributes() {
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
                    logger.d("AttributeRegistry", "Attr: $attr, Val: $value, Side: $side")
                }
                // This requires getViewID and getParentView which are now in ViewExtensions
                val parent = view.getParentView() as? ConstraintLayout
                if (parent != null) {
                    addConstraintRule(parent, view, value, side)
                } else if (isLoggingEnabled) {
                    logger.e(
                        "BaseViewAttributes",
                        "Parent of view $view is not a ConstraintLayout. Cannot apply constraint rule $attr"
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
    }

    /**
     * Registers attributes specific to [ImageView].
     *
     * Includes attributes like 'scaleType' and 'src'.
     */
    private fun imageViewAttributes() {
        register<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SCALE_TYPE) { view, value ->
            parseScaleType(value)?.let { view.scaleType = it }
        }

        register<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SRC) { view, value ->
            setImageSource(view, value)
        }
    }

    /**
     * Registers common attributes applicable to the base [View] class.
     *
     * Includes attributes like 'onClick'.
     */
    private fun viewAttributes() {
        register<View, String>(Attributes.View.VIEW_ON_CLICK) { view, value ->
            // getClickListener is now in ReflectionUtils
            view.setOnClickListener(getClickListener(view.getParentView(), value))
        }
    }
}