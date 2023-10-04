package com.kalikov.game

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import javax.swing.JFrame

class AwtInput(private val frame: JFrame, private val config: KeyboardConfig) : KeyAdapter(), Input {
    private val eventQueue: Queue<Event> = ConcurrentLinkedQueue()

    private val pressed = HashSet<Int>()

    override var lastKeyPressed: Int = 0
        private set

    init {
        frame.addKeyListener(this)
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode != KeyEvent.VK_UNDEFINED && pressed.add(e.keyCode)) {
            lastKeyPressed = e.keyCode
            createKeyboardEvent(e) { Keyboard.KeyPressed(it) }?.let { pushEvent(it) }
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (e.keyCode != KeyEvent.VK_UNDEFINED) {
            createKeyboardEvent(e) { Keyboard.KeyReleased(it) }?.let { pushEvent(it) }
            pressed.remove(e.keyCode)
        }
    }

    private fun createKeyboardEvent(e: KeyEvent, constructor: (key: Keyboard.Key) -> Event): Event? {
        return when (e.keyCode) {
            config.up -> Keyboard.Key.UP
            config.down -> Keyboard.Key.DOWN
            config.left -> Keyboard.Key.LEFT
            config.right -> Keyboard.Key.RIGHT
            config.start -> Keyboard.Key.START
            config.select -> Keyboard.Key.SELECT
            config.action -> Keyboard.Key.ACTION
            else -> null
        }?.let(constructor)
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