package com.kalikov.game

interface SpriteContainer {
    val size: Int

    data class Added(val sprite: Sprite) : Event()

    data class Removed(val sprite: Sprite) : Event()

    fun forEach(action: (Sprite) -> Unit)

    fun iterateWhile(action: (Sprite) -> Boolean): Boolean

    fun addSprite(sprite: Sprite)

    fun removeSprite(sprite: Sprite)

    fun containsSprite(sprite: Sprite): Boolean

    fun dispose()
}