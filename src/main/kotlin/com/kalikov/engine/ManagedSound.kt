package com.kalikov.engine

interface ManagedSound : Sound {
    fun pause()

    fun resume()
}