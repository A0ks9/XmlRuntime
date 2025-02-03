package com.flipkart.android.proteus

/**
 * Kotlin implementation of ProteusContextWrapper, a simple wrapper around ProteusContext.
 *
 * This class extends ProteusContext and is designed to be used when you need to pass a ProteusContext
 * but want to avoid direct modifications to the original context instance. It essentially creates a thin
 * wrapper that delegates to the underlying ProteusContext.
 *
 * @param context The ProteusContext instance to wrap.
 */
class ProteusContextWrapper(context: ProteusContext) :
    ProteusContext( // Kotlin class declaration extending ProteusContext
        context, // Pass the base Context from the input context
        context.getProteusResources(), // Pass ProteusResources from the input context
        context.getLoader(), // Pass ImageLoader from the input context
        context.getCallback() // Pass Callback from the input context
    ) {
    // The constructor of ProteusContextWrapper simply calls the constructor of its superclass (ProteusContext)
    // passing along the necessary components from the provided 'context'.
    // No additional properties or methods are defined in this wrapper class as it's designed to be a direct passthrough.
}