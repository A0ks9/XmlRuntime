package com.flipkart.android.proteus.processor

import android.view.View
import com.flipkart.android.proteus.ProteusLayoutInflater
import com.flipkart.android.proteus.ProteusView
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * Abstract class for processing events on Proteus Views.
 *
 * `EventProcessor` is an abstract `AttributeProcessor` specialized for handling event attributes (like `onClick`, `onLongClick`, etc.)
 * in Proteus layouts. It provides default implementations for handling different types of attribute values
 * (Value, Resource, AttributeResource, StyleResource) and delegates the actual event listener setup to the
 * abstract `setOnEventListener()` method, which must be implemented by concrete subclasses.
 * It also provides a `trigger()` method to invoke the `ProteusLayoutInflater.Callback`'s `onEvent()` method
 * when an event is triggered on a view.
 *
 * @param T The type of Android View this EventProcessor is designed to handle, must extend `View`.
 */
abstract class EventProcessor<T : View> :
    AttributeProcessor<T>() { // Kotlin abstract class declaration inheriting from AttributeProcessor

    /**
     * Handles a Value type attribute by calling `setOnEventListener` to set up the event listener.
     * This method is called when an event attribute is provided directly as a Value in the layout.
     *
     * @param view  The Android View to which the event listener should be attached.
     * @param value The Value representing the event attribute's value (typically a Binding or Function call).
     */
    override fun handleValue(
        view: T?, value: Value
    ) { // Override handleValue to set event listener for Value
        setOnEventListener(view!!, value) // Delegate to setOnEventListener with Value
    }

    /**
     * Handles a Resource type attribute by calling `setOnEventListener` to set up the event listener.
     * This method is called when an event attribute refers to an Android Resource (which is generally not applicable for Events, but included for completeness from the base class).
     *
     * @param view     The Android View.
     * @param resource The Resource object.
     */
    override fun handleResource(
        view: T?, resource: Resource
    ) { // Override handleResource to set event listener for Resource
        setOnEventListener(view!!, resource) // Delegate to setOnEventListener with Resource
    }

    /**
     * Handles an AttributeResource type attribute by calling `setOnEventListener`.
     * This method is called when the event attribute refers to another attribute resource (again, less common for Events, but included for base class compatibility).
     *
     * @param view      The Android View.
     * @param attribute The AttributeResource object.
     */
    override fun handleAttributeResource(
        view: T?, attribute: AttributeResource
    ) { // Override handleAttributeResource to set event listener for AttributeResource
        setOnEventListener(
            view!!, attribute
        ) // Delegate to setOnEventListener with AttributeResource
    }

    /**
     * Handles a StyleResource type attribute by calling `setOnEventListener`.
     * This method is called when the event attribute refers to a style resource (not typically used for Events, more for style attributes, but included for base class consistency).
     *
     * @param view   The Android View.
     * @param style The StyleResource object.
     */
    override fun handleStyleResource(
        view: T?, style: StyleResource
    ) { // Override handleStyleResource to set event listener for StyleResource
        setOnEventListener(view!!, style) // Delegate to setOnEventListener with StyleResource
    }

    /**
     * Abstract method to be implemented by concrete EventProcessor subclasses to set up the event listener on the View.
     * This is where subclasses will define the specific event listener (e.g., `OnClickListener`, `OnLongClickListener`)
     * and how it should behave when triggered, based on the provided attribute Value.
     *
     * @param view  The Android View on which to set the event listener.
     * @param value The Value representing the event attribute's value (e.g., Binding or Function call to execute on event).
     */
    abstract fun setOnEventListener(view: T, value: Value)

    /**
     * Triggers the event callback.
     * This method is called by concrete EventProcessors when a specific event occurs on the View (e.g., a click, long click).
     * It retrieves the `ProteusLayoutInflater.Callback` from the View's `ViewManager` and, if a callback is registered,
     * calls its `onEvent()` method, passing the event name, the attribute Value, and the ProteusView instance.
     *
     * @param event The name of the event that occurred (e.g., "click").
     * @param value The Value associated with the event attribute in the layout.
     * @param view  The ProteusView instance on which the event was triggered. Must be non-null.
     */
    fun trigger(
        event: String, value: Value, view: ProteusView
    ) { // Public method to trigger the event callback
        val callback: ProteusLayoutInflater.Callback? =
            view.viewManager.context.getCallback() // Get the Callback from ViewManager's context
        callback?.onEvent(
            event, value, view
        ) // Safely call onEvent on the callback if it's not null, using Kotlin's safe call operator ?.
    }
}