package com.voyager.core.attribute

import android.view.View
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Registry for attribute handlers in the Voyager framework.
 * This object maintains a mapping between attribute names and their corresponding handlers,
 * allowing for type-safe attribute processing. It provides thread-safe registration
 * of attribute handlers and ensures that each attribute is registered only once.
 */
@PublishedApi
internal object AttributeRegistry {
    internal val logger by lazy { LoggerFactory.getLogger(AttributeRegistry::class.java.simpleName) }

    // Access config lazily to avoid initialization issues
    internal val isLoggingEnabled by lazy { ConfigManager.config.isLoggingEnabled }

    /** Map of handler IDs to their corresponding handlers */
    internal val handlers = ConcurrentHashMap<Int, AttributeHandler>()

    /** Map of attribute names to their handler IDs */
    internal val ids = ConcurrentHashMap<String, Int>()

    /** Atomic counter for generating unique handler IDs */
    internal val nextId = AtomicInteger(0)

    @PublishedApi
    internal fun registerInternal(
        name: String,
        viewClass: Class<out View>,
        valueClass: Class<*>,
        handler: (View, Any) -> Unit,
    ): Boolean {
        if (ids.containsKey(name)) {
            if (isLoggingEnabled) {
                logger.warn("register", "Attribute '$name' is already registered")
            }
            return false
        }

        synchronized(this) {
            if (ids.containsKey(name)) {
                if (isLoggingEnabled) {
                    logger.warn("register", "Attribute '$name' was registered concurrently")
                }
                return false
            }

            val newId = nextId.andIncrement
            ids[name] = newId
            handlers[newId] = AttributeHandler(viewClass, valueClass, handler)

            if (isLoggingEnabled) {
                logger.debug(
                    "register",
                    "Registered attribute '$name' with ID $newId for ${viewClass.simpleName}"
                )
            }
            return true
        }
    }

    /**
     * Registers a new attribute handler for a specific view and value type.
     * This method is type-safe and ensures that handlers are registered only once.
     * It uses synchronization to handle concurrent registration attempts safely.
     *
     * @param name The attribute name to register
     * @param handler The handler function that processes the attribute
     */
    @PublishedApi
    internal inline fun <reified V : View, reified T> register(
        name: String,
        crossinline handler: (V, T) -> Unit,
    ) {
        registerInternal(name, V::class.java, T::class.java) { view, value ->
            handler(view as V, value as T)
        }
    }


    internal fun register(
        name: String,
        viewClass: Class<out View>,
        valueClass: Class<*>,
        handler: (View, Any) -> Unit,
    ) {
        registerInternal(name, viewClass, valueClass, handler)
    }

    /**
     * Retrieves a handler for a given handler ID.
     * This method is used internally by the attribute processor to get the
     * appropriate handler for processing an attribute.
     *
     * @param id The handler ID to look up
     * @return The corresponding AttributeHandler, or null if not found
     */
    internal fun getHandler(id: Int): AttributeHandler? = handlers[id].also { handler ->
        if (handler == null && isLoggingEnabled) {
            logger.error("getHandler", "No handler found for ID $id")
        }
    }
}