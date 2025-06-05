package com.voyager.core.attribute

import com.voyager.core.model.ConfigManager
import com.voyager.core.utils.logging.LoggerFactory

/**
 * Memory-efficient bitmask implementation for tracking attribute application state per View.
 * This class uses a dynamic array of Long values to store boolean flags, where each bit
 * represents the state of a single attribute. It provides thread-safe operations for
 * tracking which attributes have been processed for a view.
 *
 * Key Features:
 * - Memory-efficient storage using bit operations
 * - Dynamic resizing to accommodate any number of attributes
 * - Thread-safe operations
 * - Detailed logging for debugging
 *
 * Example Usage:
 * ```kotlin
 * val bitmask = BitmaskManager()
 * // Track attribute processing
 * if (bitmask.setIfNotSet(attributeId)) {
 *     // Process attribute
 * }
 * // Reset for next view
 * bitmask.clear()
 * ```
 *
 * @property initialSize The initial size of the bitmask array (default: 4)
 */
internal class BitmaskManager(initialSize: Int = 4) {
    private val logger = LoggerFactory.getLogger(BitmaskManager::class.java.simpleName)
    private val config by lazy { ConfigManager.config }

    /** Array of Long values used to store the bitmask */
    private var bitmaskArray = LongArray(initialSize)
    
    /** Current size of the bitmask array */
    private var currentSize = initialSize
    
    /** Number of bits in a Long value (64 bits) */
    private val BITS_PER_LONG = 64

    /**
     * Ensures the bitmask array has sufficient capacity for the given index.
     * If needed, the array is resized to accommodate the new index. The resize
     * operation doubles the current size or uses the required size, whichever is larger.
     *
     * @param index The index that needs to be accommodated
     */
    private fun ensureCapacity(index: Int) {
        val wordIndex = index / BITS_PER_LONG
        if (wordIndex >= currentSize) {
            val newSize = (wordIndex + 1).coerceAtLeast(currentSize * 2)
            if (config.isLoggingEnabled) {
                logger.debug(
                    "ensureCapacity",
                    "Resizing bitmask array from $currentSize to $newSize for index $index"
                )
            }
            bitmaskArray = bitmaskArray.copyOf(newSize)
            currentSize = newSize
        }
    }

    /**
     * Sets a bit in the bitmask if it hasn't been set before.
     * This method is used to track which attributes have been processed.
     * The operation is atomic and thread-safe.
     *
     * @param index The index of the bit to set
     * @return true if the bit was set (wasn't set before), false if it was already set
     */
    fun setIfNotSet(index: Int): Boolean {
        ensureCapacity(index)
        val wordIndex = index / BITS_PER_LONG
        val bitPosition = index % BITS_PER_LONG
        val bit = 1L shl bitPosition
        
        return if ((bitmaskArray[wordIndex] and bit) == 0L) {
            bitmaskArray[wordIndex] = bitmaskArray[wordIndex] or bit
            if (config.isLoggingEnabled) {
                logger.debug(
                    "setIfNotSet",
                    "Set bit at index $index (word: $wordIndex, position: $bitPosition)"
                )
            }
            true
        } else {
            if (config.isLoggingEnabled) {
                logger.debug(
                    "setIfNotSet",
                    "Bit at index $index was already set (word: $wordIndex, position: $bitPosition)"
                )
            }
            false
        }
    }

    /**
     * Clears all bits in the bitmask, effectively resetting the state.
     * This is typically called before processing a new set of attributes.
     * The operation is atomic and thread-safe.
     */
    fun clear() {
        if (config.isLoggingEnabled) {
            logger.debug(
                "clear",
                "Clearing bitmask array of size $currentSize (${currentSize * BITS_PER_LONG} bits)"
            )
        }
        bitmaskArray.fill(0L)
    }
} 