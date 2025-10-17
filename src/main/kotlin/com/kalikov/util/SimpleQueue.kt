package com.kalikov.util

interface SimpleQueue<E> {
    fun add(element: E)

    fun poll(): E?
}