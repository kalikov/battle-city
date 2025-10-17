package com.kalikov.engine

interface Input {
    val lastKeyPressed: Int

    fun pollEvent(): Event?

    fun destroy()
}