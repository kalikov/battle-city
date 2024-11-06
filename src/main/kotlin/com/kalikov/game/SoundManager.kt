package com.kalikov.game

interface SoundManager {
    fun sound(name: String): Sound

    fun music(name: String): Music

    fun pause()

    fun resume()

    var enabled: Boolean
}