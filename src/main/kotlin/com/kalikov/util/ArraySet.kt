package com.kalikov.util

import java.util.Arrays
import kotlin.math.max

private const val INITIAL_CAPACITY = 16

class ArraySet<T : Any>(
    private val comparator: Comparator<T>,
    initialCapacity: Int = INITIAL_CAPACITY,
) {
    private var elements: Array<T?>
    var size = 0
        private set

    constructor(set: ArraySet<T>) : this(set.comparator, set.elements.size) {
        System.arraycopy(set.elements, 0, elements, 0, set.size)
        size = set.size
    }

    constructor(comparator: Comparator<T>, vararg elements: T) : this(comparator, max(INITIAL_CAPACITY, elements.size)) {
        for (element in elements) {
            add(element)
        }
    }

    init {
        require(initialCapacity > 0) { "Initial capacity must be greater than 0" }
        @Suppress("UNCHECKED_CAST")
        elements = arrayOfNulls<Any>(initialCapacity) as Array<T?>
        size = 0
    }

    fun add(value: T): Boolean {
        var index = Arrays.binarySearch(elements, 0, size, value, comparator)
        if (index >= 0) {
            return false
        }

        index = -(index + 1)
        ensureCapacity()

        System.arraycopy(elements, index, elements, index + 1, size - index)
        elements[index] = value
        size++
        return true
    }

    fun contains(value: T): Boolean {
        return Arrays.binarySearch(elements, 0, size, value, comparator) >= 0
    }

    fun remove(value: T): Boolean {
        val index = Arrays.binarySearch(elements, 0, size, value, comparator)
        if (index < 0) {
            return false
        }

        System.arraycopy(elements, index + 1, elements, index, size - index - 1)
        size--
        elements[size] = null
        return true
    }

    fun isEmpty(): Boolean {
        return size == 0
    }

    fun forEach(action: (T) -> Unit) {
        for (i in 0 until size) {
            action(requireNotNull(elements[i]))
        }
    }

    fun iterateWhile(action: (T) -> Boolean): Boolean {
        for (i in 0 until size) {
            if (!action(requireNotNull(elements[i]))) {
                return false
            }
        }
        return true
    }

    private fun ensureCapacity() {
        if (size >= elements.size) {
            elements = elements.copyOf(elements.size * 2)
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> ArraySet<T>.isNotEmpty() = !isEmpty()