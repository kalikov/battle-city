package com.kalikov.game

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

private const val INITIAL_CAPACITY = 16

class ConcurrentArraySimpleQueue<E : Any>(capacity: Int = INITIAL_CAPACITY) : SimpleQueue<E> {
    @Volatile
    private var elements: AtomicReferenceArray<E>
    private val head = AtomicInteger(0)
    private val tail = AtomicInteger(0)
    private val lock = ReentrantReadWriteLock(true)

    init {
        require(capacity > 0) { "Capacity must be greater than 0" }
        this.elements = AtomicReferenceArray(capacity)
    }

    override fun add(element: E) {
        while (true) {
            lock.read {
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
            lock.write {
                val currentHead = head.get()
                val currentTail = tail.get()
                val size = currentTail - currentHead
                val capacity = elements.length()

                if (size >= capacity) {
                    val newCapacity = 2 * capacity
                    val newElements = AtomicReferenceArray<E>(newCapacity)
                    for (i in currentHead until currentTail) {
                        newElements.set(i % newCapacity, elements.get(i % capacity))
                    }
                    elements = newElements
                }
            }
        }
    }

    override fun poll(): E? {
        while (true) {
            lock.read {
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
}