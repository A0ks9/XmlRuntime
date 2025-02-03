package com.flipkart.android.proteus.parser

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.ProteusContext
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.processor.AttributeProcessor
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor
import com.flipkart.android.proteus.processor.DrawableResourceProcessor
import com.flipkart.android.proteus.processor.EventProcessor
import com.flipkart.android.proteus.processor.GravityAttributeProcessor
import com.flipkart.android.proteus.processor.GravityAttributeProcessor.ProteusGravity
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin class for parsing and processing attributes of generic Android Views in Proteus layouts.
 *
 * This class extends [ViewTypeParser] and is responsible for handling attributes common to all Android Views,
 * such as layout dimensions, padding, margin, visibility, click listeners, and more.
 *
 * @param V The View type to which this parser applies.
 */
open class ViewParser<V : View> : ViewTypeParser<V>() { // Made class 'open' to allow inheritance

    /**
     * Returns the type name for this parser, which is "View".
     *
     * This type name is used to identify generic View tags in layout definitions.
     *
     * @return The string "View".
     */
    override fun getType(): String = "View"

    /**
     * Returns null as the parent type for generic Views, indicating it's a root element or can be a child of any ViewGroup.
     *
     * @return null.
     */
    override fun getParentType(): String? = null

    /**
     * Creates a [ProteusView] instance, specifically a [ProteusAndroidView] in this case, for generic View elements.
     *
     * @param context   The ProteusContext for view creation.
     * @param layout    The Layout object representing the View definition.
     * @param data      The data [ObjectValue] for data binding.
     * @param parent    The optional parent [ViewGroup].
     * @param dataIndex The index for data binding if it's within a list/array.
     * @return A new [ProteusAndroidView] instance.
     */
    override fun createView(
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?,
        dataIndex: Int
    ): ProteusView = ProteusAndroidView(context) // Create ProteusAndroidView for generic View

    /**
     * Adds attribute processors for various View attributes.
     *
     * This method registers attribute processors for handling attributes like 'activated', 'onClick',
     * 'background', layout dimensions, padding, margins, gravity, visibility, id, content description, etc.
     * Each processor is responsible for setting the corresponding property on the View.
     */
    override fun addAttributeProcessors() {

        addAttributeProcessor(
            Attributes.View.Activated,
            BooleanAttributeProcessor<V> { view, value ->
                view.isActivated = value // Kotlin property syntax for setting activated state
            })

        addAttributeProcessor(Attributes.View.OnClick,
            object : EventProcessor<V>() { // Anonymous object for EventProcessor
                override fun setOnEventListener(view: V, value: Value) {
                    view.setOnClickListener { // Kotlin lambda for OnClickListener
                        trigger(
                            Attributes.View.OnClick, value, view as ProteusView
                        ) // Cast to ProteusView for trigger
                        view.performClick() // Call performClick() for accessibility!
                    }
                }
            })

        addAttributeProcessor(Attributes.View.OnLongClick,
            object : EventProcessor<V>() { // Anonymous object for EventProcessor
                override fun setOnEventListener(view: V, value: Value) {
                    view.setOnLongClickListener { // Kotlin lambda for OnLongClickListener
                        trigger(
                            Attributes.View.OnLongClick, value, view as ProteusView
                        ) // Cast to ProteusView for trigger
                        view.performClick() // Call performClick() for accessibility!
                        return@setOnLongClickListener true // Return true for long click listener
                    }
                }
            })

        @SuppressLint("ClickableViewAccessibility") addAttributeProcessor(Attributes.View.OnTouch,
            object : EventProcessor<V>() { // Anonymous object for EventProcessor
                override fun setOnEventListener(view: V, value: Value) {
                    view.setOnTouchListener { v, event -> // Kotlin lambda for OnTouchListener with parameters
                        trigger(
                            Attributes.View.OnTouch, value, view as ProteusView
                        ) // Cast to ProteusView for trigger
                        view.performClick() // ADD THIS LINE: Call performClick() for accessibility!
                        return@setOnTouchListener true // Return true for touch listener
                    }
                }
            })
        addAttributeProcessor(Attributes.View.Background,
            DrawableResourceProcessor<V> { view, drawable ->
                view.background = drawable // Modern setBackground method
            })

        addAttributeProcessor(Attributes.View.Height,
            DimensionAttributeProcessor<V> { view, dimension ->
                view!!.layoutParams?.let { // Using let to safely access and modify layoutParams if not null
                    it.height = dimension.toInt() // Set height to dimension
                    view.layoutParams = it // Re-apply layoutParams to view
                }
            })

        addAttributeProcessor(
            Attributes.View.Width,
            DimensionAttributeProcessor<V> { view, dimension ->
                view!!.layoutParams?.let { // Using let to safely access and modify layoutParams if not null
                    it.width = dimension.toInt() // Set width to dimension
                    view.layoutParams = it // Re-apply layoutParams to view
                }
            })

        addAttributeProcessor(Attributes.View.Weight,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
                override fun setString(view: V, value: String) {
                    if (view.layoutParams is LinearLayout.LayoutParams) { // Check if layoutParams is LinearLayout.LayoutParams using 'is'
                        val layoutParams =
                            view.layoutParams as LinearLayout.LayoutParams // Smart cast after 'is' check
                        layoutParams.weight = ParseHelper.parseFloat(value) // Parse weight value
                        view.layoutParams = layoutParams // Re-apply layoutParams
                    } else {
                        if (ProteusConstants.isLoggingEnabled()) {
                            Log.e(TAG, "'weight' is only supported for LinearLayouts")
                        }
                    }
                }
            })

        addAttributeProcessor(Attributes.View.LayoutGravity,
            object :
                GravityAttributeProcessor<V>() { // Anonymous object for GravityAttributeProcessor
                override fun setGravity(view: V, @ProteusGravity gravity: Int) {
                    when (view.layoutParams) { // Using when for cleaner type checking
                        is LinearLayout.LayoutParams -> {
                            (view.layoutParams as LinearLayout.LayoutParams).gravity =
                                gravity // Smart cast and set gravity
                            view.layoutParams =
                                view.layoutParams // Re-apply layoutParams - technically not needed as params are modified in place
                        }

                        is FrameLayout.LayoutParams -> {
                            (view.layoutParams as FrameLayout.LayoutParams).gravity =
                                gravity // Smart cast and set gravity
                            view.layoutParams =
                                view.layoutParams // Re-apply layoutParams - technically not needed
                        }

                        else -> if (ProteusConstants.isLoggingEnabled()) {
                            Log.e(
                                TAG,
                                "'layout_gravity' is only supported for LinearLayout and FrameLayout"
                            )
                        }
                    }
                }
            })

        addAttributeProcessor(Attributes.View.Padding,
            DimensionAttributeProcessor<V> { view, dimension ->
                val padding = dimension.toInt()
                view?.setPadding(padding, padding, padding, padding) // Set padding on all sides
            })

        addAttributeProcessor(Attributes.View.PaddingLeft,
            DimensionAttributeProcessor<V> { view, dimension ->
                view?.setPadding(
                    dimension.toInt(), view.paddingTop, view.paddingRight, view.paddingBottom
                ) // Set left padding
            })

        addAttributeProcessor(
            Attributes.View.PaddingTop,
            DimensionAttributeProcessor<V> { view, dimension ->
                view?.setPadding(
                    view.paddingLeft, dimension.toInt(), view.paddingRight, view.paddingBottom
                ) // Set top padding
            })

        addAttributeProcessor(
            Attributes.View.PaddingRight,
            DimensionAttributeProcessor<V> { view, dimension ->
                view?.setPadding(
                    view.paddingLeft, view.paddingTop, dimension.toInt(), view.paddingBottom
                ) // Set right padding
            })

        addAttributeProcessor(Attributes.View.PaddingBottom,
            DimensionAttributeProcessor<V> { view, dimension ->
                view?.setPadding(
                    view.paddingLeft, view.paddingTop, view.paddingRight, dimension.toInt()
                ) // Set bottom padding
            })

        addAttributeProcessor(
            Attributes.View.Margin,
            DimensionAttributeProcessor<V> { view, dimension ->
                if (view?.layoutParams is ViewGroup.MarginLayoutParams) { // Check if layoutParams is ViewGroup.MarginLayoutParams using 'is'
                    val layoutParams =
                        view.layoutParams as ViewGroup.MarginLayoutParams // Smart cast after 'is' check
                    val margin = dimension.toInt()
                    layoutParams.setMargins(
                        margin, margin, margin, margin
                    ) // Set margins on all sides
                    view.layoutParams = layoutParams // Re-apply layoutParams
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup")
                    }
                }
            })

        addAttributeProcessor(Attributes.View.MarginLeft,
            DimensionAttributeProcessor<V> { view, dimension ->
                if (view?.layoutParams is ViewGroup.MarginLayoutParams) { // Check if layoutParams is ViewGroup.MarginLayoutParams using 'is'
                    val layoutParams =
                        view.layoutParams as ViewGroup.MarginLayoutParams // Smart cast after 'is' check
                    layoutParams.leftMargin = dimension.toInt() // Set left margin
                    view.layoutParams = layoutParams // Re-apply layoutParams
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup")
                    }
                }
            })

        addAttributeProcessor(Attributes.View.MarginTop,
            DimensionAttributeProcessor<V> { view, dimension ->
                if (view?.layoutParams is ViewGroup.MarginLayoutParams) { // Check if layoutParams is ViewGroup.MarginLayoutParams using 'is'
                    val layoutParams =
                        view.layoutParams as ViewGroup.MarginLayoutParams // Smart cast after 'is' check
                    layoutParams.topMargin = dimension.toInt() // Set top margin
                    view.layoutParams = layoutParams // Re-apply layoutParams
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup")
                    }
                }
            })

        addAttributeProcessor(
            Attributes.View.MarginRight,
            DimensionAttributeProcessor<V> { view, dimension ->
                if (view?.layoutParams is ViewGroup.MarginLayoutParams) { // Check if layoutParams is ViewGroup.MarginLayoutParams using 'is'
                    val layoutParams =
                        view.layoutParams as ViewGroup.MarginLayoutParams // Smart cast after 'is' check
                    layoutParams.rightMargin = dimension.toInt() // Set right margin
                    view.layoutParams = layoutParams // Re-apply layoutParams
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup")
                    }
                }
            })

        addAttributeProcessor(Attributes.View.MarginBottom,
            DimensionAttributeProcessor<V> { view, dimension ->
                if (view?.layoutParams is ViewGroup.MarginLayoutParams) { // Check if layoutParams is ViewGroup.MarginLayoutParams using 'is'
                    val layoutParams =
                        view.layoutParams as ViewGroup.MarginLayoutParams // Smart cast after 'is' check
                    layoutParams.bottomMargin = dimension.toInt() // Set bottom margin
                    view.layoutParams = layoutParams // Re-apply layoutParams
                } else {
                    if (ProteusConstants.isLoggingEnabled()) {
                        Log.e(TAG, "margins can only be applied to views with parent ViewGroup")
                    }
                }
            })

        addAttributeProcessor(
            Attributes.View.MinHeight,
            DimensionAttributeProcessor<V> { view, dimension ->
                view?.minimumHeight = dimension.toInt() // Kotlin property syntax for minimumHeight
            })

        addAttributeProcessor(Attributes.View.MinWidth,
            DimensionAttributeProcessor<V> { view, dimension ->
                view?.minimumWidth = dimension.toInt() // Kotlin property syntax for minimumWidth
            })

        addAttributeProcessor(
            Attributes.View.Elevation,
            DimensionAttributeProcessor<V> { view, dimension ->
                view?.elevation = dimension // Kotlin property syntax for elevation
            })

        addAttributeProcessor(Attributes.View.Alpha,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
                override fun setString(view: V, value: String) {
                    view.alpha = ParseHelper.parseFloat(value) // Kotlin property syntax for alpha
                }
            })

        addAttributeProcessor(Attributes.View.Visibility,
            object : AttributeProcessor<V>() { // Anonymous object for AttributeProcessor
                override fun handleValue(view: V?, value: Value) {
                    if (value.isPrimitive && value.asPrimitive.isNumber()) { // Check if Value is primitive number
                        @Suppress("ResourceType") // Suppress ResourceType warning - visibility is IntDef
                        view!!.visibility = value.asInt() // Set visibility from int Value
                    } else {
                        process(
                            view, precompile(
                                value,
                                view!!.context,
                                (view.context as ProteusContext).getFunctionManager()
                            )!!
                        ) // Process precompiled value
                    }
                }

                override fun handleResource(view: V?, resource: Resource) {
                    val visibility = resource.getInteger(view!!.context)
                    @Suppress("WrongConstant") // Suppress WrongConstant warning - visibility is IntDef
                    view.visibility =
                        visibility ?: View.GONE // Set visibility from resource, default GONE
                }

                override fun handleAttributeResource(view: V?, attribute: AttributeResource) {
                    val typedArray = attribute.apply(view!!.context)
                    @Suppress("WrongConstant") // Suppress WrongConstant warning - visibility is IntDef
                    view.visibility = typedArray.getInt(
                        0, View.GONE
                    ) // Set visibility from attribute, default GONE
                    typedArray.recycle() // Recycle TypedArray
                }

                override fun handleStyleResource(view: V?, style: StyleResource) {
                    val typedArray = style.apply(view!!.context)
                    @Suppress("WrongConstant") // Suppress WrongConstant warning - visibility is IntDef
                    view.visibility =
                        typedArray.getInt(0, View.GONE) // Set visibility from style, default GONE
                    typedArray.recycle() // Recycle TypedArray
                }

//                override fun compile(value: Value?, context: Context?): Value {
//                    val visibility = ParseHelper.parseVisibility(value)
//                    return ParseHelper.getVisibility(visibility)
//                }
            })

        addAttributeProcessor(Attributes.View.Id,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
                override fun setString(view: V, value: String) {
                    if (view is ProteusView) { // Check if view is ProteusView using 'is'
                        view.id = (view as ProteusView).viewManager.context.getInflater()
                            .getUniqueViewId(value) // Set ID using ProteusContext inflater
                    }

                    // set view id resource name for accessibility
                    val resourceName = value
                    view.accessibilityDelegate =
                        object : View.AccessibilityDelegate() { // Anonymous AccessibilityDelegate
                            override fun onInitializeAccessibilityNodeInfo(
                                host: View, info: AccessibilityNodeInfo
                            ) {
                                super.onInitializeAccessibilityNodeInfo(host, info)
                                val normalizedResourceName: String
                                if (!TextUtils.isEmpty(resourceName)) {
                                    val id = if (resourceName.startsWith(ID_STRING_START_PATTERN)) {
                                        resourceName.substring(ID_STRING_START_PATTERN.length)
                                    } else if (resourceName.startsWith(ID_STRING_START_PATTERN1)) {
                                        resourceName.substring(ID_STRING_START_PATTERN1.length)
                                    } else {
                                        resourceName
                                    }
                                    normalizedResourceName =
                                        view.context.packageName + ID_STRING_NORMALIZED_PATTERN + id
                                } else {
                                    normalizedResourceName = ""
                                }
                                info.setViewIdResourceName(normalizedResourceName)
                            }
                        }
                }
            })

        addAttributeProcessor(Attributes.View.ContentDescription,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
                override fun setString(view: V, value: String) {
                    view.contentDescription = value // Kotlin property syntax for contentDescription
                }
            })

        addAttributeProcessor(Attributes.View.Clickable,
            BooleanAttributeProcessor<V> { view, value ->
                view.isClickable = value // Kotlin property syntax for clickable
            })

        addAttributeProcessor(Attributes.View.Tag,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
                override fun setString(view: V, value: String) {
                    view.tag = value // Kotlin property syntax for tag
                }
            })

        addAttributeProcessor(Attributes.View.Enabled, BooleanAttributeProcessor<V> { view, value ->
            view.isEnabled = value // Kotlin property syntax for enabled
        })

        addAttributeProcessor(Attributes.View.Selected,
            BooleanAttributeProcessor<V> { view, value ->
                view.isSelected = value // Kotlin property syntax for selected
            })

        addAttributeProcessor(Attributes.View.Style,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
                override fun setString(view: V, value: String) {
                    val viewManager =
                        (view as ProteusView).viewManager // Cast to ProteusView and access viewManager
                    val context = viewManager.context
                    val layout = viewManager.layout

                    val handler = context.inflater.getParser(layout.type)

                    val styleSet = value.split(ProteusConstants.STYLE_DELIMITER)
                    for (styleName in styleSet) {
                        val style = context.getStyle(styleName)
                        style?.let { // Using let for null-safe style processing
                            process(
                                it, view as ProteusView, handler ?: this@ViewParser
                            ) // Process style, use 'this@ViewParser' for outer class reference
                        }
                    }
                }

                private fun process(
                    style: Map<String, Value>, proteusView: ProteusView, handler: ViewTypeParser<*>
                ) { //Note: handler is ViewTypeParser<*> to avoid generic type issues
                    for (entry in style) {
                        @Suppress("UNCHECKED_CAST") // Suppress UncheckedCast warning - handler.handleAttribute needs to be correctly typed in implementations
                        (handler as ViewTypeParser<View>).handleAttribute(
                            proteusView.asView, handler.getAttributeId(entry.key), entry.value
                        )
                    }
                }
            })

        addAttributeProcessor(Attributes.View.TransitionName,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
                override fun setString(view: V, value: String) {
                    view.transitionName = value // Kotlin property syntax for transitionName
                }
            })

        addAttributeProcessor(Attributes.View.RequiresFadingEdge,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor

                private val NONE = "none"
                private val BOTH = "both"
                private val VERTICAL = "vertical"
                private val HORIZONTAL = "horizontal"

                override fun setString(view: V, value: String) {

                    when (value) {
                        NONE -> {
                            view.isVerticalFadingEdgeEnabled = false // Kotlin property syntax
                            view.isHorizontalFadingEdgeEnabled = false // Kotlin property syntax
                        }

                        BOTH -> {
                            view.isVerticalFadingEdgeEnabled = true // Kotlin property syntax
                            view.isHorizontalFadingEdgeEnabled = true // Kotlin property syntax
                        }

                        VERTICAL -> {
                            view.isVerticalFadingEdgeEnabled = true // Kotlin property syntax
                            view.isHorizontalFadingEdgeEnabled = false // Kotlin property syntax
                        }

                        HORIZONTAL -> {
                            view.isVerticalFadingEdgeEnabled = false // Kotlin property syntax
                            view.isHorizontalFadingEdgeEnabled = true // Kotlin property syntax
                        }

                        else -> {
                            view.isVerticalFadingEdgeEnabled =
                                false // Default to false if value is not recognized
                            view.isHorizontalFadingEdgeEnabled =
                                false // Default to false if value is not recognized
                        }
                    }
                }
            })

        addAttributeProcessor(Attributes.View.FadingEdgeLength,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
                override fun setString(view: V, value: String) {
                    view.fadingEdgeLength =
                        ParseHelper.parseInt(value) // Kotlin property syntax for fadingEdgeLength
                }
            })

        addAttributeProcessor(Attributes.View.Animation,
            object :
                TweenAnimationResourceProcessor<V>() { // Anonymous object for TweenAnimationResourceProcessor

                override fun setAnimation(view: V, animation: Animation) {
                    view.animation = animation // Kotlin property syntax for animation
                }
            })

        addAttributeProcessor(Attributes.View.TextAlignment,
            object :
                StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor

                override fun setString(view: V, value: String) {

                    val textAlignment = ParseHelper.parseTextAlignment(value)
                    textAlignment?.let { // Using let for null-safe textAlignment setting
                        @Suppress("ResourceType") // Suppress ResourceType warning - textAlignment is IntDef
                        view.textAlignment = it // Kotlin property syntax for textAlignment
                    }
                }
            })

        addAttributeProcessor(
            Attributes.View.Above, createRelativeLayoutRuleProcessor(RelativeLayout.ABOVE)
        )
        addAttributeProcessor(
            Attributes.View.AlignBaseline,
            createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_BASELINE)
        )
        addAttributeProcessor(
            Attributes.View.AlignBottom,
            createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_BOTTOM)
        )
        addAttributeProcessor(
            Attributes.View.AlignLeft, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_LEFT)
        )
        addAttributeProcessor(
            Attributes.View.AlignRight,
            createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_RIGHT)
        )
        addAttributeProcessor(
            Attributes.View.AlignTop, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_TOP)
        )
        addAttributeProcessor(
            Attributes.View.Below, createRelativeLayoutRuleProcessor(RelativeLayout.BELOW)
        )
        addAttributeProcessor(
            Attributes.View.ToLeftOf, createRelativeLayoutRuleProcessor(RelativeLayout.LEFT_OF)
        )
        addAttributeProcessor(
            Attributes.View.ToRightOf, createRelativeLayoutRuleProcessor(RelativeLayout.RIGHT_OF)
        )
        addAttributeProcessor(
            Attributes.View.AlignEnd, createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_END)
        )
        addAttributeProcessor(
            Attributes.View.AlignStart,
            createRelativeLayoutRuleProcessor(RelativeLayout.ALIGN_START)
        )
        addAttributeProcessor(
            Attributes.View.ToEndOf, createRelativeLayoutRuleProcessor(RelativeLayout.END_OF)
        )
        addAttributeProcessor(
            Attributes.View.ToStartOf, createRelativeLayoutRuleProcessor(RelativeLayout.START_OF)
        )

        addAttributeProcessor(
            Attributes.View.AlignParentTop,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_TOP)
        )
        addAttributeProcessor(
            Attributes.View.AlignParentRight,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_RIGHT)
        )
        addAttributeProcessor(
            Attributes.View.AlignParentBottom,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_BOTTOM)
        )
        addAttributeProcessor(
            Attributes.View.AlignParentLeft,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_LEFT)
        )
        addAttributeProcessor(
            Attributes.View.CenterHorizontal,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_HORIZONTAL)
        )
        addAttributeProcessor(
            Attributes.View.CenterVertical,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_VERTICAL)
        )
        addAttributeProcessor(
            Attributes.View.CenterInParent,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.CENTER_IN_PARENT)
        )
        addAttributeProcessor(
            Attributes.View.AlignParentStart,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_START)
        )
        addAttributeProcessor(
            Attributes.View.AlignParentEnd,
            createRelativeLayoutBooleanRuleProcessor(RelativeLayout.ALIGN_PARENT_END)
        )
    }

    override fun handleChildren(view: V, children: Value): Boolean {
        return false
    }

    override fun addView(parent: ProteusView, view: ProteusView): Boolean {
        return false
    }

    private fun createRelativeLayoutRuleProcessor(rule: Int): AttributeProcessor<V> {
        return object :
            StringAttributeProcessor<V>() { // Anonymous object for StringAttributeProcessor
            override fun setString(view: V, value: String) {
                if (view is ProteusView) { // Check if view is ProteusView using 'is'
                    val id =
                        (view as ProteusView).viewManager.context.inflater.getUniqueViewId(value)
                    ParseHelper.addRelativeLayoutRule(view, rule, id)
                }
            }
        }
    }

    private fun createRelativeLayoutBooleanRuleProcessor(rule: Int): BooleanAttributeProcessor<V> {
        return BooleanAttributeProcessor<V> { view, value ->
            val trueOrFalse = ParseHelper.parseRelativeLayoutBoolean(value)
            ParseHelper.addRelativeLayoutRule(view, rule, trueOrFalse)
        }
    }

    companion object {
        private const val TAG = "ViewParser"

        private const val ID_STRING_START_PATTERN = "@+id/"
        private const val ID_STRING_START_PATTERN1 = "@id/"
        private const val ID_STRING_NORMALIZED_PATTERN = ":id/"
    }
}