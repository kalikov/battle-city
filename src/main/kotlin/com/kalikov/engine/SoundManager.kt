package com.kalikov.engine

import com.kalikov.game.Music

interface SoundManager {
    fun sound(name: String): Sound

    fun music(name: String): Music

    fun pause()

    fun resume()

    var enabled: Boolean
}