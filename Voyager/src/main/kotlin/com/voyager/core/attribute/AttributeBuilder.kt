package com.voyager.core.attribute

import android.view.View
import com.voyager.core.utils.ErrorUtils
import com.voyager.core.utils.logging.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

@PublishedApi
internal val errorUtils = ErrorUtils("AttributeBuilder")

class AttributeBuilder<V : View> {
    // Using ConcurrentHashMap for thread-safe operations
    @PublishedApi
    internal val attributeHandler = ConcurrentHashMap<String, Pair<Class<*>, (V, Any) -> Unit>>()

    inline fun <reified T> attribute(name: String, crossinline handler: (V, T) -> Unit) {
        attributeHandler[name] = T::class.java to { view, value ->
            errorUtils.tryOrThrow(
                { handler(view, value as T) },
                "Type mismatch",
                "Attribute: $name, Expected: ${T::class.java}, Got: ${value.let { it::class.java }}"
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> attribute(name: String, valueClass: Class<T>, handler: (V, T) -> Unit) {
        attributeHandler[name] = valueClass to { view, value ->
            errorUtils.tryOrThrow(
                { handler(view, value as T) },
                "Type mismatch",
                "Attribute: $name, Expected: $valueClass, Got: ${value.let { it::class.java }}"
            )
        }
    }
}

@PublishedApi
internal fun <V : View> registerAll(
    viewClass: Class<out V>,
    builder: AttributeBuilder<V>,
) {
    builder.attributeHandler.forEach { (name, value) ->
        errorUtils.tryOrThrow(
            {
                val (valueClass, handler) = value
                AttributeRegistry.register(name, viewClass, valueClass) { view, value ->
                    @Suppress("UNCHECKED_CAST") handler(view as V, value)
                }
            }, "Registration failed", "Attribute: $name"
        )
    }
}

inline fun <reified V : View> attributesForView(
    block: AttributeBuilder<V>.() -> Unit,
) {
    val builder = AttributeBuilder<V>()
    builder.block()
    registerAll(V::class.java, builder)
}

inline fun <reified V : View, reified T> String.asAttribute(crossinline handler: (V, T) -> Unit) =
    errorUtils.tryOrThrow(
        { AttributeRegistry.register(this, handler) }, "Registration failed", "Attribute: $this"
    )

object JavaAttributeBuilder {
    private val errorUtils = ErrorUtils("JavaAttributeBuilder")

    @JvmStatic
    fun <V : View> forView(viewClass: Class<V>) = AttributeBuilder<V>()

    @JvmStatic
    fun registerAll(viewClass: Class<out View>, builder: AttributeBuilder<View>) {
        errorUtils.tryOrThrow(
            { registerAll(viewClass, builder) }, "Registration failed", "View: $viewClass"
        )
    }
}