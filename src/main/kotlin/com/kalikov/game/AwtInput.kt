package com.kalikov.game

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import javax.swing.JFrame

class AwtInput(private val frame: JFrame, config: Map<String, KeyEventConfig>) : KeyAdapter(), Input {
    private val eventQueue: Queue<Event> = ConcurrentLinkedQueue()

    private val pressed = HashSet<Int>()

    private val codes: Map<Int, KeyEventConfig>

    override var lastKeyPressed: Int = 0
        private set

    init {
        frame.addKeyListener(this)

        codes = config.asSequence().associateBy({ parseCode(it.key) }, { it.value })
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
            lastKeyPressed = e.keyCode
            createKeyboardEvent(e, Keyboard::KeyPressed)?.let { pushEvent(it) }
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode != KeyEvent.VK_UNDEFINED) {
            createKeyboardEvent(e, Keyboard::KeyReleased)?.let { pushEvent(it) }
            pressed.remove(e.keyCode)
        }
    }

    private fun createKeyboardEvent(e: KeyEvent, constructor: (key: Keyboard.Key, playerIndex: Int) -> Event): Event? {
        return codes[e.keyCode]?.let { constructor(it.key, it.player - 1) }
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
}