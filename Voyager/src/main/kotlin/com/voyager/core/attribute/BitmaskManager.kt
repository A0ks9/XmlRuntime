package com.voyager.core.attribute

/**
 * Memory-efficient bitmask for tracking attribute application state per View.
 */
internal class BitmaskManager(initialSize: Int = 4) {
    private var bitmaskArray = LongArray(initialSize)
    private var currentSize = initialSize
    private val BITS_PER_LONG = 64

    private fun ensureCapacity(index: Int) {
        val wordIndex = index / BITS_PER_LONG
        if (wordIndex >= currentSize) {
            val newSize = (wordIndex + 1).coerceAtLeast(currentSize * 2)
            bitmaskArray = bitmaskArray.copyOf(newSize)
            currentSize = newSize
        }
    }

    fun setIfNotSet(index: Int): Boolean {
        ensureCapacity(index)
        val wordIndex = index / BITS_PER_LONG
        val bitPosition = index % BITS_PER_LONG
        val bit = 1L shl bitPosition
        return if ((bitmaskArray[wordIndex] and bit) == 0L) {
            bitmaskArray[wordIndex] = bitmaskArray[wordIndex] or bit
            true
        } else {
            false
        }
    }

    fun clear() {
        bitmaskArray.fill(0L)
    }
} 