package com.kalikov.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArraySetTest {
    private val intCmp = Comparator<Int> { a, b -> a.compareTo(b) }

    @Test
    fun `add keeps elements sorted and avoids duplicates`() {
        val s = ArraySet(intCmp)
        assertTrue(s.add(5))
        assertTrue(s.add(2))
        assertTrue(s.add(4))
        assertTrue(s.add(1))
        assertTrue(s.add(3))
        assertFalse(s.add(3)) // duplicate

        assertEquals(5, s.size)
        val seen = mutableListOf<Int>()
        s.forEach { seen += it }
        assertEquals(listOf(1, 2, 3, 4, 5), seen)

        (1..5).forEach { assertTrue(s.contains(it)) }
        assertFalse(s.contains(0))
        assertFalse(s.contains(6))
    }

    @Test
    fun `remove shifts left and clears tail slot`() {
        val s = ArraySet(intCmp, 1, 2, 3, 4, 5)
        assertTrue(s.remove(3))
        assertFalse(s.remove(3)) // already gone
        assertEquals(4, s.size)

        val seen = mutableListOf<Int>()
        s.forEach { seen += it }
        assertEquals(listOf(1, 2, 4, 5), seen)
    }

    @Test
    fun `iterateWhile short-circuits and returns proper result`() {
        val s = ArraySet(intCmp, 1, 2, 3, 4, 5)
        val visited = mutableListOf<Int>()
        val result = s.iterateWhile { v ->
            visited += v
            v < 3 // stop when we hit 3
        }
        assertFalse(result)
        assertEquals(listOf(1, 2, 3), visited)
    }

    @Test
    fun `copy constructor creates independent copy`() {
        val original = ArraySet(intCmp, 1, 2, 3)
        val copy = ArraySet(original)

        // mutate original
        assertTrue(original.remove(2))
        assertEquals(2, original.size)
        assertEquals(3, copy.size) // unaffected

        // mutate copy
        assertTrue(copy.add(4))
        assertEquals(2, original.size) // still unaffected (1,3) + previous check
        val copySeen = mutableListOf<Int>()
        copy.forEach { copySeen += it }
        assertEquals(listOf(1, 2, 3, 4), copySeen)
    }

    @Test
    fun `grows capacity and maintains ordering under stress`() {
        val s = ArraySet(intCmp, initialCapacity = 1) // force multiple resizes
        val n = 1_000

        for (v in n downTo 1) {
            assertTrue(s.add(v))
        }
        assertEquals(n, s.size)

        var expected = 1
        s.forEach { v ->
            assertEquals(expected, v)
            expected++
        }

        assertTrue(s.contains(1))
        assertTrue(s.contains(500))
        assertTrue(s.contains(1000))

        assertTrue(s.remove(1))       // head
        assertTrue(s.remove(500))     // middle
        assertTrue(s.remove(1000))    // tail
        assertEquals(n - 3, s.size)
        assertFalse(s.contains(1))
        assertFalse(s.contains(500))
        assertFalse(s.contains(1000))
    }

    @Test
    fun `vararg constructor deduplicates and sorts`() {
        val s = ArraySet(intCmp, 5, 3, 3, 2, 1, 2)
        assertEquals(4, s.size)
        val seen = mutableListOf<Int>()
        s.forEach { seen += it }
        assertEquals(listOf(1, 2, 3, 5), seen)
    }

    @Test
    fun `isEmpty and isNotEmpty reflect state`() {
        val s = ArraySet(intCmp)
        assertTrue(s.isEmpty())
        assertFalse(s.isNotEmpty())
        assertTrue(s.add(42))
        assertFalse(s.isEmpty())
        assertTrue(s.isNotEmpty())
        assertTrue(s.remove(42))
        assertTrue(s.isEmpty())
    }
}