package com.kalikov.util

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

class WeakIdentityToIntMap<K : Any> private constructor(
    private var keys: Array<WeakRef<K>?>,
    private val hashFunction: (K) -> Int = System::identityHashCode,
) {
    private companion object {
        /**
         * The initial capacity used by the no-args constructor.
         * MUST be a power of two.  The value 32 corresponds to the
         * (specified) expected maximum size of 21, given a load factor
         * of 2/3.
         */
        private const val DEFAULT_CAPACITY = 32

        /**
         * The minimum capacity, used if a lower value is implicitly specified
         * by either of the constructors with arguments.  The value 4 corresponds
         * to an expected maximum size of 2, given a load factor of 2/3.
         * MUST be a power of two.
         */
        private const val MINIMUM_CAPACITY = 4

        /**
         * The maximum capacity, used if a higher value is implicitly specified
         * by either of the constructors with arguments.
         * MUST be a power of two <= 1<<29.
         *
         * In fact, the map can hold no more than MAXIMUM_CAPACITY-1 items
         * because it has to have at least one slot with the key == null
         * in order to avoid infinite loops in get(), put(), remove()
         */
        private const val MAXIMUM_CAPACITY = 1 shl 29

        /**
         * Returns the appropriate capacity for the given expected maximum size.
         * Returns the smallest power of two between MINIMUM_CAPACITY and
         * MAXIMUM_CAPACITY, inclusive, that is greater than (3 *
         * expectedMaxSize)/2, if such a number exists. Otherwise, returns MAXIMUM_CAPACITY.
         */
        private fun capacity(expectedMaxSize: Int): Int {
            require(expectedMaxSize >= 0)
            return if ((expectedMaxSize > MAXIMUM_CAPACITY / 3)) {
                MAXIMUM_CAPACITY
            } else if ((expectedMaxSize <= 2 * MINIMUM_CAPACITY / 3)) {
                MINIMUM_CAPACITY
            } else {
                Integer.highestOneBit(expectedMaxSize + (expectedMaxSize shl 1))
            }
        }

        /**
         * Initializes object to be an empty map with the specified initial
         * capacity, which is assumed to be a power of two between
         * MINIMUM_CAPACITY and MAXIMUM_CAPACITY inclusive.
         */
        private fun <T : Any> init(initCapacity: Int): Array<WeakRef<T>?> {
            check((initCapacity and -initCapacity) == initCapacity) // power of 2
            check(initCapacity >= MINIMUM_CAPACITY)
            check(initCapacity <= MAXIMUM_CAPACITY)

            return arrayOfNulls(initCapacity)
        }
    }

    private val queue = ReferenceQueue<K>()

    private var values = IntArray(keys.size)

    val capacity get() = keys.size

    private var _size = 0

    val size: Int
        get() {
            removeStaleEntries()
            return _size
        }

    constructor() : this(init(DEFAULT_CAPACITY))

    constructor(expectedMaxSize: Int) : this(init(capacity(expectedMaxSize)))

    constructor(hashFunction: (K) -> Int) : this(init(DEFAULT_CAPACITY), hashFunction)

    fun isEmpty(): Boolean {
        removeStaleEntries()
        return _size == 0
    }

    fun getOrDefault(key: K, defaultValue: Int): Int {
        removeStaleEntries()
        val keys = keys
        val index = getItemIndex(key, keys.size)
        var i = index
        do {
            val item = keys[i] ?: return defaultValue
            if (item.get() === key) {
                return values[i]
            }
            i = getNextIndex(i, keys.size)
        } while (i != index)
        return defaultValue
    }

    fun containsKey(key: K): Boolean {
        removeStaleEntries()
        val keys = keys
        val index = getItemIndex(key, keys.size)
        var i = index
        do {
            val item = keys[i] ?: return false
            if (item.get() === key) {
                return true
            }
            i = getNextIndex(i, keys.size)
        } while (i != index)
        return false
    }

    fun computeIfAbsent(key: K, function: () -> Int): Int {
        removeStaleEntries()
        val hash = hashFunction(key)
        while (true) {
            val keys = keys
            val index = getHashIndex(hash, keys.size)
            var i = index
            do {
                val item = keys[i]
                if (item == null) {
                    val value = function()
                    keys[i] = WeakRef(key, queue, hash)
                    values[i] = value
                    _size++
                    return value
                }
                if (item.get() === key) {
                    return values[i]
                }
                i = getNextIndex(i, keys.size)
            } while (i != index)
            resize()
        }
    }

    fun put(key: K, value: Int): Boolean {
        removeStaleEntries()
        val hash = hashFunction(key)
        while (true) {
            val keys = keys
            val values = values
            val index = getHashIndex(hash, keys.size)

            var i = index
            do {
                val item = keys[i]
                if (item == null) {
                    keys[i] = WeakRef(key, queue, hash)
                    values[i] = value
                    _size++
                    return true
                }
                if (item.get() === key) {
                    values[i] = value
                    return false
                }
                i = getNextIndex(i, keys.size)
            } while (i != index)
            resize()
        }
    }

    fun remove(key: K): Boolean {
        removeStaleEntries()
        val keys = keys
        val values = values
        val index = getItemIndex(key, keys.size)

        var j = -1
        var i = index
        var removed = false
        for (n in keys.indices) {
            val item = keys[i] ?: break
            if (j >= 0) {
                val itemIndex = getHashIndex(item.hash, keys.size)
                if (!isInRange(itemIndex, j, i)) {
                    keys[j] = item
                    values[j] = values[i]
                    keys[i] = null
                    j = i
                }
            } else if (item.get() === key) {
                keys[i] = null
                removed = true
                _size--
                j = i
            }
            i = getNextIndex(i, keys.size)
        }
        return removed
    }

    private fun getItemIndex(x: K, length: Int) = getHashIndex(hashFunction(x), length)

    private fun getHashIndex(hash: Int, length: Int) = hash and (length - 1)

    private fun getNextIndex(i: Int, length: Int) = if (i + 1 < length) i + 1 else 0

    private fun removeStaleEntries() {
        val keys = keys
        val values = values
        var ref: WeakRef<out K>? = queue.poll() as? WeakRef<out K>
        while (ref != null) {
            val index = getHashIndex(ref.hash, keys.size)

            var j = -1
            var i = index
            for (n in keys.indices) {
                val item = keys[i] ?: break
                if (j >= 0) {
                    val itemIndex = getHashIndex(item.hash, keys.size)
                    if (!isInRange(itemIndex, j, i)) {
                        keys[j] = item
                        values[j] = values[i]
                        keys[i] = null
                        j = i
                    }
                } else if (item === ref) {
                    keys[i] = null
                    _size--
                    j = i
                }
                i = getNextIndex(i, keys.size)
            }
            ref = queue.poll() as? WeakRef<out K>
        }
    }

    private fun isInRange(index: Int, fromExclusive: Int, toInclusive: Int): Boolean {
        if (fromExclusive <= toInclusive) {
            return index in (fromExclusive + 1) .. toInclusive
        }
        return index > fromExclusive || index <= toInclusive
    }

    private fun resize(): Boolean {
        val oldKeys = keys
        val oldValues = values
        check(oldKeys.size < MAXIMUM_CAPACITY) { "Capacity exhausted." }
        val newLength = oldKeys.size * 2
        check(oldKeys.size < newLength)

        val newKeys = arrayOfNulls<WeakRef<K>?>(newLength)
        val newValues = IntArray(newLength)
        var newSize = 0

        var j = 0
        while (j < oldKeys.size) {
            val ref = oldKeys[j]
            oldKeys[j] = null
            val key = ref?.get()
            if (key != null) {
                val value = oldValues[j]
                var i = getItemIndex(key, newLength)
                while (newKeys[i] != null) {
                    i = getNextIndex(i, newLength)
                }
                newKeys[i] = ref
                newValues[i] = value
                newSize++
            }
            j++
        }
        keys = newKeys
        values = newValues
        _size = newSize
        return true
    }

    private class WeakRef<T>(referent: T, queue: ReferenceQueue<T>, val hash: Int) : WeakReference<T>(referent, queue)
}