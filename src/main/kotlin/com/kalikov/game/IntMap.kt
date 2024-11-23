package com.kalikov.game

import java.util.Arrays
import java.util.function.Function

private const val INITIAL_CAPACITY = 16

class IntMap<T : Any>(initialCapacity: Int = INITIAL_CAPACITY) {
    private var keys: IntArray
    private var values: Array<Any?>

    var size = 0
        private set

    init {
        require(initialCapacity > 0) { "Initial capacity must be greater than 0" }
        keys = IntArray(initialCapacity)
        values = Array(initialCapacity) { null }
        size = 0
    }

    @Suppress("UNCHECKED_CAST")
    fun put(key: Int, value: T): T? {
        val index = Arrays.binarySearch(keys, 0, size, key)
        if (index >= 0) {
            val prevValue = values[index] as T?
            values[index] = value
            return prevValue
        }

        insert(index, key, value)
        return null
    }

    @Suppress("UNCHECKED_CAST")
    fun computeIfAbsent(key: Int, function: Function<Int, T>): T {
        val index = Arrays.binarySearch(keys, 0, size, key)
        if (index >= 0) {
            return values[index] as T
        }
        val value = function.apply(key)
        insert(index, key, value)
        return value
    }

    private fun insert(binarySearchInsertionPoint: Int, key: Int, value: T) {
        val index = -(binarySearchInsertionPoint + 1)
        ensureCapacity()

        System.arraycopy(keys, index, keys, index + 1, size - index)
        System.arraycopy(values, index, values, index + 1, size - index)
        keys[index] = key
        values[index] = value
        size++
    }

    @Suppress("UNCHECKED_CAST")
    fun get(key: Int): T? {
        val index = Arrays.binarySearch(keys, 0, size, key)
        if (index >= 0) {
            return values[index] as T
        }
        return null
    }

    fun containsKey(key: Int): Boolean {
        return Arrays.binarySearch(keys, 0, size, key) >= 0
    }

    fun remove(key: Int): Boolean {
        val index = Arrays.binarySearch(keys, 0, size, key)
        if (index < 0) {
            return false
        }

        System.arraycopy(keys, index + 1, keys, index, size - index - 1)
        System.arraycopy(values, index + 1, values, index, size - index - 1)
        size--
        keys[size] = 0
        values[size] = null
        return true
    }

    fun isEmpty(): Boolean {
        return size == 0
    }

    private fun ensureCapacity() {
        if (size >= keys.size) {
            keys = keys.copyOf(keys.size * 2)
            values = values.copyOf(values.size * 2)
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> IntMap<T>.isNotEmpty() = !isEmpty()