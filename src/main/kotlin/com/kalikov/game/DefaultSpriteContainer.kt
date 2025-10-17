package com.kalikov.game

import com.kalikov.util.ArraySet
import com.kalikov.engine.Event
import com.kalikov.engine.EventManager
import com.kalikov.engine.EventSubscriber
import kotlin.reflect.KClass

class DefaultSpriteContainer(private val eventManager: EventManager) : SpriteContainer, EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Sprite.Destroyed::class)

        private val ordering = Comparator<Sprite> { a, b ->
            val cmp = a.z - b.z
            if (cmp != 0) {
                cmp
            } else {
                a.id - b.id
            }
        }
    }

    private var sprites = ArraySet(ordering)
    private var copyOnWrite = false

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override val size get() = sprites.size

    override fun forEach(action: (Sprite) -> Unit) {
        read {
            sprites.forEach(action)
        }
    }

    override fun iterateWhile(action: (Sprite) -> Boolean): Boolean {
        return read {
            sprites.iterateWhile(action)
        }
    }

    private inline fun <T> read(action: () -> T): T {
        return if (copyOnWrite) {
            action()
        } else {
            copyOnWrite = true
            return try {
                action()
            } finally {
                copyOnWrite = false
            }
        }
    }

    override fun addSprite(sprite: Sprite) {
        if (copyOnWrite) {
            sprites = ArraySet(sprites)
        }
        if (sprites.add(sprite)) {
            eventManager.fireEvent(SpriteContainer.Added(sprite))
        }
    }

    override fun removeSprite(sprite: Sprite) {
        if (copyOnWrite) {
            sprites = ArraySet(sprites)
        }
        if (sprites.remove(sprite)) {
            eventManager.fireEvent(SpriteContainer.Removed(sprite))
        }
    }

    override fun containsSprite(sprite: Sprite): Boolean {
        return sprites.contains(sprite)
    }

    override val identity: Int
        get() = TODO("Not yet implemented")

    override fun notify(event: Event) {
        if (event is Sprite.Destroyed) {
            removeSprite(event.sprite)
        }
    }

    override fun dispose() {
        sprites.forEach {
            it.dispose()
        }

        eventManager.removeSubscriber(this, subscriptions)
    }
}