package com.kalikov.engine

interface Sound {
    fun play()

    fun stop()

    val playingCount: Int
}
