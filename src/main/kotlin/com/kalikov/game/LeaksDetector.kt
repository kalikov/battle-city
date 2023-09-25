package com.kalikov.game

object LeaksDetector {
    private val items = LinkedHashSet<Any>()

    fun add(item: Any) {
        items.add(item)
    }

    fun remove(item: Any) {
        items.remove(item)
    }

    fun print() {
        if (items.isEmpty()) {
            return
        }
        println("Leaks detected:")
        for (item in items) {
            println(item)
        }
    }
}