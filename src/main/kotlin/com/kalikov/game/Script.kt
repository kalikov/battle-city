package com.kalikov.game

import java.util.LinkedList

class Script : ScriptCallback {
    var isActive = true

    val isEmpty: Boolean get() = nodes.isEmpty()

    val size: Int get() = nodes.size

    private val nodes = LinkedList<ScriptNode>()

    fun enqueue(node: ScriptNode) {
        nodes.add(node)
    }

    fun update() {
        if (!isActive) {
            return
        }
        while (true) {
            if (nodes.size == 0) {
                return
            }
            if (!nodes[0].isDisposable) {
                break
            }
            nodes[0].update()
            nodes.removeFirst()
        }
        nodes[0].update()
    }

    override fun actionCompleted() {
        nodes.removeFirst()
    }
}