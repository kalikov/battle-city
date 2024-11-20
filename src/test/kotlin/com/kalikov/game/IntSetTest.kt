package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntSetTest {
    private lateinit var set: IntSet

    @BeforeEach
    fun beforeEach() {
        set = IntSet()
    }

    @Test
    fun `should correctly read empty set`() {
        assertEquals(0, set.size)
        assertTrue(set.isEmpty())

        assertFalse(set.contains(0), "Empty set should not contain 0")
        assertFalse(set.contains(10), "Empty set should not contain 10")
    }

    @Test
    fun `should add element into empty set`() {
        set.add(Int.MAX_VALUE)

        assertEquals(1, set.size)
        assertTrue(set.isNotEmpty())
        assertTrue(set.contains(Int.MAX_VALUE))
    }

    @Test
    fun `should add multiple elements`() {
        assertTrue(set.add(10), "Adding 10 should return true")
        assertTrue(set.add(20), "Adding 20 should return true")
        assertFalse(set.add(10), "Adding duplicate 10 should return false")

        assertEquals(2, set.size, "Set size should be 2")
        assertTrue(set.contains(10), "Set should contain 10")
        assertTrue(set.contains(20), "Set should contain 20")
    }

    @Test
    fun `should remove existing element`() {
        set.add(10)
        set.add(20)
        assertTrue(set.remove(10), "Removing 10 should return true")
        assertFalse(set.contains(10), "Set should not contain 10 after removal")
        assertTrue(set.contains(20), "Set should still contain 20")
        assertEquals(1, set.size, "Set size should be 1 after removal")
    }

    @Test
    fun `should remove multiple elements`() {
        set.add(10)
        set.add(20)
        set.add(Int.MIN_VALUE)
        assertTrue(set.remove(10))
        assertTrue(set.remove(20))
        assertTrue(set.remove(Int.MIN_VALUE))
        assertEquals(0, set.size, "Set size should be 0 after removal")
        assertTrue(set.isEmpty())
    }

    @Test
    fun `should remove non-existing element`() {
        set.add(10)
        set.add(20)

        assertFalse(set.remove(30), "Removing non-existent element should return false")

        assertEquals(2, set.size, "Set size should remain 2")
        assertTrue(set.contains(10), "Set should still contain 10")
        assertTrue(set.contains(20), "Set should still contain 20")
    }

    @Test
    fun `should not contain non-existing element`() {
        set.add(10)
        set.add(20)

        assertFalse(set.contains(15), "Set should not contain 15")
        assertFalse(set.contains(30), "Set should not contain 30")
    }

    @Test
    fun `should not add duplicated elements`() {
        set.add(10)
        set.add(10)
        set.add(10)

        assertEquals(1, set.size, "Set size should be 1 with duplicates")
    }

    @Test
    fun `should add boundary values`() {
        set.add(Int.MIN_VALUE)
        set.add(0)
        set.add(Int.MAX_VALUE)

        assertTrue(set.contains(Int.MIN_VALUE), "Set should contain Integer.MIN_VALUE")
        assertTrue(set.contains(0), "Set should contain 0")
        assertTrue(set.contains(Int.MAX_VALUE), "Set should contain Integer.MAX_VALUE")
    }

    @Test
    fun `should resize set`() {
        for (i in 1..100) {
            set.add(i * (2 * (i % 2) - 1))
        }

        assertEquals(100, set.size)
        for (i in 1..100) {
            assertTrue(set.contains(i * (2 * (i % 2) - 1)))
            assertFalse(set.contains(-i * (2 * (i % 2) - 1)))
        }
        assertFalse(set.contains(0))
        assertFalse(set.contains(101))
        assertFalse(set.contains(-101))
    }
}