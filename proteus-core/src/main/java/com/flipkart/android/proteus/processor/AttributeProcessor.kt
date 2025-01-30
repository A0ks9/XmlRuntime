package com.flipkart.android.proteus.processor

import android.content.Context
import android.view.View
import com.flipkart.android.proteus.value.AttributeResource
import com.flipkart.android.proteus.value.Binding
import com.flipkart.android.proteus.value.NestedBinding
import com.flipkart.android.proteus.value.Null
import com.flipkart.android.proteus.value.ObjectValue
import com.flipkart.android.proteus.value.Primitive
import com.flipkart.android.proteus.value.Resource
import com.flipkart.android.proteus.value.StyleResource
import com.flipkart.android.proteus.value.Value

/**
 * Kotlin abstract class for processing attributes and resolving their values.
 *
 * This class provides a framework for handling different types of attribute values
 * (Binding, Resource, AttributeResource, StyleResource, Value) and evaluating them
 * in a Proteus context. It is designed to be extended by specific attribute processors
 * for different View attributes.
 *
 * @param V The type of View this AttributeProcessor works with, must be a subclass of View.
 */
abstract class AttributeProcessor<V : View> {

    companion object { // Companion object to hold static methods, similar to Java's static methods

        /**
         * Evaluates a given [input] Value in a Proteus context and returns the resolved Value.
         *
         * This method uses an anonymous AttributeProcessor to handle different Value types
         * and resolve them to a final Value.
         *
         * @param context The Android Context.
         * @param input   The input Value to evaluate (can be Binding, Resource, etc.).
         * @param data    The data context as a Value.
         * @param index   The index within the data context (if applicable).
         * @return The resolved Value after processing.
         */
        @JvmStatic // To keep it callable as static method from Java if needed
        fun evaluate(
            context: Context, input: Value, data: Value, index: Int
        ): Value? { // Return type made nullable for safety
            var output: Value? = null // Initialize output as nullable

            // Use object expression to create an anonymous AttributeProcessor, simplifying the Java anonymous class
            val processor = object :
                AttributeProcessor<View>() { // Anonymous object implementing AttributeProcessor<View>
                override fun handleBinding(view: View?, binding: Binding) {
                    output =
                        binding.evaluate(context, data, index) // Evaluate binding and set output
                }

                override fun handleValue(view: View?, value: Value) {
                    output = value // Directly set output to the value
                }

                override fun handleResource(view: View?, resource: Resource) {
                    output =
                        Primitive(resource.getString(context)) // Resolve resource to Primitive<String> and set output
                }

                override fun handleAttributeResource(view: View?, attribute: AttributeResource) {
                    output = Primitive(
                        attribute.apply(context).getString(0)
                    ) // Resolve AttributeResource to Primitive<String> and set output
                }

                override fun handleStyleResource(view: View?, style: StyleResource) {
                    output = Primitive(
                        style.apply(context).getString(0)
                    ) // Resolve StyleResource to Primitive<String> and set output
                }
            }

            processor.process(
                null, input
            ) // Process the input Value, view is null as it's a static evaluation

            return output // Return the resolved output Value
        }

        /**
         * Performs static pre-compilation on a Primitive Value to resolve bindings, resources, etc.
         *
         * This method checks if the Primitive Value represents a Binding, Resource, AttributeResource, or StyleResource
         * and returns the corresponding compiled Value if found. Otherwise, returns null.
         *
         * @param value   The Primitive Value to pre-compile.
         * @param context The Android Context.
         * @param manager The FunctionManager.
         * @return The pre-compiled Value, or null if no pre-compilation is needed.
         */
        @JvmStatic // To keep it callable as static method from Java if needed
        fun staticPreCompile(
            value: Primitive, context: Context, manager: FunctionManager
        ): Value? { // Return type is nullable
            val string = value.getAsString() // Get string value from Primitive

            return when { // Using 'when' for cleaner conditional checks
                Binding.isBindingValue(string) -> Binding.valueOf(
                    string, context, manager
                ) // Create Binding if it's a binding value
                Resource.isResource(string) -> Resource.valueOf(
                    string, null, context
                ) // Create Resource if it's a resource
                AttributeResource.isAttributeResource(string) -> AttributeResource.valueOf(
                    string, context
                ) // Create AttributeResource
                StyleResource.isStyleResource(string) -> StyleResource.valueOf(
                    string, context
                ) // Create StyleResource
                else -> null // Return null if no pre-compilation needed
            }
        }

        /**
         * Performs static pre-compilation on an ObjectValue to resolve nested bindings.
         *
         * This method checks if the ObjectValue contains a nested binding key and returns the corresponding NestedBinding Value if found.
         * Otherwise, returns null.
         *
         * @param objectValue  The ObjectValue to pre-compile.
         * @param context The Android Context (not directly used in this method, but kept for consistency).
         * @param manager The FunctionManager (not directly used in this method, but kept for consistency).
         * @return The pre-compiled Value (NestedBinding), or null if no nested binding is found.
         */
        @JvmStatic // To keep it callable as static method from Java if needed
        fun staticPreCompile(
            objectValue: ObjectValue, context: Context, manager: FunctionManager
        ): Value? { // Return type is nullable
            return objectValue[NestedBinding.NESTED_BINDING_KEY]?.let { // Using let for concise null check and scope
                NestedBinding.valueOf(it) // Create NestedBinding if key exists
            } // if get() returns null, let block is not executed, and null is returned implicitly
        }

        /**
         * Performs static pre-compilation on a Value to resolve different Value types.
         *
         * This method handles Primitive and ObjectValue types, delegating to specific pre-compile methods.
         * For Binding, Resource, AttributeResource, and StyleResource types, it returns the value itself as they are already considered compiled.
         * For other Value types, it returns null.
         *
         * @param value   The Value to pre-compile.
         * @param context The Android Context.
         * @param manager The FunctionManager.
         * @return The pre-compiled Value, or null if no pre-compilation is needed or for unhandled types.
         */
        @JvmStatic // To keep it callable as static method from Java if needed
        fun staticPreCompile(
            value: Value, context: Context, manager: FunctionManager
        ): Value? { // Return type is nullable
            return when { // Using 'when' for type checking and branching
                value.isPrimitive -> staticPreCompile(
                    value.asPrimitive(), context, manager
                ) // Pre-compile Primitive
                value.isObject -> staticPreCompile(
                    value.asObject(), context, manager
                ) // Pre-compile ObjectValue
                value.isBinding || value.isResource || value.isAttributeResource || value.isStyleResource -> value // Return value as is for already compiled types
                else -> null // Return null for other Value types
            }
        }
    }

    /**
     * Processes a given [value] and dispatches it to the appropriate handler method based on its type.
     *
     * This method checks the type of the [value] (Binding, Resource, etc.) and calls the corresponding
     * `handle...` method to process it.
     *
     * @param view  The View to be processed.
     * @param value The Value to process.
     */
    open fun process(
        view: V?, value: Value
    ) { // 'open' for potential overriding in subclasses, view made nullable
        when { // Using 'when' for cleaner type checking and branching
            value.isBinding -> handleBinding(view, value.asBinding()) // Handle Binding Value
            value.isResource -> handleResource(view, value.asResource()) // Handle Resource Value
            value.isAttributeResource -> handleAttributeResource(
                view, value.asAttributeResource()
            ) // Handle AttributeResource Value
            value.isStyleResource -> handleStyleResource(
                view, value.asStyleResource()
            ) // Handle StyleResource Value
            else -> handleValue(view, value) // Default: Handle as a regular Value
        }
    }

    /**
     * Handles a Binding Value.
     *
     * This method resolves the Binding against the DataContext and calls [handleValue] with the resolved Value.
     *
     * @param view  The View being processed.
     * @param value The Binding Value to handle.
     */
    open fun handleBinding(
        view: V?, value: Binding
    ) { // 'open' for potential overriding, view made nullable
        // Get DataContext from ProteusView's ViewManager
        val dataContext =
            (view as? ProteusView)?.viewManager?.dataContext // Safe cast and null check
        val resolved = dataContext?.let { // Using let for null safety
            evaluate(
                value, view?.context ?: contextForEvaluate, it.data, it.index
            ) // Evaluate binding if dataContext and context are available
        } ?: Null // If dataContext is null, resolve to Null Value

        handleValue(
            view, resolved ?: Null
        ) // Handle the resolved value, defaulting to Null if evaluation failed
    }

    /**
     * Abstract method to handle a regular Value. Must be implemented by subclasses.
     *
     * @param view  The View being processed.
     * @param value The Value to handle.
     */
    abstract fun handleValue(view: V?, value: Value) // Abstract method, view made nullable

    /**
     * Abstract method to handle a Resource Value. Must be implemented by subclasses.
     *
     * @param view     The View being processed.
     * @param resource The Resource Value to handle.
     */
    abstract fun handleResource(view: V?, resource: Resource) // Abstract method, view made nullable

    /**
     * Abstract method to handle an AttributeResource Value. Must be implemented by subclasses.
     *
     * @param view      The View being processed.
     * @param attribute The AttributeResource Value to handle.
     */
    abstract fun handleAttributeResource(
        view: V?, attribute: AttributeResource
    ) // Abstract method, view made nullable

    /**
     * Abstract method to handle a StyleResource Value. Must be implemented by subclasses.
     *
     * @param view  The View being processed.
     * @param style The StyleResource Value to handle.
     */
    abstract fun handleStyleResource(
        view: V?, style: StyleResource
    ) // Abstract method, view made nullable

    /**
     * Pre-compiles a Value. If static pre-compilation is possible, it returns the statically pre-compiled Value.
     * Otherwise, it returns the result of [compile].
     *
     * @param value   The Value to pre-compile.
     * @param context The Android Context.
     * @param manager The FunctionManager.
     * @return The pre-compiled Value.
     */
    open fun precompile(
        value: Value, context: Context, manager: FunctionManager
    ): Value? { // 'open' for potential overriding
        return staticPreCompile(value, context, manager) ?: compile(
            value, context
        ) // Use elvis operator for concise logic
    }

    /**
     * Compiles a Value. Default implementation returns the Value itself. Subclasses can override for custom compilation logic.
     *
     * @param value   The Value to compile.
     * @param context The Android Context.
     * @return The compiled Value (default implementation returns the input Value).
     */
    open fun compile(
        value: Value?, context: Context
    ): Value? { // 'open' for potential overriding, value made nullable
        return value // Default compile implementation: return the input value
    }

    /**
     * Evaluates a Binding Value.
     *
     * This protected method is used internally to evaluate a Binding against the given context, data, and index.
     *
     * @param binding The Binding to evaluate.
     * @param context The Android Context.
     * @param data    The data context as a Value.
     * @param index   The index within the data context.
     * @return The evaluated Value.
     */
    protected open fun evaluate(
        binding: Binding, context: Context, data: Value, index: Int
    ): Value? { // 'open' for potential overriding, return type nullable
        return binding.evaluate(context, data, index) // Delegate evaluation to Binding.evaluate
    }

    /**
     * Provides a Context for evaluation when a View is not available (e.g., in static evaluation).
     *
     * Subclasses can override this to provide a default Context if needed for static evaluations.
     * The default implementation returns a fallback context or null if no suitable fallback is available.
     */
    protected open val contextForEvaluate: Context?
        get() = null // Default implementation: return null, subclasses can override to provide a fallback Context
}