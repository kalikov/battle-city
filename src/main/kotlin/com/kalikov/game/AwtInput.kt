package com.kalikov.game

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import javax.swing.JFrame

class AwtInput(private val frame: JFrame) : KeyAdapter(), Input {
    private val eventQueue: Queue<Event> = ConcurrentLinkedQueue()

    private val pressed = HashSet<Int>()

    init {
        frame.addKeyListener(this)
    }

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode != KeyEvent.VK_UNDEFINED && pressed.add(e.keyCode)) {
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
            KeyEvent.VK_UP -> Keyboard.Key.UP
            KeyEvent.VK_DOWN -> Keyboard.Key.DOWN
            KeyEvent.VK_LEFT -> Keyboard.Key.LEFT
            KeyEvent.VK_RIGHT -> Keyboard.Key.RIGHT
            KeyEvent.VK_ENTER -> Keyboard.Key.START
            KeyEvent.VK_BACK_SPACE -> Keyboard.Key.SELECT
            KeyEvent.VK_TAB -> Keyboard.Key.SELECT
            KeyEvent.VK_SPACE -> Keyboard.Key.SPACE
            KeyEvent.VK_S -> Keyboard.Key.S
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