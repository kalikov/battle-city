package com.kalikov.game

interface SpriteContainer {
    data class Added(val sprite: Sprite) : Event()

    data class Removed(val sprite: Sprite) : Event()

    val sprites: Collection<Sprite>

    fun addSprite(sprite: Sprite)

    fun removeSprite(sprite: Sprite)

    fun containsSprite(sprite: Sprite): Boolean

    fun dispose()
}