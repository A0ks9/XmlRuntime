package com.voyager.utils.processors


import android.view.View
import com.voyager.utils.view.Attributes
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@PublishedApi
internal object AttributeProcessor {
    @Volatile
    var attributeHandlers = mutableListOf<AttributeHandler>()
    val attributeIds = ConcurrentHashMap<String, Int>()
    val nextId = AtomicInteger(0)
    private val bitmask = BitmaskManager()

    //make a fun that registerAttribute with the attr id of it

    inline fun <reified V : View, reified T> registerAttribute(
        name: String,
        noinline handler: (V, T) -> Unit,
    ) {
        if (!attributeIds.containsKey(name)) {
            synchronized(attributeIds) {
                if (!attributeIds.containsKey(name)) {
                    val id = nextId.getAndIncrement()
                    attributeIds[name] = id
                    synchronized(attributeHandlers) {
                        @Suppress("UNCHECKED_CAST") attributeHandlers.add(
                            AttributeHandler(
                                V::class.java, T::class.java, handler as (View, Any?) -> Unit
                            )
                        )
                    }
                }
            }
        } else {
            throw IllegalArgumentException("Attribute '$name' is already registered.")
        }
    }

    inline fun <reified V : View, reified T> registerAttributes(
        attributes: Map<String, (V, T) -> Unit>,
    ) {
        attributes.forEach { (name, handler) ->
            registerAttribute(name, handler)
        }
    }

    internal fun applyAttribute(view: View, name: String, value: Any?) {
        val id = attributeIds[name] ?: throw IllegalArgumentException("Unknown attribute: $name")

        if (!bitmask.setIfNotSet(id)) {
            throw IllegalStateException("Duplicate attribute detected: $name")
        }

        attributeHandlers.getOrNull(id)?.apply(view, value)
            ?: throw IllegalArgumentException("Missing handler for attribute: $name")
    }

    fun applyAttributes(view: View, attrs: Map<String, Any?>) {
        bitmask.clear()

        applyAttribute(view, Attributes.Common.ID.name, attrs[Attributes.Common.ID.name])

        val nonConstraintAttributes =
            attrs.filter { !it.key.isConstraintLayoutAttribute() && !it.key.isIDAttribute() }
        applyAttributesInternal(view, nonConstraintAttributes)

        val constraintAttributes = attrs.filter { it.key.isConstraintLayoutAttribute() }

        val constraintOnlyAttributes = constraintAttributes.filter { it.key.isConstraint() }
        applyAttributesInternal(view, constraintOnlyAttributes)

        val biasAttributes = constraintAttributes.filter { it.key.isBias() }
        applyAttributesInternal(view, biasAttributes)
    }

    private fun applyAttributesInternal(view: View, attrs: Map<String, Any?>) {
        attrs.forEach { (name, value) ->
            if (name == "android" || name == "app") return@forEach
            applyAttribute(view, name, value)
        }
    }

    private class BitmaskManager {
        @Volatile
        private var bitmaskArray = LongArray(4)

        private fun ensureCapacity(index: Int) {
            if (index shr 6 >= bitmaskArray.size) {
                synchronized(this) {
                    if (index shr 6 >= bitmaskArray.size) {
                        bitmaskArray = bitmaskArray.copyOf(bitmaskArray.size * 2)
                    }
                }
            }
        }

        fun setIfNotSet(index: Int): Boolean {
            ensureCapacity(index)
            val pos = index shr 6
            val bit = 1L shl (index and 0x3F)

            synchronized(this) {
                if ((bitmaskArray[pos] and bit) != 0L) return false
                bitmaskArray[pos] = bitmaskArray[pos] or bit
                return true
            }
        }

        fun clear() {
            bitmaskArray.fill(0L)
        }
    }

    class AttributeHandler(
        private val viewClass: Class<*>,
        private val valueClass: Class<*>,
        private val handler: (View, Any?) -> Unit,
    ) {
        fun apply(view: View, value: Any?) {
            if (!viewClass.isInstance(view)) {
                throw IllegalArgumentException("View type mismatch: Expected ${viewClass.simpleName}, got ${view::class.java.simpleName}")
            }

            if (value != null && !valueClass.isInstance(value)) {
                throw IllegalArgumentException("Value type mismatch: Expected ${valueClass.simpleName}, got ${value::class.java.simpleName}")
            }

            handler(view, value)
        }
    }

    private fun String.isIDAttribute(): Boolean =
        equals(Attributes.Common.ID.name, ignoreCase = true)

    /**
     * Checks if an attribute name belongs to ConstraintLayout.
     *
     * @return true if the attribute is a ConstraintLayout attribute, false otherwise.
     */
    private fun String.isConstraintLayoutAttribute(): Boolean =
        startsWith("layout_constraint", ignoreCase = true)

    /**
     * Checks if an attribute name is a ConstraintLayout constraint (excluding bias).
     *
     * @return true if the attribute is a ConstraintLayout constraint, false otherwise.
     */
    private fun String.isConstraint(): Boolean =
        isConstraintLayoutAttribute() && !contains("bias", ignoreCase = true)


    /**
     * Checks if an attribute name is a ConstraintLayout bias.
     *
     * @return true if the attribute is a ConstraintLayout bias, false otherwise.
     */
    private fun String.isBias(): Boolean =
        isConstraintLayoutAttribute() && contains("bias", ignoreCase = true)
}