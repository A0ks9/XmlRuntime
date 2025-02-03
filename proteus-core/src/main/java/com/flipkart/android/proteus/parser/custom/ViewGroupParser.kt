package com.flipkart.android.proteus.parser.custom

import android.view.View
import android.view.ViewGroup
import com.flipkart.android.proteus.ProteusConstants
import com.flipkart.android.proteus.ViewTypeParser
import com.flipkart.android.proteus.processor.AttributeProcessor
import com.flipkart.android.proteus.processor.BooleanAttributeProcessor
import com.flipkart.android.proteus.toolbox.Attributes
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Binding
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.NestedBinding
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin implementation of ViewGroupParser, responsible for creating and configuring ViewGroup views.
 *
 * This class extends ViewTypeParser and specializes in handling "ViewGroup" view types within the Proteus framework.
 * It defines how ViewGroup views are created, their type, parent type, and handles specific attributes like
 * `clipChildren`, `clipToPadding`, `layoutMode`, `splitMotionEvents`, and `children` (for dynamic child inflation and data binding).
 *
 * @param T The type of ViewGroup view this parser handles, must be a subclass of ViewGroup.
 *           In the context of Proteus, this is likely `ProteusViewGroup` or its subclasses like `ProteusAspectRatioFrameLayout`.
 */
open class ViewGroupParser<T : ViewGroup> :
    ViewTypeParser<T>() { // Kotlin class declaration inheriting from ViewTypeParser, marked 'open' for potential subclassing

    companion object { // Companion object for constants, similar to static final fields in Java
        private const val LAYOUT_MODE_CLIP_BOUNDS =
            "clipBounds" // Constant for clipBounds layout mode
        private const val LAYOUT_MODE_OPTICAL_BOUNDS =
            "opticalBounds" // Constant for opticalBounds layout mode
    }

    /**
     * Returns the type of view this parser is responsible for, which is "ViewGroup".
     * This type string is used to identify this parser in the Proteus framework configuration.
     *
     * @return The string "ViewGroup", representing the view type.
     */
    override fun getType(): String =
        "ViewGroup" // Override getType() function using expression body, returning view type name

    /**
     * Returns the parent type of the ViewGroup view, which is "View".
     * This indicates that ViewGroup inherits properties and behaviors from View in the Proteus framework.
     *
     * @return The string "View", representing the parent view type.
     *         Returns null as there's no explicit parent type beyond "View".
     */
    override fun getParentType(): String? =
        "View" // Override getParentType(), using Kotlin's nullable String? and expression body

    /**
     * Creates a new instance of a ProteusView for ViewGroup. In this base implementation, it creates a `ProteusAspectRatioFrameLayout`.
     * Subclasses might override this to create specific ViewGroup types.
     *
     * @param context    The ProteusContext providing resources and environment for view creation.
     * @param layout     The Layout object defining the structure and attributes of the view.
     * @param data       The ObjectValue containing data to be bound to the view.
     * @param parent     The parent ViewGroup under which the ViewGroup view will be added. May be null if it's a root view.
     * @param dataIndex  The index of the data item if the view is part of a data-bound list.
     * @return           A new ProteusView instance, specifically a `ProteusAspectRatioFrameLayout` in this base implementation.
     */
    override fun createView( // Override createView(), using expression body
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView =
        ProteusAspectRatioFrameLayout(context) // Creates and returns a new ProteusAspectRatioFrameLayout instance

    /**
     * Overrides `createViewManager` to create a `ViewGroupManager` for managing ViewGroup-specific behavior.
     *
     * @param context    The ProteusContext.
     * @param view       The ProteusView instance.
     * @param layout     The Layout object.
     * @param data       The ObjectValue data.
     * @param caller     The ViewTypeParser that initiated the call.
     * @param parent     The parent ViewGroup.
     * @param dataIndex  The data index.
     * @return           A `ViewGroupManager` instance for managing the ViewGroup.
     */
    override fun createViewManager( // Override createViewManager() for ViewGroup-specific manager
        context: ProteusContext,
        view: ProteusView,
        layout: Layout,
        data: ObjectValue,
        caller: ViewTypeParser<*>?, // Using Kotlin's nullable ViewTypeParser?
        parent: ViewGroup?, // Using Kotlin's nullable ViewGroup?
        dataIndex: Int
    ): ProteusView.Manager {
        val dataContext = createDataContext(
            context, layout, data, parent, dataIndex
        ) // Create DataContext for the view
        return ViewGroupManager(
            context, caller ?: this, view.asView(), layout, dataContext
        ) // Create and return ViewGroupManager
    }

    /**
     * Overrides `addAttributeProcessors` to define attribute processors specific to ViewGroup.
     * This method registers processors for handling attributes like `clipChildren`, `clipToPadding`, `layoutMode`, etc.
     */
    override fun addAttributeProcessors() { // Override addAttributeProcessors() to register custom attribute handlers

        // Attribute processor for 'clipChildren' attribute (Boolean) - using lambda
        addAttributeProcessor(
            Attributes.ViewGroup.ClipChildren,
            object : BooleanAttributeProcessor<T>() {
                override fun setBoolean(view: T, value: Boolean) {
                    view.clipChildren = value
                }
            }) // setting clipChildren

        // Attribute processor for 'clipToPadding' attribute (Boolean) - using lambda
        addAttributeProcessor(Attributes.ViewGroup.ClipToPadding,
            object : BooleanAttributeProcessor<T>() {
                override fun setBoolean(view: T, value: Boolean) {
                    view.clipToPadding = value
                }
            }) // setting clipToPadding

        // Attribute processor for 'layoutMode' attribute (String - "clipBounds" or "opticalBounds") - using lambda and API level check
        addAttributeProcessor(Attributes.ViewGroup.LayoutMode,
            StringAttributeProcessor { view, value -> // Lambda for setting layoutMode
                // API level check for setLayoutMode
                view.layoutMode =
                    when (value) { // Use when expression for layout mode string matching
                        LAYOUT_MODE_CLIP_BOUNDS -> ViewGroup.LAYOUT_MODE_CLIP_BOUNDS // Set clipBounds layout mode
                        LAYOUT_MODE_OPTICAL_BOUNDS -> ViewGroup.LAYOUT_MODE_OPTICAL_BOUNDS // Set opticalBounds layout mode
                        else -> view.layoutMode // Default to current layoutMode if value is not recognized
                    }
            })

        // Attribute processor for 'splitMotionEvents' attribute (Boolean) - using lambda
        addAttributeProcessor(Attributes.ViewGroup.SplitMotionEvents,
            object : BooleanAttributeProcessor<T>() {
                override fun setBoolean(view: T, value: Boolean) {
                    view.isMotionEventSplittingEnabled = value
                }
            }) // setting motionEventSplittingEnabled

        // Attribute processor for 'children' attribute (special AttributeProcessor to handle Layout array or data-bound children)
        addAttributeProcessor(Attributes.ViewGroup.Children,
            object :
                AttributeProcessor<T>() { // Anonymous AttributeProcessor to handle 'children' attribute
                override fun handleBinding(
                    view: T?, value: Binding
                ) { // Override handleBinding for data-bound children
                    handleDataBoundChildren(
                        view!!, value
                    ) // Delegate to handleDataBoundChildren for data binding logic
                }

                override fun handleValue(
                    view: T?, value: Value
                ) { // Override handleValue for static children inflation
                    handleChildren(
                        view!!, value
                    ) // Delegate to handleChildren for static inflation logic
                }

                override fun handleResource(
                    view: T?, resource: Resource
                ) { // Override handleResource to throw exception for resource type
                    throw IllegalArgumentException("children cannot be a resource") // Children attribute does not support resources
                }

                override fun handleAttributeResource(
                    view: T?, attribute: AttributeResource
                ) { // Override handleAttributeResource to throw exception
                    throw IllegalArgumentException("children cannot be a resource") // Children attribute does not support attribute resources
                }

                override fun handleStyleResource(
                    view: T?, style: StyleResource
                ) { // Override handleStyleResource to throw exception for style resource
                    throw IllegalArgumentException("children cannot be a style attribute") // Children attribute does not support style resources
                }
            })
    }

    /**
     * Handles inflation of child views for a ViewGroup based on a `Value` representing children layouts.
     * This is for static child views defined directly in the layout (not data-bound).
     *
     * @param view     The ViewGroup to which children are being added.
     * @param children The Value containing an array of Layout objects representing child views.
     * @return         Always returns true to indicate children handling was attempted.
     */
    override fun handleChildren(
        view: T, children: Value
    ): Boolean { // Override handleChildren to inflate static children
        val proteusView =
            view as ProteusView // Cast to ProteusView to access Proteus framework methods
        val viewManager = proteusView.viewManager // Get ViewManager from ProteusView
        val layoutInflater = viewManager.context.inflater // Get ProteusLayoutInflater from context
        val data = viewManager.dataContext.data // Get current data context
        val dataIndex = viewManager.dataContext.index // Get current data index

        if (children.isArray) { // Check if children Value is an array
            val iterator = children.asArray.iterator() // Get iterator for the array of children
            while (iterator.hasNext()) { // Iterate through each child layout
                val element = iterator.next() // Get the next child element
                if (!element.isLayout) { // Check if the element is a Layout Value
                    throw ProteusInflateException("attribute  'children' must be an array of 'Layout' objects") // Throw exception if not a Layout
                }
                val child = layoutInflater.inflate(
                    element.asLayout, data, view, dataIndex
                ) // Inflate child layout
                addView(
                    proteusView, child
                ) // Add the inflated child view to the parent ViewGroup
            }
        }
        return true // Indicate children handling was attempted
    }

    /**
     * Handles data-bound children for a ViewGroup. This method is invoked when the 'children' attribute is data-bound.
     * It dynamically inflates or updates child views based on the provided data collection and layout configuration.
     *
     * @param view  The ViewGroup to which data-bound children are being managed.
     * @param value The Binding object containing configuration for data-bound children (collection and layout).
     */
    protected open fun handleDataBoundChildren(
        view: T, value: Binding
    ) { // Open function for handling data-bound children
        val parent = view as ProteusView // Cast to ProteusView
        val manager = parent.viewManager as ViewGroupManager // Cast ViewManager to ViewGroupManager
        val dataContext = manager.dataContext // Get DataContext
        val config =
            (value as NestedBinding).value.asObject // Get configuration object from Binding

        val collection =
            config.asBinding(ProteusConstants.COLLECTION) // Get collection Binding from config
        val layout = config.asLayout(ProteusConstants.LAYOUT) // Get layout Layout from config

        manager.hasDataBoundChildren = true // Mark that this ViewGroup has data-bound children

        if (layout == null || collection == null) { // Check if layout and collection are both provided
            throw ProteusInflateException("'collection' and 'layout' are mandatory for attribute:'children'") // Throw exception if missing
        }

        val dataset = collection.asBinding().evaluate(
            view.context, dataContext.data, dataContext.index
        ) // Evaluate collection binding
        if (dataset.isNull) { // If dataset is null, return (no children to inflate)
            return
        }

        if (!dataset.isArray) { // Check if dataset is an array
            throw ProteusInflateException("'collection' in attribute:'children' must be NULL or Array") // Throw exception if not an array
        }

        val length = dataset.asArray.size() // Get size of the dataset array
        val count = view.childCount // Get current child count
        val data = dataContext.data // Get current data
        val inflater = manager.context.inflater // Get ProteusLayoutInflater
        var child: ProteusView // Variable to hold inflated ProteusView
        var temp: View // Variable to hold child View

        if (count > length) { // If there are more children than data items, remove extra views
            for (indexToRemove in length until count) {
                view.removeViewAt(length) // Remove views from the end until count matches data length
            }
        }

        for (index in 0 until length) { // Iterate through the dataset
            if (index < count) { // If a child view already exists at this index
                temp = view.getChildAt(index) // Get the existing child view
                if (temp is ProteusView) { // Check if it's a ProteusView
                    temp.viewManager.update(data) // Update the existing ProteusView with current data
                }
            } else { // If no child view exists at this index (need to inflate a new one)
                child = inflater.inflate(
                    layout, data, view, index
                ) // Inflate a new child view using layout and data
                addView(
                    parent, child
                ) // Add the newly inflated child view to the parent ViewGroup
            }
        }
    }

    /**
     * Overrides `addView` to handle adding a `ProteusView` to a `ViewGroup`.
     * This method ensures that the `ProteusView`'s underlying Android View is added to the `ViewGroup`.
     *
     * @param parent The parent `ProteusView` (which must be a ViewGroup).
     * @param view   The child `ProteusView` to add.
     * @return       `true` if the view was successfully added, `false` otherwise.
     */
    override fun addView(
        parent: ProteusView, view: ProteusView
    ): Boolean { // Override addView to handle ProteusView children
        if (parent.getAsView() is ViewGroup) { // Check if parent ProteusView's underlying view is a ViewGroup
            (parent.getAsView() as ViewGroup).addView(view.asView()) // Add the child ProteusView's view to the parent ViewGroup
            return true // Return true indicating successful addition
        }
        return false // Return false if parent is not a ViewGroup
    }
}