package com.kalikov.game

import com.kalikov.util.WeakIdentityToIntMap
import org.junit.jupiter.api.Test
import java.util.IdentityHashMap
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WeakIdentityToIntMapTest {
    @Test
    fun `isEmpty should return true for a new map`() {
        val map = WeakIdentityToIntMap<String>()
        assertTrue(map.isEmpty())
        assertEquals(0, map.size)
    }

    @Test
    fun `put should add a new entry`() {
        val map = WeakIdentityToIntMap<String>()
        val key = "key1"

        map.put(key, 42)

        assertFalse(map.isEmpty())
        assertEquals(1, map.size)
    }

    @Test
    fun `computeIfAbsent should add an entry only once`() {
        val map = WeakIdentityToIntMap<String>()
        val key = "key1"

        assertEquals(42, map.computeIfAbsent(key) { 42 })
        assertFalse(map.isEmpty())
        assertEquals(1, map.size)

        assertEquals(42, map.computeIfAbsent(key) { 100 })
        assertEquals(42, map.computeIfAbsent(key) { 200 })
        assertEquals(42, map.getOrDefault(key, 0))

        assertFalse(map.isEmpty())
        assertEquals(1, map.size)
    }

    @Test
    fun `should resize correctly`() {
        val map = WeakIdentityToIntMap<String>(6)
        val keys = Array(map.capacity) { it.toString() }
        for (i in keys.indices) {
            map.put(keys[i], i + 1)
        }

        for (i in keys.indices) {
            assertEquals(i + 1, map.getOrDefault(keys[i], 0))
        }
        assertFalse(map.isEmpty())
        assertEquals(keys.size, map.size)

        map.put("test", 100)

        for (i in keys.indices) {
            assertEquals(i + 1, map.getOrDefault(keys[i], 0))
        }
        assertEquals(100, map.getOrDefault("test", 0))
        assertFalse(map.isEmpty())
        assertEquals(keys.size + 1, map.size)
    }

    @Test
    fun `get should return the correct value for an existing key`() {
        val map = WeakIdentityToIntMap<String>()
        val key = "key1"

        map.put(key, 42)
        assertEquals(42, map.getOrDefault(key, 0), "get should return the correct value")
    }

    @Test
    fun `get should return default value for a missing key`() {
        val map = WeakIdentityToIntMap<String>()

        assertEquals(100, map.getOrDefault("missingKey", 100), "get should return default value (0) for a missing key")
    }

    @Test
    fun `containsKey should return true for an existing key`() {
        val map = WeakIdentityToIntMap<String>()
        val key = "key1"

        map.put(key, 42)
        assertTrue(map.containsKey(key), "containsKey should return true for an existing key")
    }

    @Test
    fun `containsKey should return false for a missing key`() {
        val map = WeakIdentityToIntMap<String>()

        assertFalse(map.containsKey("missingKey"), "containsKey should return false for a missing key")
    }

    @Test
    fun `put should overwrite an existing key with a new value`() {
        val map = WeakIdentityToIntMap<String>()
        val key = "key1"

        map.put(key, 42)
        map.put(key, 84)

        assertEquals(84, map.getOrDefault(key, 0), "put should overwrite the existing value")

        assertFalse(map.isEmpty())
        assertEquals(1, map.size)
    }

    @Test
    fun `get should respect identity semantics for keys`() {
        val map = WeakIdentityToIntMap<String>()
        val key1 = String("key1".toCharArray()) // Different object with the same value
        val key2 = String("key1".toCharArray()) // Different object with the same value

        map.put(key1, 42)

        assertFalse(map.containsKey(key2), "containsKey should respect identity semantics")
        assertEquals(0, map.getOrDefault(key2, 0), "get should return default value for a different object with the same value")

        assertTrue(map.containsKey(key1))
    }

    @Test
    fun `keys should be garbage collected`() {
        val map = WeakIdentityToIntMap<String>()
        var key: String? = String("key1".toCharArray()) // Create a unique object
        map.put(key!!, 42)

        assertTrue(map.containsKey(key), "containsKey should return true before key is nullified")

        @Suppress("AssignedValueIsNeverRead")
        key = null // Nullify the strong reference to the key

        gc()

        assertTrue(map.isEmpty())
        assertEquals(0, map.size)
    }

    @Test
    fun `GC should preserve remaining values`() {
        val map = WeakIdentityToIntMap<String>()
        val chars = "key1".toCharArray()
        val keys = Array<String?>(map.capacity) { String(chars) } // Keys with the same hash value

        for (i in keys.indices) {
            map.put(keys[i]!!, i + 1)
        }

        for (i in keys.indices) {
            assertEquals(i + 1, map.getOrDefault(keys[i]!!, 0))
        }

        keys[keys.size / 2] = null

        gc()

        assertFalse(map.isEmpty())
        assertEquals(keys.size - 1, map.size)
        for (i in keys.indices) {
            val key = keys[i]
            if (key != null) {
                assertEquals(i + 1, map.getOrDefault(key, 0))
            }
        }
    }

    @Test
    fun `should correctly shift on removal`() {
        val map = WeakIdentityToIntMap<Int> { it }
        val boundary = Integer.MAX_VALUE - 100
        val keys = arrayOf(boundary, boundary + 1, boundary + 1, boundary + 3, boundary, boundary, boundary)
        for (i in keys.indices) {
            map.put(keys[i], i + 1)
        }
        map.remove(keys[2])
        assertEquals(4, map.getOrDefault(keys[3], 0))
    }

    @Test
    fun `stress test`() {
        var counter = 0
        val map = WeakIdentityToIntMap<String>()
        val source = IdentityHashMap<String, Int>()

        var capacity = map.capacity
        var resizingCount = 0

        fun add() {
            val chars = CharArray(10) { ('a' .. 'z').random() }
            val key = String(chars)
            val value = ++counter
            source.computeIfAbsent(key) { value }
            map.computeIfAbsent(key) { value }
            if (map.capacity != capacity) {
                capacity = map.capacity
                resizingCount++
            }
        }

        repeat(3 * map.capacity / 2) {
            add()
        }

        fun validate() {
            assertEquals(source.size, map.size)
            for (key in source.keys) {
                assertEquals(source[key], map.getOrDefault(key, 0))
            }
        }

        validate()

        var iteration = 0
        while (iteration < 10000 || resizingCount < 10) {
            if (iteration % 100 == 0) {
                System.gc()
                System.gc()
            }
            val remove = Random.nextInt(100) >= 75
            if (remove && source.isNotEmpty()) {
                val key = source.keys.random()
                source.remove(key)
                map.remove(key)
            } else {
                add()
            }
            validate()
            iteration++
        }
    }

    @Test
    fun `remove returns false when key is not present`() {
        val map = WeakIdentityToIntMap<Any>()
        val k1 = Any()
        val k2 = Any()
        map.put(k1, 10)

        // Remove a different key (never inserted).
        val removed = map.remove(k2)

        assertFalse(removed, "remove(keyAbsent) must return false.")
        assertEquals(1, map.size)
        assertTrue(map.containsKey(k1))
    }

    private fun gc() {
        System.gc()
        System.gc()

        Thread.sleep(100)
    }
}