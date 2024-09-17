package com.kalikov.game

import java.util.TreeSet
import kotlin.Unit

class DefaultSpriteContainer(private val eventManager: EventManager) : SpriteContainer, EventSubscriber {
    private val ordering = Comparator<Sprite> { a, b ->
        val cmp = a.z - b.z
        if (cmp != 0) {
            cmp
        } else {
            a.id - b.id
        }
    }

    private var sprites = TreeSet(ordering)
    private var copyOnWrite = false

    init {
        eventManager.addSubscriber(this, setOf(Sprite.Destroyed::class))
    }

    override val size get() = sprites.size

    override fun forEach(action: (Sprite) -> Unit) {
        copyOnWrite = true
        try {
            sprites.forEach(action)
        } finally {
            copyOnWrite = false
        }
    }

    override fun iterateWhile(action: (Sprite) -> Boolean): Boolean {
        copyOnWrite = true
        try {
            for (sprite in sprites) {
                if (!action(sprite)) {
                    return false
                }
            }
        } finally {
            copyOnWrite = false
        }
        return true
    }

    override fun addSprite(sprite: Sprite) {
        if (copyOnWrite) {
            sprites = TreeSet(sprites)
            copyOnWrite = false
        }
        if (sprites.add(sprite)) {
            eventManager.fireEvent(SpriteContainer.Added(sprite))
        }
    }

    override fun removeSprite(sprite: Sprite) {
        if (copyOnWrite) {
            sprites = TreeSet(sprites)
            copyOnWrite = false
        }
        if (sprites.remove(sprite)) {
            eventManager.fireEvent(SpriteContainer.Removed(sprite))
        }
    }

    override fun containsSprite(sprite: Sprite): Boolean {
        return sprites.contains(sprite)
    }

    override fun notify(event: Event) {
        if (event is Sprite.Destroyed) {
            removeSprite(event.sprite)
        }
    }

    override fun dispose() {
        sprites.forEach {
            it.dispose()
        }

        eventManager.removeSubscriber(this, setOf(Sprite.Destroyed::class))
    }
}