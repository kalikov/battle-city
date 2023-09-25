package com.kalikov.game

import java.util.concurrent.ConcurrentSkipListSet

class ConcurrentSpriteContainer(private val eventManager: EventManager) : SpriteContainer, EventSubscriber {
    private val ordering = Comparator<Sprite> { a, b ->
        val cmp = a.z - b.z
        if (cmp != 0) {
            cmp
        } else {
            System.identityHashCode(a) - System.identityHashCode(b)
        }
    }

    override val sprites = ConcurrentSkipListSet(ordering)

    init {
        eventManager.addSubscriber(this, setOf(Sprite.Destroyed::class))
    }

    override fun addSprite(sprite: Sprite) {
        if (sprites.add(sprite)) {
            eventManager.fireEvent(SpriteContainer.Added(sprite))
        }
    }

    override fun removeSprite(sprite: Sprite) {
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