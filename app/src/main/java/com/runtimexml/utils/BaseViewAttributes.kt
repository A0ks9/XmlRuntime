package com.runtimexml.utils

import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.Glide
import com.runtimexml.utils.ParseHelper.getColor
import com.runtimexml.utils.ParseHelper.getDrawable
import com.runtimexml.utils.ParseHelper.parseEllipsize
import com.runtimexml.utils.ParseHelper.parseFloat
import com.runtimexml.utils.ParseHelper.parseGravity
import com.runtimexml.utils.ParseHelper.parseInputType
import com.runtimexml.utils.ParseHelper.parseRelativeLayoutBoolean
import com.runtimexml.utils.ParseHelper.parseScaleType
import com.runtimexml.utils.ParseHelper.parseTextAlignment
import com.runtimexml.utils.ParseHelper.parseTextStyle
import com.runtimexml.utils.ParseHelper.parseVisibility
import com.runtimexml.utils.Utils.getClickListener
import com.runtimexml.utils.Utils.getGeneratedViewInfo
import com.runtimexml.utils.interfaces.AttributeProcessorRegistry
import com.runtimexml.utils.processors.AttributeRegistry
import com.runtimexml.utils.processors.AttributeRegistry.Companion.configureProcessor
import com.runtimexml.utils.processors.AttributeRegistry.Companion.registerAttribute

/**
 * `BaseViewAttributes` is a central object for managing and applying view attributes
 * to different UI elements. It provides a structured way to define how various XML
 * attributes should be translated into corresponding Android View properties.
 *
 * This object includes:
 * - A registry of `AttributeRegistry` instances, each responsible for handling a specific
 *   attribute or set of attributes.
 * - Initialization logic to populate this registry with processors for common attributes
 *   and attributes specific to various layout types (e.g., LinearLayout, RelativeLayout,
 *   ConstraintLayout) and View types (e.g., ImageView, TextView).
 * - Utility functions for common operations like setting margins, padding, sizes,
 *   and handling attribute values.
 *
 * The core functionality revolves around the `initializeAttributes` function, which
 * populates the `AttributeProcessors` set with `AttributeRegistry` instances,
 * each of which knows how to apply a specific attribute to a target view.
 *
 * The attribute processors added in this class cover a wide range of common view properties,
 * such as:
 * - `id`: Setting the unique identifier for a view.
 * - `width`, `height`: Defining the dimensions of a view.
 * - `gravity`: Aligning the content within a view or a view within its parent.
 * - `visibility`: Controlling whether a view is visible, invisible, or gone.
 * - `clickable`, `longClickable`: Enabling or disabling click interactions.
 * - `tag`: Attaching an arbitrary object to a view.
 * - `orientation`, `weight`: (LinearLayout) Configuring the layout direction and child view weights.
 * - `RelativeLayout` rules: Positioning a view relative to another view or its parent.
 * - `ConstraintLayout` constraints: Defining connections and constraints between views.
 * - `scaleType`, `src`: (ImageView) Controlling how an image */
internal object BaseViewAttributes {

    val AttributeProcessors = LinkedHashSet<Pair<String, AttributeProcessorRegistry<*, Any>>>()
    private const val wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT

    /**
     * Initializes the attribute registry with common and layout-specific attributes.
     *
     * This function populates the attribute registry, which is likely a central
     * repository for UI element attributes, with a set of predefined attributes.
     * It includes attributes common to all views, as well as attributes specific
     * to certain layout types (e.g., LinearLayout, RelativeLayout, ConstraintLayout)
     * and common UI elements (e.g., ImageView, TextView, View).
     *
     * The function first checks if the attribute registry already contains an ID.
     * If it does, it assumes that the registry has already been initialized and
     * returns early, preventing redundant initialization.
     *
     * If the registry is not initialized, it proceeds to populate it by invoking
     * various attribute registration functions within a lambda block:
     *
     * - `commonAttributes()`: Registers attributes that are applicable to most,
     *   if not all, UI elements.
     * - `linearLayoutAttributes()`: Registers attributes specific to
     *   LinearLayout views.
     * - `relativeLayoutAttributes()`: Registers attributes specific to
     *   RelativeLayout views.
     * - `constraintLayoutAttributes()`: Registers attributes specific to
     *   ConstraintLayout views.
     * - `imageViewAttributes()`: Registers attributes specific to ImageView
     *   views.
     * - `textViewAttributes()`: Registers attributes specific to TextView
     *   views.
     * - `viewAttributes()`: Registers attributes specific to base View
     *   views.
     *
     * This method ensures that the attribute registry is properly set up before
     * UI elements are created or manipulated.
     *
     * @see attributeRegistryContainsId
     * @see configureProcessor
     * @see commonAttributes
     * @see linearLayoutAttributes
     * @see relativeLayoutAttributes
     * @see constraintLayoutAttributes
     * @see imageViewAttributes
     * @see textViewAttributes
     * @see viewAttributes
     */
    fun initializeAttributes() {
        if (attributeRegistryContainsId()) return

        configureProcessor {
            commonAttributes()
            linearLayoutAttributes()
            relativeLayoutAttributes()
            constraintLayoutAttributes() // Added ConstraintLayout attributes
            imageViewAttributes()
            textViewAttributes()
            viewAttributes()
        }
    }

    private fun AttributeRegistry.commonAttributes() {
        registerAttribute<View, String>(Attributes.Common.ID) { targetView, attributeValue ->
            targetView.getParentView()?.getGeneratedViewInfo()?.let { info ->
                Log.d(
                    "ViewAttributes",
                    "Setting ID: $attributeValue and viewID: ${attributeValue.extractViewId()}"
                )
                targetView.id =
                    View.generateViewId().also { info.viewID[attributeValue.extractViewId()] = it }
            }
        }

        widthAndHeightAttributes()

        registerAttribute<View, String>(Attributes.Common.GRAVITY) { targetView, attributeValue ->
            val alignment = parseGravity(attributeValue)
            when (targetView.getParentView()) {
                is LinearLayout -> (targetView.layoutParams as LinearLayout.LayoutParams).gravity =
                    alignment

                is FrameLayout -> (targetView.layoutParams as FrameLayout.LayoutParams).gravity =
                    alignment

                else -> {
                    // Attempt to set gravity for other view types if possible
                    try {
                        if (targetView is TextView) {
                            targetView.gravity = alignment
                        }
                    } catch (_: Exception) {
                        // Log error if gravity is not applicable for view
                    }
                }
            }
        }

        marginAndPaddingAttributes()

        registerAttribute<View, String>(Attributes.Common.VISIBILITY) { targetView, attributeValue ->
            targetView.visibility = parseVisibility(attributeValue)
        }

        registerAttribute<View, String>(Attributes.Common.CLICKABLE) { targetView, attributeValue ->
            targetView.isClickable = attributeValue.asBoolean() == true
        }

        registerAttribute<View, String>(Attributes.Common.LONG_CLICKABLE) { targetView, attributeValue ->
            targetView.isLongClickable = attributeValue.asBoolean() == true
        }

        registerAttribute<View, String>(Attributes.Common.TAG) { targetView, attributeValue ->
            if (targetView.tag == null) targetView.tag = attributeValue
        }

        registerAttribute<View, String>(Attributes.Common.ENABLED) { targetView, attributeValue ->
            targetView.isEnabled = attributeValue.asBoolean() == true
        }
    }

    /**
     * Processes width and height attributes for a View.
     *
     * This function handles the parsing and application of width and height attributes
     * to a target View. It supports both standard `width`/`height` attributes as well as
     * layout-specific `layout_width`/`layout_height` attributes.
     *
     * It uses an `AttributeRegistry` to define how each attribute should be handled.
     * Specifically, it utilizes the `registerAttribute` function to register a lambda
     * that will be called when the corresponding attribute is encountered during
     * attribute processing.
     *
     * The lambda associated with each attribute is responsible for:
     * 1. Receiving the target `View` and the attribute value (as a `String`).
     * 2. Calling the `setSize` function to apply the width or height to the `View`.
     * 3. Indicating whether it's setting the width (`isWidth = true`) or height
     *    (`isWidth = false`).
     *
     * The `setSize` function (which is not defined in this code snippet) is
     * assumed to be responsible for the actual logic of interpreting and
     * applying the attribute value to the View. It likely handles cases like:
     * - "match_parent" (or "fill_parent")
     * - "wrap_content"
     * - Dimension values (e.g., "100dp", "50px")
     *
     * Example usage (conceptual):
     * ```
     * val processor = AttributeRegistry()
     * processor.widthAndHeightAttributes()
     * // ... later, when processing attributes for a View ...
     * processor.processAttributes(view, mapOf("width" to "match_parent", "height" to "100dp"))
     * ```
     *
     * @receiver AttributeRegistry The `AttributeRegistry` instance to which the
     *                            width/height attribute handlers will be added.
     * @see AttributeRegistry
     * @see registerAttribute
     * @see setSize
     */
    private fun AttributeRegistry.widthAndHeightAttributes() {
        // Handles width, height dynamically
        listOf(Attributes.Common.WIDTH, Attributes.Common.LAYOUT_WIDTH).forEach { attribute ->
            registerAttribute<View, String>(attribute) { targetView, attributeValue ->
                setSize(targetView, attributeValue, isWidth = true)
            }
        }
        listOf(Attributes.Common.HEIGHT, Attributes.Common.LAYOUT_HEIGHT).forEach { attribute ->
            registerAttribute<View, String>(attribute) { targetView, attributeValue ->
                setSize(targetView, attributeValue, isWidth = false)
            }
        }
    }

    /**
     * Processes and applies attributes specific to LinearLayout views.
     *
     * This function adds attribute processors for the following:
     *
     * 1.  **`android:orientation` (LinearLayout.LINEARLAYOUT_ORIENTATION):**
     *     -   Sets the orientation of the LinearLayout (horizontal or vertical).
     *     -   Accepts string values: "horizontal" (case-insensitive) or "vertical" (default).
     *     -   If the value is "horizontal", the orientation is set to `LinearLayout.HORIZONTAL`.
     *     -   Otherwise, it defaults to `LinearLayout.VERTICAL`.
     *
     * 2.  **`layout_weight` (Attributes.Common.WEIGHT):**
     *     -   Sets the weight of a child view within the LinearLayout.
     *     -   Accepts a string representing a floating-point number.
     *     -   Parses the string to a float using [parseFloat].
     *     -   Applies the parsed weight to the child view's `LinearLayout.LayoutParams`.
     *     -   Only applied if the view's `layoutParams` are of type `LinearLayout.LayoutParams`.
     *
     * @receiver AttributeRegistry The AttributeRegistry instance that this function extends.
     */
    private fun AttributeRegistry.linearLayoutAttributes() {
        registerAttribute<LinearLayout, String>(Attributes.LinearLayout.LINEARLAYOUT_ORIENTATION) { targetView, attributeValue ->
            targetView.orientation = if (attributeValue.equals(
                    "horizontal", ignoreCase = true
                )
            ) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        }

        registerAttribute<LinearLayout, String>(Attributes.Common.WEIGHT) { targetView, attributeValue ->
            (targetView.layoutParams as? LinearLayout.LayoutParams)?.apply {
                weight = parseFloat(attributeValue)
            }
        }
    }

    /**
     * Processes and applies attributes specific to RelativeLayout.
     *
     * This function iterates through a predefined set of RelativeLayout rules and adds attribute handlers
     * for each rule. These handlers are responsible for extracting the view ID from the attribute value
     * and then applying the corresponding RelativeLayout rule to the target view.
     *
     * The supported attributes and their corresponding RelativeLayout rules are:
     * - `Attributes.View.VIEW_ABOVE`: `RelativeLayout.ABOVE`
     * - `Attributes.View.VIEW_BELOW`: `RelativeLayout.BELOW`
     * - `Attributes.View.VIEW_TO_LEFT_OF`: `RelativeLayout.LEFT_OF`
     * - `Attributes.View.VIEW_TO_RIGHT_OF`: `RelativeLayout.RIGHT_OF`
     * - `Attributes.View.VIEW_ALIGN_TOP`: `RelativeLayout.ALIGN_TOP`
     * - `Attributes.View.VIEW_ALIGN_BOTTOM`: `RelativeLayout.ALIGN_BOTTOM`
     * - `Attributes.View.VIEW_ALIGN_PARENT_TOP`: `RelativeLayout.ALIGN_PARENT_TOP`
     * - `Attributes.View.VIEW_ALIGN_PARENT_BOTTOM`: `RelativeLayout.ALIGN_PARENT_BOTTOM`
     * - `Attributes.View.VIEW_ALIGN_START`: `RelativeLayout.ALIGN_START`
     * - `Attributes.View.VIEW_ALIGN_END`: `RelativeLayout.ALIGN_END`
     * - `Attributes.View.VIEW_ALIGN_PARENT_START`: `RelativeLayout.ALIGN_PARENT_START`
     * - `Attributes.View.VIEW_ALIGN_PARENT_END`: `RelativeLayout.ALIGN_PARENT_END`
     *
     * For each attribute, a handler is added that takes the target view and the attribute value (which should be a view ID as a String).
     * It then calls `addRelativeLayoutRule` to add the layout rule to the target view's layout parameters.
     *
     * @see AttributeRegistry
     * @see Attributes.View
     * @see RelativeLayout
     * @see addRelativeLayoutRule
     */
    private fun AttributeRegistry.relativeLayoutAttributes() {
        val relativeLayoutRules = mapOf(
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
        relativeLayoutRules.forEach { (attr, rule) ->
            registerAttribute<View, String>(attr) { targetView, attributeValue ->
                addRelativeLayoutRule(targetView, attributeValue, rule)
            }
        }
    }

    /**
     * Configures and adds attribute processors for ConstraintLayout specific attributes.
     *
     * This function handles the parsing and application of various ConstraintLayout attributes,
     * including:
     *  - Constraint rules (e.g., `layout_constraintLeft_toLeftOf`, `layout_constraintTop_toBottomOf`).
     *  - Chain styles (e.g., `layout_constraintHorizontal_chainStyle`, `layout_constraintVertical_chainStyle`).
     *  - Dimension ratio (`layout_constraintDimensionRatio`).
     *  - Bias (e.g., `layout_constraintVertical_bias`, `layout_constraintHorizontal_bias`).
     *
     * It iterates through a predefined map of constraint rules, associating each XML attribute
     * with the corresponding ConstraintSet side (LEFT, RIGHT, TOP, BOTTOM, START, END, BASELINE).
     * For each attribute, it registers an attribute processor that parses the attribute value,
     * identifies the target view's parent as a ConstraintLayout, and applies the constraint
     * using the `addConstraintRule` helper function.
     *
     * It also registers attribute processors for chain styles, dimension ratios, and biases,
     * using corresponding helper functions (`setChainStyle`, `setDimensionRatio`, and `setConstraintLayoutBias`).
     *
     * All the attributes handled by this function are expected to be applied to views that are
     * direct children of a ConstraintLayout.
     *
     * @receiver AttributeRegistry The `AttributeRegistry` instance to which the attribute handlers are added.
     * @see addConstraintRule
     * @see setChainStyle
     * @see setDimensionRatio
     * @see setConstraintLayoutBias
     * @see ConstraintSet
     * @see ConstraintLayout
     * @see Attributes.ConstraintLayout
     */
    private fun AttributeRegistry.constraintLayoutAttributes() {
        // ConstraintLayout attributes
        val constraintLayoutRules = mapOf(
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

        constraintLayoutRules.forEach { (attr, side) ->
            registerAttribute<View, String>(attr) { targetView, attributeValue ->
                Log.d(
                    "AttributeRegistry",
                    "Processing attribute: $attr, value: $attributeValue, side: $side"
                )
                addConstraintRule(
                    targetView.getParentView() as? ConstraintLayout,
                    targetView,
                    attributeValue,
                    side
                )
            }
        }

        // Chain Style
        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_HORIZONTAL_STYLE) { targetView, attributeValue ->
            setChainStyle(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                ConstraintSet.HORIZONTAL,
                attributeValue
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_CHAIN_VERTICAL_STYLE) { targetView, attributeValue ->
            setChainStyle(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                ConstraintSet.VERTICAL,
                attributeValue
            )
        }

        // Dimension Ratio
        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_DIMENSION_RATIO) { targetView, attributeValue ->
            setDimensionRatio(
                targetView.getParentView() as? ConstraintLayout, targetView, attributeValue
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_VERTICAL_BIAS) { targetView, attributeValue ->
            setConstraintLayoutBias(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                true,
                parseFloat(attributeValue)
            )
        }

        registerAttribute<View, String>(Attributes.ConstraintLayout.CONSTRAINTLAYOUT_LAYOUT_CONSTRAINT_HORIZONTAL_BIAS) { targetView, biasValue ->
            setConstraintLayoutBias(
                targetView.getParentView() as? ConstraintLayout,
                targetView,
                false,
                parseFloat(biasValue)
            )
        }
    }

    private fun AttributeRegistry.imageViewAttributes() {
        registerAttribute<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SCALE_TYPE) { targetView, attributeValue ->
            parseScaleType(attributeValue)?.let {
                targetView.scaleType = it
            }
        }

        registerAttribute<ImageView, String>(Attributes.ImageView.IMAGEVIEW_SRC) { targetView, attributeValue ->
            setImageSource(targetView, attributeValue)
        }
    }

    private fun AttributeRegistry.textViewAttributes() {
        registerAttribute<TextView, String>(Attributes.Common.TEXT) { targetView, attributeValue ->
            targetView.text = attributeValue
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_TEXT_SIZE) { targetView, attributeValue ->
            targetView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                attributeValue.toPixels(targetView.resources.displayMetrics) as Float
            )
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_TEXT_COLOR) { targetView, attributeValue ->
            targetView.setTextColor(getColor(attributeValue, targetView.context))
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_TEXT_STYLE) { targetView, attributeValue ->
            targetView.setTypeface(null, parseTextStyle(attributeValue.lowercase()))
        }

        registerAttribute<TextView, String>(Attributes.View.VIEW_TEXT_ALIGNMENT) { targetView, attributeValue ->
            targetView.textAlignment = parseTextAlignment(attributeValue)!!
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_ELLIPSIZE) { targetView, attributeValue ->
            targetView.ellipsize = parseEllipsize(attributeValue.lowercase())
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_SINGLE_LINE) { targetView, attributeValue ->
            targetView.isSingleLine = attributeValue.asBoolean() == true
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_HINT) { targetView, attributeValue ->
            targetView.hint = attributeValue
        }

        registerAttribute<TextView, String>(Attributes.TextView.TEXTVIEW_INPUT_TYPE) { targetView, attributeValue ->
            parseInputType(attributeValue).let {
                if (0 or it > 0) targetView.inputType = 0 or it
            }
        }
    }

    private fun AttributeRegistry.viewAttributes() {
        registerAttribute<View, String>(Attributes.View.VIEW_ON_CLICK) { targetView, attributeValue ->
            targetView.setOnClickListener(
                getClickListener(
                    targetView.getParentView(), attributeValue
                )
            )
        }
    }

    private fun AttributeRegistry.marginAndPaddingAttributes() {
        // Handle margins and paddings
        listOf(
            // Margins (need parent for calculation)
            Attributes.Common.LAYOUT_MARGIN to ::setMargin,
            Attributes.Common.LAYOUT_MARGIN_LEFT to ::setMarginLeft,
            Attributes.Common.LAYOUT_MARGIN_RIGHT to ::setMarginRight,
            Attributes.Common.LAYOUT_MARGIN_START to ::setMarginLeft,
            Attributes.Common.LAYOUT_MARGIN_END to ::setMarginRight,
            Attributes.Common.LAYOUT_MARGIN_TOP to ::setMarginTop,
            Attributes.Common.LAYOUT_MARGIN_BOTTOM to ::setMarginBottom,

            // Paddings (only use displayMetrics)
            Attributes.Common.PADDING to ::setPadding,
            Attributes.Common.PADDING_LEFT to ::setPaddingLeft,
            Attributes.Common.PADDING_RIGHT to ::setPaddingRight,
            Attributes.Common.PADDING_START to ::setPaddingLeft,
            Attributes.Common.PADDING_END to ::setPaddingRight,
            Attributes.Common.PADDING_TOP to ::setPaddingTop,
            Attributes.Common.PADDING_BOTTOM to ::setPaddingBottom
        ).forEach { (attr, func) ->
            registerAttribute<View, String>(attr) { targetView, attributeValue ->
                val isMargin = attr.contains("margin")
                val pixels = if (isMargin) {
                    val isHorizontal = attr in listOf(
                        Attributes.Common.LAYOUT_MARGIN_LEFT,
                        Attributes.Common.LAYOUT_MARGIN_START,
                        Attributes.Common.LAYOUT_MARGIN_RIGHT,
                        Attributes.Common.LAYOUT_MARGIN_END
                    )

                    attributeValue.toPixels(
                        targetView.resources.displayMetrics,
                        targetView.getParentView(), // Use parent for margins
                        isHorizontal,
                        true
                    )
                } else {
                    attributeValue.toPixels(
                        targetView.resources.displayMetrics, asInt = true
                    ) // Paddings don't need parent
                }

                func(targetView, pixels as Int)
            }
        }
    }

    private fun attributeRegistryContainsId() =
        AttributeProcessors.any { it.first == Attributes.Common.ID }

    private fun setImageSource(targetView: ImageView, attributeValue: String) {
        var imageReference = attributeValue
        if (imageReference.startsWith("//")) imageReference = "http:$imageReference"
        if (imageReference.startsWith("http:")) {
            Glide.with(targetView.context).load(imageReference).into(targetView)
        } else if (imageReference.startsWith("@drawable/")) {
            val resDrawable = getDrawable(targetView, attributeValue.removePrefix("@drawable/"))
            if (resDrawable != null) {
                targetView.setImageDrawable(resDrawable)
            } else {
                // Handle case where resource is not found (optional)
                targetView.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }
    }

    /** Sets width or height dynamically */
    private fun setSize(targetView: View, attributeValue: String, isWidth: Boolean) {
        val params = (targetView.layoutParams ?: ViewGroup.LayoutParams(wrapContent, wrapContent))
        val size = attributeValue.toLayoutParam(
            targetView.resources.displayMetrics, targetView.getParentView(), isWidth
        )
        if (isWidth) params.width = size else params.height = size
        targetView.layoutParams = params
    }

    /** Converts layout string values to proper size */
    private fun String.toLayoutParam(
        displayMetrics: DisplayMetrics, parentView: ViewGroup?, isHorizontal: Boolean
    ) = when (this) {
        "fill_parent", "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
        "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
        else -> this.toPixels(displayMetrics, parentView, isHorizontal, true) as Int
    }

    /** Helper functions for margins & paddings */
    private fun setMargin(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            attributeValue, attributeValue, attributeValue, attributeValue
        )

    private fun setMarginLeft(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            attributeValue, 0, 0, 0
        )

    private fun setMarginRight(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, 0, attributeValue, 0
        )

    private fun setMarginTop(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, attributeValue, 0, 0
        )

    private fun setMarginBottom(targetView: View, attributeValue: Int) =
        (targetView.layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(
            0, 0, 0, attributeValue
        )

    private fun setPadding(targetView: View, attributeValue: Int) =
        targetView.setPadding(attributeValue, attributeValue, attributeValue, attributeValue)

    private fun setPaddingLeft(targetView: View, attributeValue: Int) =
        targetView.setPadding(attributeValue, 0, 0, 0)

    private fun setPaddingRight(targetView: View, attributeValue: Int) =
        targetView.setPadding(0, 0, attributeValue, 0)

    private fun setPaddingTop(targetView: View, attributeValue: Int) =
        targetView.setPadding(0, attributeValue, 0, 0)

    private fun setPaddingBottom(targetView: View, attributeValue: Int) =
        targetView.setPadding(0, 0, 0, attributeValue)

    /** Adds relative layout relativeLayoutRule dynamically */
    private fun addRelativeLayoutRule(
        targetView: View, attributeValue: String, relativeLayoutRule: Int
    ) {
        (targetView.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            val anchor = when {
                attributeValue.isBoolean() -> parseRelativeLayoutBoolean(attributeValue.asBoolean()!!)
                else -> targetView.getParentView()?.getViewID(attributeValue.extractViewId())!!
            }
            ParseHelper.addRelativeLayoutRule(targetView, relativeLayoutRule, anchor)
        }
    }

    private fun addConstraintRule(
        constraint: ConstraintLayout?,
        targetView: View,
        attributeValue: String,
        constraintSides: Pair<Int, Int>
    ) {
        // Apply the constraint rule using ConstraintSet
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraint)

        val viewId = targetView.id // The ID of the targetView to apply the constraint to

        // ID of the targetView to connect to (either another targetView or the parent)
        val targetId = if (attributeValue == "parent") {
            ConstraintSet.PARENT_ID
        } else {
            constraint?.getViewID(attributeValue.extractViewId()) ?: ConstraintSet.PARENT_ID
        }
        Log.d("ViewAttributes", "targetView: $viewId, targetId: $targetId")

        constraintSet.connect(viewId, constraintSides.first, targetId, constraintSides.second)

        // Apply the constraints
        constraintSet.applyTo(constraint)
    }

    private fun setChainStyle(
        constraint: ConstraintLayout?, targetView: View, orientation: Int, attributeValue: String
    ) {
        val chainStyle = when (attributeValue.lowercase()) {
            "spread" -> ConstraintSet.CHAIN_SPREAD
            "spread_inside" -> ConstraintSet.CHAIN_SPREAD_INSIDE
            "packed" -> ConstraintSet.CHAIN_PACKED
            else -> ConstraintSet.CHAIN_SPREAD // Default attributeValue
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraint)

        if (orientation == ConstraintSet.HORIZONTAL) {
            constraintSet.setHorizontalChainStyle(targetView.id, chainStyle)
        } else if (orientation == ConstraintSet.VERTICAL) {
            constraintSet.setVerticalChainStyle(targetView.id, chainStyle)
        }

        constraintSet.applyTo(constraint)
    }

    private fun setDimensionRatio(
        constraint: ConstraintLayout?, targetView: View, attributeValue: String
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraint)

        constraintSet.setDimensionRatio(targetView.id, attributeValue)

        constraintSet.applyTo(constraint)
    }

    private fun setConstraintLayoutBias(
        constraintLayout: ConstraintLayout?,
        targetView: View,
        isVertical: Boolean,
        attributeValue: Float
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        if (isVertical) {
            constraintSet.setVerticalBias(targetView.id, attributeValue)
        } else {
            constraintSet.setHorizontalBias(targetView.id, attributeValue)
        }
        constraintSet.applyTo(constraintLayout)
    }
}