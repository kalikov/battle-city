package com.kalikov.game

interface Input {
    fun pollEvent(): Event?

    fun destroy()
}