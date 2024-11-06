package com.kalikov.game

interface Sound {
    fun play()

    fun stop()

    val playingCount: Int
}
