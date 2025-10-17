package com.kalikov.util

import com.kalikov.util.SimpleQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceArray

private const val INITIAL_CAPACITY = 16

class BlockingArraySimpleQueue<E : Any>(capacity: Int = INITIAL_CAPACITY) : SimpleQueue<E> {
    private val elements: AtomicReferenceArray<E>
    private val head = AtomicInteger(0)
    private val tail = AtomicInteger(0)

    init {
        require(capacity > 0) { "Capacity must be greater than 0" }
        elements = AtomicReferenceArray(capacity)
    }

    override fun add(element: E) {
        while (true) {
            val currentTail = tail.get()
            val currentHead = head.get()
            val size = currentTail - currentHead

            val capacity = elements.length()
            if (size < capacity) {
                val index = currentTail % capacity
                if (elements.compareAndSet(index, null, element)) {
                    if (tail.compareAndSet(currentTail, currentTail + 1)) {
                        return
                    }
                    check(elements.compareAndSet(index, element, null))
                }
                continue
            }
        }
    }

    override fun poll(): E? {
        while (true) {
            val currentHead = head.get()
            val currentTail = tail.get()
            val size = currentTail - currentHead

            if (size <= 0) {
                return null
            }

            val index = currentHead % elements.length()

            val element = elements.get(index)

            if (element != null && head.compareAndSet(currentHead, currentHead + 1)) {
                check(elements.compareAndSet(index, element, null))
                return element
            }
        }
    }
}