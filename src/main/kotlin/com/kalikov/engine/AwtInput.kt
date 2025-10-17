package com.kalikov.engine

import com.kalikov.game.KeyEventConfig
import com.kalikov.util.BlockingArraySimpleQueue
import com.kalikov.util.IntMap
import com.kalikov.util.IntSet
import com.kalikov.util.SimpleQueue
import java.awt.Component
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

open class AwtInput(private val frame: Component, config: Map<String, KeyEventConfig>) : KeyAdapter(), Input {
    override val lastKeyPressed get() = lastKeyCode

    private val eventQueue: SimpleQueue<Event> = BlockingArraySimpleQueue(32)

    private val pressed = IntSet()

    private val codes: IntMap<CodeItem>

    private var lastKeyCode: Int = 0

    init {
        frame.addKeyListener(this)

        codes = IntMap(config.size)
        config.forEach { (key, value) ->
            codes.put(parseCode(key), CodeItem(value))
        }
    }

    private fun parseCode(key: String): Int {
        return if (key.startsWith("0x")) {
            key.substring(2).toInt(16)
        } else {
            key.toInt()
        }
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode != KeyEvent.VK_UNDEFINED && pressed.add(e.keyCode)) {
            lastKeyCode = e.keyCode
            createKeyboardEvent(e, Keyboard::KeyPressed)?.let { pushEvent(it) }
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode != KeyEvent.VK_UNDEFINED && pressed.remove(e.keyCode)) {
            createKeyboardEvent(e, Keyboard::KeyReleased)?.let { pushEvent(it) }
        }
    }

    private fun createKeyboardEvent(e: KeyEvent, constructor: (key: Keyboard.Key, playerIndex: Int) -> Event): Event? {
        return codes.get(e.keyCode)?.getOrCreateEvent(constructor)
    }

    private fun pushEvent(event: Event) {
        eventQueue.add(event)
    }

    override fun pollEvent(): Event? {
        return eventQueue.poll()
    }

    override fun destroy() {
        frame.removeKeyListener(this)
    }

    private class CodeItem(
        val config: KeyEventConfig,
        val events: MutableMap<(key: Keyboard.Key, playerIndex: Int) -> Event, Event> = HashMap()
    ) {
        fun getOrCreateEvent(constructor: (key: Keyboard.Key, playerIndex: Int) -> Event): Event {
            return events.computeIfAbsent(constructor) {
                it(config.key, config.player - 1)
            }
        }
    }
}