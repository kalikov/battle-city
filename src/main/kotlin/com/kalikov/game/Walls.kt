package com.kalikov.game

class Walls(
    private val game: Game,
    private val x: Pixel,
    private val y: Pixel,
    config: WallsConfig,
) : WallsHandle {
    private companion object {
        private val BRICK_SIZE = Globals.TILE_SIZE / 2

        private val SIZE_IN_TILES = GameField.SIZE_IN_TILES
        private val SIZE_IN_BRICKS = GameField.SIZE_IN_TILES.bricksCount()
    }

    data object Hit : Event()

    private val collisionMatrix = Array(SIZE_IN_TILES.toInt()) { Array(SIZE_IN_TILES.toInt()) { false } }
    private val brickMatrix = Array(SIZE_IN_BRICKS) { Array<ScreenSurface?>(SIZE_IN_BRICKS) { null } }

    private val mask = game.screen.createSurface(GameField.SIZE_IN_PIXELS, GameField.SIZE_IN_PIXELS)

    private val steelImage = game.imageManager.getImage("wall_steel")
    private val brickImage = game.imageManager.getImage("wall_brick")

    override val config: WallsConfig
        get() {
            val bricks = mutableSetOf<BrickTile>()
            val steel = mutableSetOf<TilePoint>()
            for (x in brickMatrix.indices step 2) {
                for (y in brickMatrix[x].indices step 2) {
                    if (brickMatrix[x][y] === steelImage) {
                        steel.add(TilePoint(t(x / 2), t(y / 2)))
                    } else {
                        var integrity = 0
                        if (brickMatrix[x][y] === brickImage) {
                            integrity = integrity or 0x8
                        }
                        if (brickMatrix[x + 1][y] === brickImage) {
                            integrity = integrity or 0x4
                        }
                        if (brickMatrix[x][y + 1] === brickImage) {
                            integrity = integrity or 0x1
                        }
                        if (brickMatrix[x + 1][y + 1] === brickImage) {
                            integrity = integrity or 0x2
                        }
                        if (integrity != 0) {
                            bricks.add(BrickTile(t(x / 2), t(y / 2), integrity))
                        }
                    }
                }
            }
            return WallsConfig(steel, bricks)
        }

    init {
        LeaksDetector.add(this)

        mask.clear(ARGB.TRANSPARENT)
        config.bricks.forEach {
            collisionMatrix[it.x.toInt()][it.y.toInt()] = true
            val bricksX = it.x.bricksCount()
            val bricksY = it.y.bricksCount()
            if ((it.integrity and 0x8) != 0) {
                fillMaskBrick(bricksX, bricksY, brickImage)
            }
            if (it.integrity and 0x1 != 0) {
                fillMaskBrick(bricksX, bricksY + 1, brickImage)
            }
            if (it.integrity and 0x4 != 0) {
                fillMaskBrick(bricksX + 1, bricksY, brickImage)
            }
            if (it.integrity and 0x2 != 0) {
                fillMaskBrick(bricksX + 1, bricksY + 1, brickImage)
            }
        }
        config.steel.forEach {
            fillTile(it.x, it.y, steelImage)
        }
    }

    private fun fillMaskBrick(x: Int, y: Int, image: ScreenSurface?) {
        if (brickMatrix[x][y] !== image) {
            brickMatrix[x][y] = image
            if (image == null) {
                mask.clear(x * BRICK_SIZE, y * BRICK_SIZE, BRICK_SIZE, BRICK_SIZE, ARGB.TRANSPARENT)
            } else {
                mask.draw(
                    x * BRICK_SIZE,
                    y * BRICK_SIZE,
                    image,
                    (x % 2) * BRICK_SIZE,
                    (y % 2) * BRICK_SIZE,
                    BRICK_SIZE,
                    BRICK_SIZE
                )
            }
        }
    }

    override fun collides(tank: Tank, dx: Pixel, dy: Pixel): Boolean {
        val top = (tank.bounds.top + dy - y).toTile()
        val left = (tank.bounds.left + dx - x).toTile()
        val right = (tank.bounds.right + dx - x).toTile()
        val bottom = (tank.bounds.bottom + dy - y).toTile()
        for (x in left.toInt()..right.toInt()) {
            for (y in top.toInt()..bottom.toInt()) {
                if (collisionMatrix[x][y]) {
                    return true
                }
            }
        }
        return false
    }

    override fun occupied(x: Tile, y: Tile): Boolean {
        return collisionMatrix[x.toInt()][y.toInt()]
    }

    override fun hit(bullet: Bullet): Boolean {
        val bricksTop = (bullet.bounds.top - y) / BRICK_SIZE
        val bricksLeft = (bullet.bounds.left - x) / BRICK_SIZE
        val bricksRight = (bullet.bounds.right - x) / BRICK_SIZE
        val bricksBottom = (bullet.bounds.bottom - y) / BRICK_SIZE
        var intersectsBrick = false
        var intersectsSteel = false
        for (bricksX in bricksLeft..bricksRight) {
            for (bricksY in bricksTop..bricksBottom) {
                val tileX = t(bricksX / 2)
                val tileY = t(bricksY / 2)
                if (brickMatrix[bricksX][bricksY] === steelImage) {
                    intersectsSteel = true
                    if (bullet.type == Bullet.Type.ENHANCED) {
                        fillTile(tileX, tileY, null)
                    }
                }
                if (brickMatrix[bricksX][bricksY] === brickImage) {
                    intersectsBrick = true
                    if (bullet.type == Bullet.Type.ENHANCED) {
                        fillTile(tileX, tileY, null)
                    } else {
                        fillMaskBrick(bricksX, bricksY, null)
                        if (bullet.direction == Direction.DOWN || bullet.direction == Direction.UP) {
                            if (bricksX % 2 == 0) {
                                fillMaskBrick(bricksX + 1, bricksY, null)
                            } else {
                                fillMaskBrick(bricksX - 1, bricksY, null)
                            }
                        } else {
                            if (bricksY % 2 == 0) {
                                fillMaskBrick(bricksX, bricksY + 1, null)
                            } else {
                                fillMaskBrick(bricksX, bricksY - 1, null)
                            }
                        }
                        collisionMatrix[tileX.toInt()][tileY.toInt()] = false
                                || brickMatrix[tileX.bricksCount()][tileY.bricksCount()] != null
                                || brickMatrix[tileX.bricksCount()][tileY.bricksCount() + 1] != null
                                || brickMatrix[tileX.bricksCount() + 1][tileY.bricksCount()] != null
                                || brickMatrix[tileX.bricksCount() + 1][tileY.bricksCount() + 1] != null
                    }
                }
            }
        }
        if (intersectsSteel || intersectsBrick) {
            if (bullet.tank is PlayerTank) {
                if (intersectsSteel) {
                    game.eventManager.fireEvent(SoundManager.Play("bullet_hit_1"))
                }
                if (intersectsBrick) {
                    game.eventManager.fireEvent(SoundManager.Play("bullet_hit_2"))
                }
            }
            game.eventManager.fireEvent(Hit)
            return true
        }
        return false
    }

    fun draw(surface: ScreenSurface) {
        surface.draw(x, y, mask)
    }

    fun dispose() {
        LeaksDetector.remove(this)
    }

    override fun fillBrickTile(x: Tile, y: Tile) {
        fillTile(x, y, brickImage)
    }

    override fun fillSteelTile(x: Tile, y: Tile) {
        fillTile(x, y, steelImage)
    }

    override fun clearTile(x: Tile, y: Tile) {
        fillTile(x, y, null)
    }

    private fun fillTile(x: Tile, y: Tile, image: ScreenSurface?) {
        collisionMatrix[x.toInt()][y.toInt()] = image != null
        val bricksX = x.bricksCount()
        val bricksY = y.bricksCount()
        fillMaskBrick(bricksX, bricksY, image)
        fillMaskBrick(bricksX, bricksY + 1, image)
        fillMaskBrick(bricksX + 1, bricksY, image)
        fillMaskBrick(bricksX + 1, bricksY + 1, image)
    }
}