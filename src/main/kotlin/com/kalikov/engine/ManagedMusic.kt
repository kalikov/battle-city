package com.kalikov.engine

import com.kalikov.game.Music

interface ManagedMusic : Music {
    fun pause()

    fun resume()
}