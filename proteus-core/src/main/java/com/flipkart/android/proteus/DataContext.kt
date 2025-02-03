package com.flipkart.android.proteus

import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin class representing the data context for Proteus layouts.
 *
 * The `DataContext` holds the data, scope, and index necessary for data binding
 * in Proteus layouts. It manages data updates, child context creation, and data scoping.
 */
class DataContext private constructor( // Made primary constructor private to enforce factory methods
    /**
     * The local isolated scope created for this [ProteusView]
     */
    private val scope: Map<String, Value>?, // Nullable scope map

    /**
     * Index for resolving `$index` meta values in arrays and data-bound children.
     */
     val index: Int,

    /**
     * Indicates if this context has its own properties (scope and data not just cloned).
     */
    private val hasOwnProperties: Boolean // Indicates if scope and data are newly created or cloned
) {

    /**
     * Data used for data binding attribute values in the layout.
     */
    var data: ObjectValue = ObjectValue() // Mutable data property, initialized to empty ObjectValue
        private set // Restrict setter to within this class

    /**
     * Copy constructor for creating a clone of another DataContext.
     * [hasOwnProperties] is always `false` for a cloned context.
     *
     * @param dataContext Parent data context to clone from.
     */
    constructor(dataContext: DataContext) : this(
        dataContext.scope, dataContext.index, false
    ) { // Secondary constructor for cloning
        this.data = dataContext.data // Copy data from parent context
    }

    /**
     * Utility method to create a new DataContext without a scope.
     *
     * @param context ProteusContext for function binding resolution.
     * @param data Data for the data context. Nullable.
     * @param dataIndex Data index for the data context.
     * @return New DataContext with evaluated scope.
     */
    companion object { // Companion object for factory methods (static-like methods in Kotlin)
        @JvmStatic // For Java static access
        fun create(
            context: ProteusContext, data: ObjectValue?, dataIndex: Int
        ): DataContext {
            val dataContext = DataContext(
                null, dataIndex, false
            ) // Create DataContext with no scope, hasOwnProperties=false
            dataContext.update(context, data) // Update with provided data
            return dataContext
        }

        /**
         * Utility method to create a new DataContext with a scope.
         *
         * @param context ProteusContext for function binding resolution.
         * @param data Data for the data context. Nullable.
         * @param dataIndex Data index for the data context.
         * @param scope Scope map for the data context. Nullable.
         * @return New DataContext with evaluated scope.
         */
        @JvmStatic // For Java static access
        fun create(
            context: ProteusContext, data: ObjectValue?, dataIndex: Int, scope: Map<String, Value>?
        ): DataContext {
            val dataContext = DataContext(
                scope, dataIndex, scope != null
            ) // Create DataContext with provided scope
            dataContext.update(context, data) // Update with provided data
            return dataContext
        }
    }

    /**
     * Updates this DataContext with new data, evaluating scope bindings.
     *
     * @param context ProteusContext for function binding evaluation.
     * @param input New data. Nullable.
     */
    fun update(
        context: ProteusContext, input: ObjectValue?
    ) { // Renamed parameter 'in' to 'input' for clarity
        var inData = input
            ?: ObjectValue() // Use elvis operator to default to empty ObjectValue if input is null

        if (scope == null) {
            data = inData // If no scope, data is simply the input data
            return
        }

        val output = ObjectValue() // Create new ObjectValue for output (scoped data)

        for ((key, value) in scope) { // Iterate over scope entries using Kotlin's forEach destructuring
            val resolved: Value? = if (value.isBinding) { // Check if scope value is a binding
                val binding = value.asBinding() // Get binding
                binding.evaluate(
                    context, output, index
                ) // Evaluate binding, fallback to input data if needed using elvis and ?:
            } else {
                value // If not a binding, use the value directly
            }
            output[key] = resolved // Add resolved value to output with scope key
        }

        data = output // Set the data of DataContext to the scoped output
    }

    /**
     * Creates a child DataContext with its own scope and index, inheriting data.
     *
     * @param context ProteusContext for function binding resolution.
     * @param scope Scope for the new DataContext.
     * @param dataIndex Data index for the new DataContext.
     * @return New child DataContext with evaluated scope.
     */
    fun createChild(
        context: ProteusContext, scope: Map<String, Value>, dataIndex: Int
    ): DataContext {
        return create(context, data, dataIndex, scope) // Use factory method to create child context
    }

    /**
     * Creates a copy (clone) of this DataContext.
     *
     * @return New cloned DataContext.
     */
    fun copy(): DataContext {
        return DataContext(this) // Use copy constructor to create a clone
    }

    /**
     * Returns whether this DataContext has its own properties (not cloned).
     */
    fun hasOwnProperties(): Boolean {
        return hasOwnProperties
    }

    /**
     * Returns the data index of this DataContext.
     */
    fun getIndex(): Int {
        return index
    }

    /**
     * Returns the scope map of this DataContext. Nullable.
     */
    fun getScope(): Map<String, Value>? {
        return scope
    }

    /**
     * Returns the data [ObjectValue] of this DataContext.
     */
    fun getData(): ObjectValue {
        return data
    }

    /** Not needed as data is now mutable var with private setter
     * Sets the data [ObjectValue] of this DataContext.
     * @param data New data to set.
     *//*fun setData(data: ObjectValue) { // Setter not typically needed in Kotlin if data is mutable property
        this.data = data
    }*/
}