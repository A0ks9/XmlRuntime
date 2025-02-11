package com.flipkart.android.proteus

import android.content.res.XmlResourceParser
import android.view.View
import android.view.ViewGroup
import com.flipkart.android.proteus.managers.ViewManager
import com.flipkart.android.proteus.parser.ViewParser
import com.flipkart.android.proteus.processor.AttributeProcessor
import com.flipkart.android.proteus.value.Layout
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Value
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * Abstract class responsible for parsing and creating views of a specific type within the Proteus framework.
 *
 * `ViewTypeParser` handles the creation of Android Views from a layout description and processes attributes
 * to configure the view based on provided data. It supports a hierarchical structure through parent parsers
 * and manages attribute processing using `AttributeProcessor` instances.
 *
 * @param V The type of Android View this parser is responsible for creating, must extend `View`.
 */
abstract class ViewTypeParser<V : View> {

    /**
     * Static field to hold a `XmlResourceParser` instance for layout parameter hack.
     * This is used to generate default `LayoutParams` when no layout parameters are explicitly defined.
     * Initialized lazily in `generateDefaultLayoutParams`.
     */
    companion object {
        private var sParser: XmlResourceParser? = null
    }

    /**
     * Optional parent `ViewTypeParser`.
     * Used to inherit attribute processors and for hierarchical view creation and attribute handling.
     * Can be null if this parser is for a root view type or has no parent type.
     */
    var parent: ViewTypeParser<V>? = null // Using Kotlin's nullable type for Nullable

    /**
     * Array of `AttributeProcessor` instances.
     * Each processor is responsible for handling a specific XML attribute for the view type.
     * Initialized as an empty array and populated in `addAttributeProcessors` and `prepare`.
     */
    private var processors: Array<AttributeProcessor<V>> =
        emptyArray() // Using Kotlin's emptyArray for initialization

    /**
     * Map to store attributes and their corresponding `Attribute` information.
     * Keys are attribute names (Strings), and values are `AttributeSet.Attribute` instances.
     * Used for efficient lookup of attributes and their processors.
     */
    private var attributes: MutableMap<String, AttributeSet.Attribute> =
        HashMap() // Using Kotlin's MutableMap

    /**
     * Offset value used for attribute ID calculation.
     * Helps in managing attribute IDs in a hierarchical parser structure.
     * Initialized to 0 or inherited from the parent parser in `prepare`.
     */
    private var offset: Int = 0

    /**
     * `AttributeSet` instance associated with this parser.
     * Holds information about all supported attributes, their processors, and hierarchical structure.
     * Created and prepared in the `prepare` method.
     */
    private lateinit var attributeSet: AttributeSet // Using lateinit for non-null initialization in prepare

    /**
     * Abstract method to get the type name of the view this parser handles.
     * This type name is typically used to identify the parser in layout configurations.
     *
     * @return The type name of the view as a `String`. Must be non-null.
     */
    abstract fun getType(): String

    /**
     * Abstract method to get the parent type name of the view.
     * Indicates inheritance or a parent-child relationship between view types in the framework.
     *
     * @return The parent type name as a `String`, or null if there's no parent type.
     */
    abstract fun getParentType(): String? // Using Kotlin's nullable type for 

    /**
     * Abstract method to create an instance of the `ProteusView` managed by this parser.
     * This method is responsible for instantiating the actual Android View and wrapping it in a `ProteusView`.
     *
     * @param context   The `ProteusContext` providing resources and environment for view creation.
     * @param layout    The `Layout` object defining the structure and attributes of the view.
     * @param data      The `ObjectValue` containing data to be bound to the view.
     * @param parent    The parent `ViewGroup` under which the view will be added, can be null for root views.
     * @param dataIndex The index of the data item if the view is part of a data-bound list.
     * @return          A new `ProteusView` instance. Must be non-null.
     */
    abstract fun createView(
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable type for 
        dataIndex: Int
    ): ProteusView

    /**
     * Creates a `ProteusView.Manager` for the given `ProteusView`.
     * This manager is responsible for handling attribute updates and data binding for the view.
     * It recursively calls the parent parser's `createViewManager` if a parent parser exists and is not the caller
     * to ensure proper hierarchical manager creation.
     *
     * @param context   The `ProteusContext`.
     * @param view      The `ProteusView` for which to create a manager.
     * @param layout    The `Layout` object.
     * @param data      The `ObjectValue` data.
     * @param caller    The `ViewTypeParser` that initiated the `createViewManager` call, used to prevent infinite recursion.
     * @param parent    The parent `ViewGroup`.
     * @param dataIndex The data index.
     * @return          A `ProteusView.Manager` instance. Must be non-null.
     */
    open fun createViewManager(
        context: ProteusContext,
        view: ProteusView,
        layout: Layout,
        data: ObjectValue,
        caller: ViewTypeParser<View>?, // Using Kotlin's nullable type for
        parent: ViewGroup?, // Using Kotlin's nullable type for
        dataIndex: Int
    ): ProteusView.Manager {
        // If there is a parent parser and it's not the caller, delegate to the parent parser
        return if (this.parent != null && caller != this.parent) {
            this.parent!!.createViewManager(context, view, layout, data, caller, parent, dataIndex)
        } else {
            // Otherwise, create a DataContext and a ViewManager for this parser
            val dataContext = createDataContext(context, layout, data, parent, dataIndex)
            @Suppress("UNCHECKED_CAST") ViewManager(
                context, caller ?: (this as ViewParser<View>), view.asView, layout, dataContext
            ) // Use caller if available, otherwise use 'this' as parser
        }
    }

    /**
     * Creates a `DataContext` for the view.
     * `DataContext` manages the data scope for the view and its children, handling data inheritance and updates.
     * It checks for parent `DataContext` if the view is nested within another `ProteusView` and creates a child or copy accordingly.
     *
     * @param context   The `ProteusContext`.
     * @param layout    The `Layout` object.
     * @param data      The `ObjectValue` data.
     * @param parent    The parent `ViewGroup`.
     * @param dataIndex The data index.
     * @return          A `DataContext` instance. Must be non-null.
     */

    protected open fun createDataContext( // Marked as open in Kotlin to allow overriding
        context: ProteusContext,
        layout: Layout,
        data: ObjectValue,
        parent: ViewGroup?, // Using Kotlin's nullable type for
        dataIndex: Int
    ): DataContext {
        val dataContext: DataContext
        var parentDataContext: DataContext? = null
        val layoutDataMap: Map<String, Value>? = layout.data // Get data map from layout

        // Check if parent is a ProteusView to inherit DataContext
        if (parent is ProteusView) {
            parentDataContext = parent.viewManager.dataContext
        }

        // Determine DataContext creation based on layout data and parent DataContext
        dataContext = if (layoutDataMap == null) {
            // If no layout data, either copy parent DataContext or create a new one
            parentDataContext?.copy() ?: DataContext.create(context, data, dataIndex)
        } else {
            // If layout data exists, create a child DataContext or a new one with layout data
            parentDataContext?.createChild(context, layoutDataMap, dataIndex) ?: DataContext.create(
                context, data, dataIndex, layoutDataMap
            )
        }
        return dataContext
    }

    /**
     * Called after the `ProteusView` is created and before it's added to the view hierarchy.
     * This method can be used to perform post-creation setup, like setting default `LayoutParams`
     * if they are not already set.
     *
     * @param view      The created `ProteusView`. Must be non-null.
     * @param parent    The parent `ViewGroup`, can be null for root views.
     * @param dataIndex The data index.
     */
    open fun onAfterCreateView(
        view: ProteusView, parent: ViewGroup?, dataIndex: Int
    ) { // Marked as open in Kotlin
        val v: View = view.asView
        // Set default LayoutParams if not already set
        if (v.layoutParams == null) {
            val layoutParams: ViewGroup.LayoutParams = when (parent) {
                null -> ViewGroup.LayoutParams( // Default to WRAP_CONTENT if no parent
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )

                else -> generateDefaultLayoutParams(parent) // Generate default LayoutParams based on parent
            }
            v.layoutParams = layoutParams
        }
    }

    /**
     * Abstract method to be implemented by subclasses to add `AttributeProcessor` instances.
     * Subclasses should override this method to register processors for the attributes they want to handle.
     * This method is called during the `prepare` phase to set up attribute processing.
     */
    protected abstract fun addAttributeProcessors()

    /**
     * Handles a specific attribute for the given view.
     * Looks up the `AttributeProcessor` for the attribute ID and calls its `process` method to apply the attribute value.
     * If no processor is found for the attribute ID in this parser, it delegates the handling to the parent parser if one exists.
     *
     * @param view        The Android View to which the attribute should be applied.
     * @param attributeId The ID of the attribute to handle.
     * @param value       The `Value` representing the attribute's value.
     * @return            `true` if the attribute was handled by this parser or a parent, `false` otherwise.
     */
    fun handleAttribute(view: V, attributeId: Int, value: Value): Boolean {
        val position = getPosition(attributeId)
        return if (position < 0) {
            // If position is negative, attribute not found in this set, delegate to parent
            parent?.handleAttribute(
                view, attributeId, value
            ) == true // Call parent's handler if available
        } else {
            // Attribute found, get the processor and process the attribute
            val attributeProcessor: AttributeProcessor<V> =
                processors[position] // Safe cast due to type parameter V
            attributeProcessor.process(view, value)
            true // Attribute processed successfully
        }
    }

    /**
     * Handles child views for the given view.
     * In many cases, basic views don't directly handle children, so this method delegates to the parent parser if one exists.
     * Subclasses that manage child views (like layouts) should override this method to handle child view processing.
     *
     * @param view     The Android View that might contain child views.
     * @param children The `Value` representing the child views (typically an Array of Layouts).
     * @return         `true` if child handling was delegated to a parent parser, `false` otherwise.
     */
    open fun handleChildren(view: V, children: Value): Boolean { // Marked as open in Kotlin
        return parent?.handleChildren(
            view, children
        ) == true // Delegate child handling to parent if available
    }

    /**
     * Adds a child `ProteusView` to a parent `ProteusView`.
     * This method, in the base `ViewTypeParser`, delegates the actual adding of the view to the parent parser.
     * Layout parsers or container view parsers should override this method to handle the addition of child views correctly.
     *
     * @param parent The parent `ProteusView` to which the child should be added.
     * @param view   The child `ProteusView` to be added.
     * @return       `true` if view adding was delegated to a parent parser, `false` otherwise.
     */
    open fun addView(parent: ProteusView, view: ProteusView): Boolean { // Marked as open in Kotlin
        return this.parent?.addView(parent, view) == true // Delegate addView to parent if available
    }

    /**
     * Prepares the `ViewTypeParser` for attribute processing.
     * This method is called before view creation to set up the attribute set, processors, and parent-child relationships.
     * It initializes attribute processors by calling `addAttributeProcessors` and `addAttributeProcessors(extras)`.
     * It also sets up the `AttributeSet` for efficient attribute lookup.
     *
     * @param parent The parent `ViewTypeParser`, can be null if this is a root parser.
     * @param extras Optional map of extra attribute processors to add, can be null.
     * @return       The prepared `AttributeSet` for this parser. Must be non-null.
     */

    fun prepare(
        parent: ViewTypeParser<V>?, // Using Kotlin's nullable type for
        extras: Map<String, AttributeProcessor<V>>? // Using Kotlin's nullable type for
    ): AttributeSet {
        this.parent = parent
        this.processors = emptyArray() // Reset processors array
        this.attributes = HashMap() // Reset attributes map
        this.offset =
            parent?.attributeSet?.getOffset() ?: 0 // Inherit offset from parent or default to 0

        addAttributeProcessors() // Add default attribute processors for this view type

        if (extras != null) {
            addAttributeProcessors(extras) // Add extra attribute processors if provided
        }

        this.attributeSet = AttributeSet(
            if (attributes.isNotEmpty()) attributes else null, // Use attributes map if not empty, otherwise null
            parent?.attributeSet, // Set parent AttributeSet
            processors.size // Set processors length for AttributeSet
        )
        return attributeSet
    }

    /**
     * Gets the attribute ID for a given attribute name.
     * Uses the `AttributeSet` to lookup the attribute and return its ID.
     *
     * @param name The name of the attribute.
     * @return     The attribute ID if found, or -1 if not found.
     */
    fun getAttributeId(name: String): Int {
        val attribute: AttributeSet.Attribute? = attributeSet.getAttribute(name)
        return attribute?.id ?: -1 // Return attribute ID or -1 if null
    }

    /**
     * Gets the `AttributeSet` associated with this parser.
     *
     * @return The `AttributeSet` instance. Must be non-null after `prepare` is called.
     */

    fun getAttributeSet(): AttributeSet {
        return this.attributeSet
    }

    /**
     * Adds a map of `AttributeProcessor` instances.
     * Iterates through the map and calls `addAttributeProcessor(name, processor)` for each entry.
     *
     * @param processors Map of attribute names to `AttributeProcessor` instances. Must be non-null.
     */
    protected fun addAttributeProcessors(processors: Map<String, AttributeProcessor<V>>) {
        for (entry in processors.entries) {
            addAttributeProcessor(entry.key, entry.value) // Add each processor from the map
        }
    }

    /**
     * Adds a single `AttributeProcessor` for a given attribute name.
     * Calls `addAttributeProcessor(processor)` to add the processor to the processors array.
     * Puts the attribute name and its `Attribute` info into the `attributes` map.
     *
     * @param name      The name of the attribute.
     * @param processor The `AttributeProcessor` instance to handle the attribute. Must be non-null.
     */
    protected fun addAttributeProcessor(name: String, processor: AttributeProcessor<V>) {
        addAttributeProcessor(processor) // Add processor to the processors array
        attributes[name] = AttributeSet.Attribute(
            getAttributeId(processors.size - 1), // Generate attribute ID based on current processors count
            processor // Set the processor for the attribute
        )
    }

    /**
     * Adds an `AttributeProcessor` to the processors array.
     * Expands the processors array and adds the processor to the last position.
     *
     * @param handler The `AttributeProcessor` to add. Must be non-null.
     */
    private fun addAttributeProcessor(handler: AttributeProcessor<V>) {
        processors += handler // Use Kotlin's += operator to add to array
    }

    /**
     * Gets the offset value for attribute ID calculation.
     *
     * @return The offset value as an `Int`.
     */
    private fun getOffset(): Int {
        return offset
    }

    /**
     * Gets the position of an attribute in the processors array based on its attribute ID.
     * Takes into account the offset for hierarchical attribute ID management.
     *
     * @param attributeId The attribute ID.
     * @return            The position of the attribute in the processors array, or -1 if invalid.
     */
    private fun getPosition(attributeId: Int): Int {
        return attributeId + offset // Calculate position using offset
    }

    /**
     * Gets the attribute ID from a given position in the processors array.
     * Reverses the offset calculation to get the original attribute ID.
     *
     * @param position The position in the processors array.
     * @return         The attribute ID.
     */
    private fun getAttributeId(position: Int): Int {
        return position - offset // Calculate attribute ID from position using offset
    }

    /**
     * Generates default `LayoutParams` for a view based on its parent `ViewGroup`.
     * This method is a workaround to generate `LayoutParams` programmatically as there is no direct way to do it.
     * It uses a hack involving parsing a dummy layout XML to obtain an `XmlResourceParser` that can be used with `parent.generateLayoutParams`.
     *
     * @param parent The parent `ViewGroup` for which to generate `LayoutParams`. Must be non-null.
     * @return       `ViewGroup.LayoutParams` instance generated for the parent.
     */
    private fun generateDefaultLayoutParams(parent: ViewGroup): ViewGroup.LayoutParams {

        /**
         * This whole method is a hack! To generate layout params, since no other way exists.
         * Refer : http://stackoverflow.com/questions/7018267/generating-a-layoutparams-based-on-the-type-of-parent
         */
        if (sParser == null) {
            synchronized(ViewTypeParser::class.java) { // Synchronize to ensure thread-safe initialization of sParser
                if (sParser == null) {
                    initializeAttributeSet(parent) // Initialize sParser if it's null
                }
            }
        }

        return parent.generateLayoutParams(sParser) // Generate LayoutParams using the dummy parser
    }

    /**
     * Initializes the static `sParser` field with an `XmlResourceParser`.
     * Parses a dummy layout XML (`R.layout.layout_params_hack`) to get a parser that can be used to generate `LayoutParams`.
     * This is part of the `LayoutParams` generation hack.
     *
     * @param parent The parent `ViewGroup` whose resources are used to get the layout XML parser. Must be non-null.
     */
    private fun initializeAttributeSet(parent: ViewGroup) {
        sParser =
            parent.resources.getLayout(R.layout.layout_params_hack) // Get layout parser from resources
        try {
            // Skip everything until the first START_TAG in the dummy layout XML
            while (sParser!!.nextToken() != XmlResourceParser.START_TAG) {
                //no-op
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Inner class `AttributeSet` to manage a set of attributes and their processors.
     * Represents a collection of attributes supported by a `ViewTypeParser`, including hierarchical relationships.
     */
    class AttributeSet {

        /**
         * Map of attribute names to `Attribute` instances for this level of the attribute set.
         * Can be null if there are no attributes defined at this level.
         */

        private val attributes: Map<String, Attribute>? // Using Kotlin's nullable type for 

        /**
         * Parent `AttributeSet` in a hierarchical structure.
         * Used for attribute inheritance and lookup. Can be null if this is the root set.
         */

        private val parent: AttributeSet? // Using Kotlin's nullable type for 

        /**
         * Offset value for attribute IDs in this `AttributeSet`.
         * Used for hierarchical attribute ID management.
         */
        private val offset: Int

        /**
         * Constructor for `AttributeSet`.
         *
         * @param attributes Map of attributes for this set, can be null.
         * @param parent     Parent `AttributeSet`, can be null.
         * @param offset     Offset value for attribute IDs.
         */
        internal constructor(
            attributes: Map<String, Attribute>?, // Using Kotlin's nullable type for
            parent: AttributeSet?, // Using Kotlin's nullable type for
            offset: Int
        ) {
            this.attributes = attributes
            this.parent = parent
            val parentOffset = parent?.offset ?: 0 // Get parent offset or default to 0
            this.offset =
                parentOffset - offset // Calculate offset based on parent offset and current offset
        }

        /**
         * Gets the `Attribute` information for a given attribute name.
         * Looks up the attribute in the current `AttributeSet` and then recursively in the parent `AttributeSet` if not found.
         *
         * @param name The name of the attribute to get.
         * @return     The `Attribute` instance if found, or null if not found in this set or any parent set.
         */

        fun getAttribute(name: String): Attribute? {
            // Lookup in current attributes map
            var attribute: Attribute? = attributes?.get(name)
            if (attribute != null) {
                return attribute // Return if found in current set
            } else if (parent != null) {
                return parent.getAttribute(name) // Recursively lookup in parent set if not found
            } else {
                return null // Attribute not found in this set or any parent
            }
        }

        /**
         * Gets the offset value for this `AttributeSet`.
         *
         * @return The offset value as an `Int`.
         */
        internal fun getOffset(): Int {
            return offset
        }

        /**
         * Inner class `Attribute` to hold information about a single attribute.
         * Contains the attribute ID and the `AttributeProcessor` responsible for handling it.
         */
        class Attribute {

            /**
             * Unique ID of the attribute.
             * Used for efficient attribute lookup and processing.
             */
            val id: Int

            /**
             * `AttributeProcessor` instance responsible for processing this attribute.
             * Contains the logic to apply the attribute value to the view.
             */

            val processor: AttributeProcessor<*> // Using wildcard generic due to type erasure

            /**
             * Constructor for `Attribute`.
             *
             * @param id        The attribute ID.
             * @param processor The `AttributeProcessor` for this attribute. Must be non-null.
             */
            internal constructor(id: Int, processor: AttributeProcessor<*>) {
                this.processor = processor
                this.id = id
            }
        }
    }
}