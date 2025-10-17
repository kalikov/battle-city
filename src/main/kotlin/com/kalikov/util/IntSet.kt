package com.kalikov.util

import java.util.Arrays

private const val INITIAL_CAPACITY = 16

class IntSet(initialCapacity: Int = INITIAL_CAPACITY) {
    private var elements: IntArray
    var size = 0
        private set

    init {
        require(initialCapacity > 0) { "Initial capacity must be greater than 0" }
        elements = IntArray(initialCapacity)
        size = 0
    }

    fun add(value: Int): Boolean {
        var index = Arrays.binarySearch(elements, 0, size, value)
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

    fun contains(value: Int): Boolean {
        return Arrays.binarySearch(elements, 0, size, value) >= 0
    }

    fun remove(value: Int): Boolean {
        val index = Arrays.binarySearch(elements, 0, size, value)
        if (index < 0) {
            return false
        }

        System.arraycopy(elements, index + 1, elements, index, size - index - 1)
        size--
        elements[size] = 0
        return true
    }

    fun isEmpty(): Boolean {
        return size == 0
    }

    private fun ensureCapacity() {
        if (size >= elements.size) {
            elements = elements.copyOf(elements.size * 2)
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun IntSet.isNotEmpty() = !isEmpty()