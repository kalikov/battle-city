package com.kalikov.game

interface Input {
    val lastKeyPressed: Int

    fun pollEvent(): Event?

    fun destroy()
}