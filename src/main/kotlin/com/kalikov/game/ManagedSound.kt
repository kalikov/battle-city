package com.kalikov.game

interface ManagedSound : Sound {
    fun pause()

    fun resume()
}