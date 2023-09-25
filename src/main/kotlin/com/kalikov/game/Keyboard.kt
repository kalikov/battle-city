package com.kalikov.game

object Keyboard {
    enum class Key {
        SPACE,
        LEFT,
        UP,
        RIGHT,
        DOWN,
        S,
        SELECT,
        START,
    }

    data class KeyPressed(val key: Key) : Event()

    data class KeyReleased(val key: Key) : Event()
}