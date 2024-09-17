package com.kalikov.game

class GameField(
    private val game: Game,
    private val mainContainer: SpriteContainer,
    private val overlayContainer: SpriteContainer,
    x: Pixel = t(2).toPixel(),
    y: Pixel = t(2).toPixel(),
) : GameFieldHandle, ShovelWallBuilder {
    companion object {
        val SIZE_IN_TILES = t(26)
        val SIZE_IN_PIXELS = SIZE_IN_TILES.toPixel()
    }

    override val bounds = PixelRect(x, y, SIZE_IN_PIXELS, SIZE_IN_PIXELS)

    private val baseWallBuildTiles = HashSet<TilePoint>()
    private val baseWallDestroyTiles = HashSet<TilePoint>()

    override lateinit var walls: Walls
        private set
    override lateinit var trees: Trees
        private set
    override lateinit var ground: Ground
        private set
    override lateinit var base: Base
        private set

    fun load(map: StageMapConfig, playersCount: Int) {
        val baseSizeInTiles = Base.SIZE.toTile()
        for (dx in -1..baseSizeInTiles.toInt()) {
            addBaseWallBuildTile(map.base.x + dx, map.base.y - 1)
            addBaseWallBuildTile(map.base.x + dx, map.base.y + baseSizeInTiles)
        }
        for (dy in 0 until baseSizeInTiles.toInt()) {
            addBaseWallBuildTile(map.base.x - 1, map.base.y + dy)
            addBaseWallBuildTile(map.base.x + baseSizeInTiles, map.base.y + dy)
        }
        for (dx in -2..baseSizeInTiles.toInt() + 1) {
            addBaseWallDestroyTile(map.base.x + dx, map.base.y - 2)
            addBaseWallDestroyTile(map.base.x + dx, map.base.y + baseSizeInTiles + 1)
        }
        for (dy in -1..baseSizeInTiles.toInt()) {
            addBaseWallDestroyTile(map.base.x - 2, map.base.y + dy)
            addBaseWallDestroyTile(map.base.x + baseSizeInTiles + 1, map.base.y + dy)
        }

        trees = Trees(game, bounds.x, bounds.y, map.trees)

        walls = Walls(game, bounds.x, bounds.y, map.walls)

        ground = Ground(game, bounds.x, bounds.y, map.ground)
        ground.isStatic = playersCount == 0

        if (playersCount > 0) {
            clearTile(map.base.x, map.base.y)
            clearTile(map.base.x + 1, map.base.y)
            clearTile(map.base.x, map.base.y + 1)
            clearTile(map.base.x + 1, map.base.y + 1)
            map.enemySpawnPoints.forEach {
                clearTile(it.x, it.y)
                clearTile(it.x + 1, it.y)
                clearTile(it.x, it.y + 1)
                clearTile(it.x + 1, it.y + 1)
            }
        }
        map.playerSpawnPoints.asSequence().take(playersCount).forEach {
            clearTile(it.x, it.y)
            clearTile(it.x + 1, it.y)
            clearTile(it.x, it.y + 1)
            clearTile(it.x + 1, it.y + 1)
        }

        base = Base(
            game.eventManager,
            game.imageManager,
            bounds.x + map.base.x.toPixel(),
            bounds.y + map.base.y.toPixel(),
        )
        if (playersCount == 0) {
            val baseTiles = setOf(
                map.base,
                map.base.translate(t(1), t(0)),
                map.base.translate(t(0), t(1)),
                map.base.translate(t(1), t(1))
            )
            if (baseTiles.any { map.trees.contains(it) || map.ground.ice.contains(it) || map.ground.water.contains(it) } ||
                baseTiles.any { map.walls.steel.contains(it) } ||
                map.walls.bricks.any { baseTiles.contains(TilePoint(it.x, it.y)) }) {
                base.isHidden = true
            }
        }
    }

    private fun clearTile(x: Tile, y: Tile) {
        walls.clearTile(x, y)
        trees.clearTile(x, y)
        ground.clearTile(x, y)
    }

    private fun addBaseWallBuildTile(x: Tile, y: Tile) {
        if (x >= 0 && y >= 0 && x < SIZE_IN_TILES && y < SIZE_IN_TILES) {
            baseWallBuildTiles.add(TilePoint(x, y))
        }
    }

    private fun addBaseWallDestroyTile(x: Tile, y: Tile) {
        if (x >= 0 && y >= 0 && x < SIZE_IN_TILES && y < SIZE_IN_TILES) {
            baseWallDestroyTiles.add(TilePoint(x, y))
        }
    }

    fun update() {
        ground.update()

        mainContainer.forEach { it.update() }
        overlayContainer.forEach { it.update() }
    }

    fun draw(surface: ScreenSurface) {
        surface.fillRect(bounds.x, bounds.y, bounds.width, bounds.height, ARGB.BLACK)

        ground.draw(surface)
        walls.draw(surface)

        base.draw(surface)

        mainContainer.forEach {
            if (!it.isDestroyed) {
                it.draw(surface)
            }
        }

        trees.draw(surface)

        overlayContainer.forEach {
            if (!it.isDestroyed) {
                it.draw(surface)
            }
        }
    }

    override fun buildBrickWall() {
        clearBaseWallUnits()
        baseWallBuildTiles.forEach {
            walls.fillBrickTile(it.x, it.y)
        }
    }

    override fun buildSteelWall() {
        clearBaseWallUnits()
        baseWallBuildTiles.forEach {
            walls.fillSteelTile(it.x, it.y)
        }
    }

    private fun clearBaseWallUnits() {
        baseWallDestroyTiles.forEach {
            walls.clearTile(it.x, it.y)
        }
    }

    fun dispose() {
        walls.dispose()
        ground.dispose()
        trees.dispose()
        base.dispose()
    }
}