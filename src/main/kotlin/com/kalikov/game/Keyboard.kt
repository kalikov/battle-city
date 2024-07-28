package com.kalikov.game

object Keyboard {
    enum class Key {
        ACTION,
        LEFT,
        UP,
        RIGHT,
        DOWN,
        SELECT,
        START,
    }

    data class KeyPressed(val key: Key, val playerIndex: Int) : Event()

    data class KeyReleased(val key: Key, val playerIndex: Int) : Event()
}