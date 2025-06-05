package com.voyager.core.attribute

import android.view.View
import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Registry for attribute handlers in the Voyager framework.
 * This object maintains a mapping between attribute names and their corresponding handlers,
 * allowing for type-safe attribute processing. It provides thread-safe registration
 * of attribute handlers and ensures that each attribute is registered only once.
 */
@PublishedApi
internal object AttributeRegistry {
    val logger = LoggerFactory.getLogger(AttributeRegistry::class.java.simpleName)
    
    // Access config lazily to avoid initialization issues
    val config by lazy { ConfigManager.config }

    /** Map of handler IDs to their corresponding handlers */
    val handlers = mutableMapOf<Int, AttributeHandler>()
    
    /** Map of attribute names to their handler IDs */
    val ids = mutableMapOf<String, Int>()
    
    /** Atomic counter for generating unique handler IDs */
    val nextId = AtomicInteger(0)

    /**
     * Registers a new attribute handler for a specific view and value type.
     * This method is type-safe and ensures that handlers are registered only once.
     * It uses synchronization to handle concurrent registration attempts safely.
     *
     * @param name The attribute name to register
     * @param handler The handler function that processes the attribute
     */
    inline fun <reified V : View, reified T> register(
        name: String,
        crossinline handler: (V, T) -> Unit,
    ) {
        var id = ids[name]
        if (id != null) {
            if (config.isLoggingEnabled) {
                logger.warn("register", "Attribute '$name' is already registered with ID $id")
            }
            return
        }

        synchronized(ids) {
            id = ids[name]
            if (id != null) {
                if (config.isLoggingEnabled) {
                    logger.warn("register", "Attribute '$name' was registered concurrently with ID $id")
                }
                return
            }

            val newId = nextId.getAndIncrement()
            ids[name] = newId
            handlers[newId] = AttributeHandler(V::class.java, T::class.java) { view, value ->
                handler(view as V, value as T)
                id = newId
            }

            if (config.isLoggingEnabled) {
                logger.debug("register", "Registered attribute '$name' with ID $newId for ${V::class.java.simpleName}")
            }
        }
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
        if (handler == null && config.isLoggingEnabled) {
            logger.error("getHandler", "No handler found for ID $id")
        }
    }
}