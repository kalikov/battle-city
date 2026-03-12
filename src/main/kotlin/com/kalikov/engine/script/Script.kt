package com.kalikov.engine.script

import kotlin.math.max

class Script : ScriptCallback {
    var isActive = true

    val isDone: Boolean get() = endExclusive == start

    val size: Int get() = endExclusive - start

    private var nodes = arrayOfNulls<ScriptNode?>(32)
    private var start = 0
    private var endExclusive = 0

    fun enqueue(node: ScriptNode) {
        if (start > 0) {
            if (endExclusive == start) {
                endExclusive = 0
            } else {
                System.arraycopy(nodes, start, nodes, 0, endExclusive - start)
                for (i in max(start, endExclusive - start) until endExclusive) {
                    nodes[i] = null
                }
                endExclusive -= start
            }
            start = 0
        }
        if (endExclusive >= nodes.size) {
            nodes = nodes.copyOf(2 * nodes.size)
        }
        nodes[endExclusive] = node
        endExclusive++
    }

    fun update() {
        if (!isActive) {
            return
        }
        while (true) {
            val node = nodes[start]
            if (node == null) {
                return
            }
            if (!node.isDisposable) {
                node.update()
                break
            }
            node.update()
            nodes[start] = null
            start++
        }
    }

    override fun actionCompleted() {
        nodes[start] = null
        start++
    }

    fun clear() {
        for (i in start until endExclusive) {
            nodes[i] = null
        }
        start = 0
        endExclusive = 0
    }
}