package com.kalikov.game

class BaseWallBuilder(
    private val eventManager: EventManager,
    private val spriteContainer: SpriteContainer,
    private val positions: Set<Point>
) : EventSubscriber, ShovelWallBuilder {
    private companion object {
        private val subscriptions = setOf(SpriteContainer.Added::class, SpriteContainer.Removed::class)
    }

    private val walls = HashSet<Wall>()

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun buildWall(wallFactory: WallFactory) {
        positions.forEach { position ->
            val wall = wallFactory.create(position.x, position.y)
            spriteContainer.addSprite(wall)
        }
    }

    override fun destroyWall() {
        walls.forEach { wall ->
            wall.destroy()
        }
    }

    override fun notify(event: Event) {
        if (event is SpriteContainer.Added) {
            if (event.sprite is Wall && positions.contains(Point(event.sprite.x, event.sprite.y))) {
                walls.add(event.sprite)
            }
        } else if (event is SpriteContainer.Removed) {
            if (event.sprite is Wall && positions.contains(Point(event.sprite.x, event.sprite.y))) {
                walls.remove(event.sprite)
            }
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}