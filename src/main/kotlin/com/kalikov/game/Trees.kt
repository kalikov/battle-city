package com.kalikov.game

class Trees(
    game: Game,
    private val x: Pixel,
    private val y: Pixel,
    config: Set<TilePoint>,
) : TreesHandle {
    private val mask = game.screen.createSurface(GameField.SIZE_IN_PIXELS, GameField.SIZE_IN_PIXELS)

    private val image = game.imageManager.getImage("trees")

    private val tiles = HashSet<TilePoint>(config.size)

    val config: Set<TilePoint> get() = HashSet(tiles)

    init {
        LeaksDetector.add(this)

        mask.clear(ARGB.TRANSPARENT)
        config.forEach { fillTile(it.x, it.y) }
    }

    fun draw(surface: ScreenSurface) {
        surface.draw(x, y, mask)
    }

    fun dispose() {
        LeaksDetector.remove(this)
    }

    override fun fillTile(x: Tile, y: Tile) {
        val point = TilePoint(x, y)
        if (tiles.add(point)) {
            mask.draw(x.toPixel(), y.toPixel(), image)
        }
    }

    override fun clearTile(x: Tile, y: Tile) {
        val point = TilePoint(x, y)
        if (tiles.remove(point)) {
            mask.clear(x.toPixel(), y.toPixel(), Globals.TILE_SIZE, Globals.TILE_SIZE, ARGB.TRANSPARENT)
        }
    }
}