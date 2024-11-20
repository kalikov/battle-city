package com.kalikov.game

interface SimpleQueue<E> {
    fun add(element: E)

    fun poll(): E?
}