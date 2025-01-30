package com.flipkart.android.proteus.toolbox

/**
 * Kotlin class for a simple iterator over an array of type E.
 *
 * This iterator provides basic iteration over the elements of an array.
 * It does not support the `remove()` operation.
 *
 * @param elements The array of elements to iterate over.
 * @param E The type of elements in the array.
 */
class SimpleArrayIterator<E>(private val elements: Array<E>) :
    Iterator<E> { // Converted to Kotlin class, 'elements' is now a private property

    private var cursor = 0 // Cursor to track the current position in the array

    /**
     * Checks if there are more elements to iterate over.
     *
     * @return `true` if there are more elements, `false` otherwise.
     */
    override fun hasNext(): Boolean =
        cursor < elements.size // Simplified hasNext using expression body

    /**
     * Returns the next element in the iteration and advances the cursor.
     *
     * @return The next element in the array.
     * @throws NoSuchElementException if there are no more elements.
     */
    override fun next(): E {
        if (!hasNext()) { // Check for hasNext before accessing element to prevent errors
            throw NoSuchElementException() // Throw exception if no more elements
        }
        return elements[cursor++] // Return the current element and increment the cursor (post-increment)
    }
}

/**
 * Extension function to create an iterator for an IntArray.
 *
 * This provides a convenient way to get an Iterator<Integer> for an int array in Kotlin.
 *
 * @return An Iterator<Integer> for the IntArray.
 */
fun IntArray.iterator(): Iterator<Int> =
    object : Iterator<Int> { // Extension function on IntArray to create an iterator
        private var cursor = 0 // Cursor for the IntArray iterator

        override fun hasNext(): Boolean =
            cursor < size // Simplified hasNext for IntArray using 'size'

        override fun next(): Int {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            return this@iterator[cursor++] // Return the current int element and increment cursor, using 'this@iterator' to refer to IntArray
        }
    }