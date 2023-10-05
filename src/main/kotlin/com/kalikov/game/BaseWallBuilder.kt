package com.kalikov.game

class BaseWallBuilder(
    private val eventManager: EventManager,
    private val spriteContainer: SpriteContainer,
    private val bounds: Rect,
    base: Point
) : EventSubscriber, ShovelWallBuilder {
    private companion object {
        private val subscriptions = setOf(SpriteContainer.Added::class, SpriteContainer.Removed::class)
    }

    private val walls = HashSet<Wall>()
    private val positions = HashSet<Point>()

    init {
        eventManager.addSubscriber(this, subscriptions)

        for (dx in -Globals.TILE_SIZE..Base.SIZE step Globals.TILE_SIZE) {
            addPosition(base.x + dx, base.y - Globals.TILE_SIZE)
            addPosition(base.x + dx, base.y + Base.SIZE)
        }
        for (dy in 0 until Base.SIZE step Globals.TILE_SIZE) {
            addPosition(base.x - Globals.TILE_SIZE, base.y + dy)
            addPosition(base.x + Base.SIZE, base.y + dy)
        }
    }

    private fun addPosition(x: Int, y: Int) {
        if (bounds.contains(x, y)) {
            positions.add(Point(x, y))
        }
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