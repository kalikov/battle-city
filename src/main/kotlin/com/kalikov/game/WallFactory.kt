package com.kalikov.game

interface WallFactory {
    fun create(x: Int, y: Int): Wall
}