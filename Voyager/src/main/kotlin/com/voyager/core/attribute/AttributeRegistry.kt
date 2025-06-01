package com.voyager.core.attribute

import android.view.View
import java.util.concurrent.atomic.AtomicInteger

/**
 * Registry for attribute handlers. Allows registering and retrieving handlers by attribute name.
 */
object AttributeRegistry {
    val handlers = mutableMapOf<Int, AttributeHandler>()
    val ids = mutableMapOf<String, Int>()
    val nextId = AtomicInteger(0)

    inline fun <reified V : View, reified T> register(
        name: String,
        crossinline handler: (V, T) -> Unit,
    ) {
        var id = ids[name]
        if (id != null) return
        synchronized(ids) {
            id = ids[name]
            if (id != null) return
            val newId = nextId.getAndIncrement()
            ids[name] = newId
            handlers[newId] = AttributeHandler(V::class.java, T::class.java) { view, value ->
                handler(view as V, value as T)
                id = newId
            }
        }
    }

    fun getHandler(id: Int): AttributeHandler? = handlers[id]

    fun clear() = handlers.clear()
}