package com.flipkart.android.proteus.gson

import com.flipkart.android.proteus.value.Value

/**
 * Abstract class for creating custom value type adapters.
 * This allows different types of values to be handled dynamically.
 */
abstract class CustomValueTypeAdapterCreator<V : Value> {

    /**
     * Creates a [CustomValueTypeAdapter] for a given type.
     *
     * @param type The integer identifier for the value type.
     * @param factory The factory responsible for creating adapters.
     * @return A new instance of [CustomValueTypeAdapter].
     */
    abstract fun create(type: Int, factory: ProteusTypeAdapterFactory): CustomValueTypeAdapter<V>
}
