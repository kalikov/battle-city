package com.kalikov.game

class GameField(
    private val eventRouter: EventRouter,
    private val imageManager: ImageManager,
    private val entityFactory: EntityFactory,
    private val spriteContainer: SpriteContainer,
    x: Int = Globals.UNIT_SIZE,
    y: Int = Globals.UNIT_SIZE
) {
    private companion object {
        private const val SIZE = 13 * Globals.UNIT_SIZE
    }

    val bounds = Rect(x, y, SIZE, SIZE)

    fun load(map: StageMapConfig) {
        val cleanRects = mutableSetOf(
            Rect(map.base, 2, 2).multiply(Globals.TILE_SIZE).translate(bounds.x, bounds.y),
            Rect(map.playerSpawnPoint, 2, 2).multiply(Globals.TILE_SIZE).translate(bounds.x, bounds.y),
        )
        map.enemySpawnPoints.forEach {
            cleanRects.add(Rect(it, 2, 2).multiply(Globals.TILE_SIZE).translate(bounds.x, bounds.y))
        }
        for (mapObject in map.objects) {
            val sprite = entityFactory.create(
                mapObject.type,
                bounds.x + mapObject.x * Globals.TILE_SIZE,
                bounds.y + mapObject.y * Globals.TILE_SIZE
            )
            if (cleanRects.none { it.intersects(sprite.bounds) }) {
                spriteContainer.addSprite(sprite)
            } else {
                sprite.dispose()
            }
        }
        spriteContainer.addSprite(
            Base(
                eventRouter,
                imageManager,
                bounds.x + map.base.x * Globals.TILE_SIZE,
                bounds.y + map.base.y * Globals.TILE_SIZE
            )
        )
    }

    fun update() {
        val sprites = spriteContainer.sprites
        sprites.forEach { it.update() }
    }

    fun draw(surface: ScreenSurface) {
        surface.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, ARGB.BLACK)

        val sprites = spriteContainer.sprites
        sprites.filterNot { it.isDestroyed }.forEach { it.draw(surface) }
    }
}