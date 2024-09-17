package com.kalikov.game

class Ground(
    game: Game,
    private val x: Pixel,
    private val y: Pixel,
    config: GroundConfig,
) : GroundHandle {
    private companion object {
        private val waterFrames = intArrayOf(1, 2)
    }

    private val masks = Array(waterFrames.max() + 1) {
        game.screen.createSurface(GameField.SIZE_IN_PIXELS, GameField.SIZE_IN_PIXELS)
    }

    private val matrix = Array(GameField.SIZE_IN_TILES.toInt()) {
        Array<ScreenSurface?>(GameField.SIZE_IN_TILES.toInt()) { null }
    }

    private val iceImage = game.imageManager.getImage("ice")

    private val waterImage = game.imageManager.getImage("water")
    private val waterAnimation = Animation.pauseAware(game.eventManager, frameLoopOf(*waterFrames), game.clock, 500)
    private val waterImageWidth = waterImage.height

    var isStatic: Boolean = false

    val config: GroundConfig
        get() {
            val ice = mutableSetOf<TilePoint>()
            val water = mutableSetOf<TilePoint>()
            for (x in matrix.indices) {
                for (y in matrix[x].indices) {
                    when {
                        matrix[x][y] === iceImage -> {
                            ice.add(TilePoint(t(x), t(y)))
                        }

                        matrix[x][y] === waterImage -> {
                            water.add(TilePoint(t(x), t(y)))
                        }
                    }
                }
            }
            return GroundConfig(ice, water)
        }

    init {
        LeaksDetector.add(this)

        masks.forEach { it.clear(ARGB.TRANSPARENT) }

        config.ice.forEach { fillIceTile(it.x, it.y) }
        config.water.forEach { fillWaterTile(it.x, it.y) }
    }

    override fun collides(tank: Tank, dx: Pixel, dy: Pixel): Boolean {
        val top = (tank.bounds.top + dy - y).toTile().toInt()
        val left = (tank.bounds.left + dx - x).toTile().toInt()
        val right = (tank.bounds.right + dx - x).toTile().toInt()
        val bottom = (tank.bounds.bottom + dy - y).toTile().toInt()
        for (x in left..right) {
            for (y in top..bottom) {
                if (matrix[x][y] == waterImage) {
                    return true
                }
            }
        }
        return false
    }

    override fun isTankOnIce(tank: Tank): Boolean {
        val area = tank.hitRect.area
        var firmArea = area
        val top = (tank.bounds.top - y).toTile().toInt()
        val left = (tank.bounds.left - x).toTile().toInt()
        val right = (tank.bounds.right - x).toTile().toInt()
        val bottom = (tank.bounds.bottom - y).toTile().toInt()
        for (column in left..right) {
            for (row in top..bottom) {
                if (matrix[column][row] === iceImage) {
                    val tileLeft = x + t(column).toPixel()
                    val tileRight = x + t(column + 1).toPixel() - 1
                    val tileTop = y + t(row).toPixel()
                    val tileBottom = y + t(row + 1).toPixel() - 1
                    val intersects = tileLeft <= tank.hitRect.right && tileRight >= tank.hitRect.left &&
                            tileTop <= tank.hitRect.bottom && tileBottom >= tank.hitRect.top
                    if (intersects) {
                        val intersectionLeft = max(tileLeft, tank.hitRect.left)
                        val intersectionRight = min(tileRight, tank.hitRect.right)
                        val intersectionTop = max(tileTop, tank.hitRect.top)
                        val intersectionBottom = min(tileBottom, tank.hitRect.bottom)
                        firmArea -= (intersectionRight - intersectionLeft + 1) * (intersectionBottom - intersectionTop + 1)
                    }
                }
            }
        }
        return firmArea <= area / 2
    }

    fun update() {
        if (!waterAnimation.isRunning) {
            waterAnimation.restart()
        }
        waterAnimation.update()
    }

    fun draw(surface: ScreenSurface) {
        val index = if (isStatic) 0 else waterAnimation.frame
        surface.draw(x, y, masks[index])
    }

    fun dispose() {
        waterAnimation.dispose()

        LeaksDetector.remove(this)
    }

    override fun fillIceTile(x: Tile, y: Tile) {
        if (matrix[x.toInt()][y.toInt()] !== iceImage) {
            matrix[x.toInt()][y.toInt()] = iceImage
            masks.forEach {
                it.draw(x.toPixel(), y.toPixel(), iceImage)
            }
        }
    }

    override fun fillWaterTile(x: Tile, y: Tile) {
        if (matrix[x.toInt()][y.toInt()] !== waterImage) {
            matrix[x.toInt()][y.toInt()] = waterImage
            masks.forEachIndexed { index, mask ->
                val srcX = waterImageWidth * index
                mask.draw(x.toPixel(), y.toPixel(), waterImage, srcX, px(0), waterImageWidth, waterImage.height)
            }
        }
    }

    override fun clearTile(x: Tile, y: Tile) {
        if (matrix[x.toInt()][y.toInt()] !== null) {
            matrix[x.toInt()][y.toInt()] = null
            masks.forEach {
                it.clear(x.toPixel(), y.toPixel(), Globals.TILE_SIZE, Globals.TILE_SIZE, ARGB.TRANSPARENT)
            }
        }
    }
}