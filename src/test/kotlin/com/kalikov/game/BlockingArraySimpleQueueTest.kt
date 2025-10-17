package com.kalikov.game

import com.kalikov.util.BlockingArraySimpleQueue
import com.kalikov.util.IntSet
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BlockingArraySimpleQueueTest {
    @Test
    fun `should add and poll in the same thread`() {
        val queue = BlockingArraySimpleQueue<Int>(4)

        queue.add(1)
        queue.add(2)
        queue.add(3)

        assertEquals(1, queue.poll())
        assertEquals(2, queue.poll())
        assertEquals(3, queue.poll())
        assertNull(queue.poll(), "Polling an empty queue should return null")
    }

    @Test
    fun `should poll from empty queue`() {
        val queue = BlockingArraySimpleQueue<String>(4)
        assertNull(queue.poll(), "Polling from an empty queue should return null")
    }

    @Test
    fun `should block when queue is full`() {
        val initialCapacity = 4
        val queue = BlockingArraySimpleQueue<Int>(initialCapacity)

        for (i in 1 .. 4) {
            queue.add(i)
        }

        val executor = Executors.newSingleThreadExecutor()
        val future = executor.submit {
            queue.add(5)
        }
        Thread.sleep(1000)
        assertFalse(future.isDone)

        assertEquals(1, queue.poll())
        future.get(1, TimeUnit.SECONDS)
        for (i in 2 .. 5) {
            assertEquals(i, queue.poll(), "Queue should grow dynamically and preserve order")
        }

        assertNull(queue.poll(), "Polling from an empty queue after growth should return null")
    }

    @RepeatedTest(value = 100)
    fun `should add concurrently`() {
        val executor = Executors.newFixedThreadPool(4)
        val productionSize = 5000
        val queue = BlockingArraySimpleQueue<Int>(productionSize * 4)

        repeat(4) { producerId ->
            executor.submit {
                for (i in 1 .. productionSize) {
                    queue.add(producerId * productionSize + i)
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
        executor.shutdownNow()

        val elements = IntSet(4 * productionSize)

        var element = queue.poll()
        while (element != null) {
            assertTrue(elements.add(element), "Duplicated element $element")
            element = queue.poll()
        }

        assertEquals(4 * productionSize, elements.size, "All ${4 * productionSize} elements should be added and polled")

        for (i in 1 .. 4 * productionSize) {
            assertTrue(elements.contains(i), "Element $i was not polled")
        }
    }

    @RepeatedTest(value = 100)
    fun `should poll concurrently`() {
        val totalCount = 40000
        val queue = BlockingArraySimpleQueue<Int>(totalCount)
        for (i in 1 .. totalCount) {
            queue.add(i)
        }

        val executor = Executors.newFixedThreadPool(4)

        val elements = ConcurrentHashMap<Int, Unit>(totalCount)

        repeat(4) {
            executor.submit {
                var element = queue.poll()
                while (element != null) {
                    assertNull(elements.put(element, Unit), "Duplicated element $element")
                    element = queue.poll()
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
        executor.shutdownNow()

        assertEquals(totalCount, elements.size, "All $totalCount elements should be added and polled")
        for (i in 1 .. totalCount) {
            assertTrue(elements.containsKey(i), "Element $i was not polled")
        }
    }

    @RepeatedTest(value = 100)
    fun `should add and poll concurrently`() {
        val queue = BlockingArraySimpleQueue<Int>(8)
        val executor = Executors.newFixedThreadPool(4)
        val produced = AtomicInteger()
        val productionSize = 10000
        val futures = mutableListOf<Future<*>>()

        repeat(2) { producerId ->
            futures.add(executor.submit {
                for (i in 1 .. productionSize) {
                    queue.add(producerId * productionSize + i)
                }
                produced.incrementAndGet()
            })
        }

        val elements = ConcurrentHashMap<Int, Unit>()
        repeat(2) {
            futures.add(executor.submit {
                while (produced.get() < 2) {
                    queue.poll()?.let { element ->
                        assertNull(elements.put(element, Unit), "Duplicated element $element")
                    }
                }
                var element = queue.poll()
                while (element != null) {
                    assertNull(elements.put(element, Unit), "Duplicated element $element")
                    element = queue.poll()
                }
            })
        }

        futures.forEach { it.get() }

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
        executor.shutdownNow()

        assertEquals(2 * productionSize, elements.size, "All ${2 * productionSize} elements should be added and polled")
        for (i in 1 .. 2 * productionSize) {
            assertTrue(elements.containsKey(i), "Element $i was not polled")
        }
    }

    @RepeatedTest(value = 100)
    fun `should add and poll concurrently same value`() {
        val queue = BlockingArraySimpleQueue<Int>(8)
        val executor = Executors.newFixedThreadPool(4)
        val produced = AtomicInteger()
        val productionSize = 10000
        val futures = mutableListOf<Future<*>>()

        repeat(2) {
            futures.add(executor.submit {
                repeat (productionSize) {
                    queue.add(100)
                }
                produced.incrementAndGet()
            })
        }

        val polled = AtomicInteger()
        repeat(2) {
            futures.add(executor.submit {
                while (produced.get() < 2) {
                    queue.poll()?.let {
                        polled.incrementAndGet()
                    }
                }
                var element = queue.poll()
                while (element != null) {
                    polled.incrementAndGet()
                    element = queue.poll()
                }
            })
        }

        futures.forEach { it.get()}

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
        executor.shutdownNow()

        assertEquals(2 * productionSize, polled.get(), "All ${2 * productionSize} elements should be added and polled")
    }

    @RepeatedTest(value = 100)
    fun `stress test`() {
        val queue = BlockingArraySimpleQueue<Int>(16)
        val executor = Executors.newFixedThreadPool(8)

        val producerCount = 4
        val consumerCount = 4
        val totalElementsPerProducer = 2500

        val producedElements = ConcurrentHashMap<Int, Unit>()
        val consumedElements = ConcurrentHashMap<Int, Unit>()

        repeat(producerCount) { producerId ->
            executor.submit {
                for (i in 1 .. totalElementsPerProducer) {
                    val value = producerId * totalElementsPerProducer + i
                    queue.add(value)
                    producedElements[value] = Unit
                }
            }
        }

        repeat(consumerCount) {
            executor.submit {
                var element = queue.poll()
                while (element != null || producedElements.size < producerCount * totalElementsPerProducer) {
                    if (element != null) {
                        assertNull(consumedElements.put(element, Unit), "Duplicated element $element")
                    }
                    element = queue.poll()
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
        executor.shutdownNow()

        assertEquals(producedElements.size, consumedElements.size, "All produced elements should be consumed")
        assertEquals(producedElements, consumedElements, "Consumed elements should match produced elements")
    }

    @RepeatedTest(value = 100)
    fun `should poll concurrently from empty queue`() {
        val queue = BlockingArraySimpleQueue<String>(4)
        val executor = Executors.newFixedThreadPool(4)

        val results = ArrayList<String?>(400)

        repeat(4) {
            executor.submit {
                repeat(100) {
                    val value = queue.poll()
                    synchronized(results) {
                        results.add(value)
                    }
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(10, TimeUnit.SECONDS)
        executor.shutdownNow()

        assertEquals(400, results.size, "Each poller should attempt 10 polls")
        assertTrue(results.all { it == null }, "All polls should return null")
    }
}