package com.kalikov.game

interface EntityFactory {
    fun create(type: String, x: Int, y: Int): Sprite
}