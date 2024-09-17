package com.kalikov.game

import kotlin.Unit

class Builder(private val gameField: GameFieldHandle) : BuilderHandler {
    enum class Structure {
        BRICK_WALL_RIGHT,
        BRICK_WALL_BOTTOM,
        BRICK_WALL_LEFT,
        BRICK_WALL_TOP,
        BRICK_WALL_FULL,

        STEEL_WALL_RIGHT,
        STEEL_WALL_BOTTOM,
        STEEL_WALL_LEFT,
        STEEL_WALL_TOP,
        STEEL_WALL_FULL,

        WATER,
        TREES,
        ICE,

        CLEAR
    }

    var structure = Structure.BRICK_WALL_RIGHT

    override fun build(cursor: Cursor) {
        clear(cursor)
        when (structure) {
            Structure.BRICK_WALL_RIGHT -> buildBrickWallRight(cursor)
            Structure.BRICK_WALL_BOTTOM -> buildBrickWallBottom(cursor)
            Structure.BRICK_WALL_LEFT -> buildBrickWallLeft(cursor)
            Structure.BRICK_WALL_TOP -> buildBrickWallTop(cursor)
            Structure.BRICK_WALL_FULL -> buildBrickWallFull(cursor)
            Structure.STEEL_WALL_RIGHT -> buildSteelWallRight(cursor)
            Structure.STEEL_WALL_BOTTOM -> buildSteelWallBottom(cursor)
            Structure.STEEL_WALL_LEFT -> buildSteelWallLeft(cursor)
            Structure.STEEL_WALL_TOP -> buildSteelWallTop(cursor)
            Structure.STEEL_WALL_FULL -> buildSteelWallFull(cursor)
            Structure.WATER -> buildWater(cursor)
            Structure.TREES -> buildTrees(cursor)
            Structure.ICE -> buildIce(cursor)
            Structure.CLEAR -> {}
        }
    }


    private fun buildBrickWallRight(cursor: Cursor) {
        buildWallRight(cursor, WallsHandle::fillBrickTile)
    }

    private fun buildBrickWallBottom(cursor: Cursor) {
        buildWallBottom(cursor, WallsHandle::fillBrickTile)
    }

    private fun buildBrickWallLeft(cursor: Cursor) {
        buildWallLeft(cursor, WallsHandle::fillBrickTile)
    }

    private fun buildBrickWallTop(cursor: Cursor) {
        buildWallTop(cursor, WallsHandle::fillBrickTile)
    }

    private fun buildBrickWallFull(cursor: Cursor) {
        buildWallFull(cursor, WallsHandle::fillBrickTile)
    }

    private fun buildSteelWallRight(cursor: Cursor) {
        buildWallRight(cursor, WallsHandle::fillSteelTile)
    }

    private fun buildSteelWallBottom(cursor: Cursor) {
        buildWallBottom(cursor, WallsHandle::fillSteelTile)
    }

    private fun buildSteelWallLeft(cursor: Cursor) {
        buildWallLeft(cursor, WallsHandle::fillSteelTile)
    }

    private fun buildSteelWallTop(cursor: Cursor) {
        buildWallTop(cursor, WallsHandle::fillSteelTile)
    }

    private fun buildSteelWallFull(cursor: Cursor) {
        buildWallFull(cursor, WallsHandle::fillSteelTile)
    }

    private fun buildWater(cursor: Cursor) {
        gameField.ground.fillWaterTile(cursor.tileX, cursor.tileY)
        gameField.ground.fillWaterTile(cursor.tileX, cursor.tileY + 1)
        gameField.ground.fillWaterTile(cursor.tileX + 1, cursor.tileY)
        gameField.ground.fillWaterTile(cursor.tileX + 1, cursor.tileY + 1)
    }

    private fun buildTrees(cursor: Cursor) {
        gameField.trees.fillTile(cursor.tileX, cursor.tileY)
        gameField.trees.fillTile(cursor.tileX, cursor.tileY + 1)
        gameField.trees.fillTile(cursor.tileX + 1, cursor.tileY)
        gameField.trees.fillTile(cursor.tileX + 1, cursor.tileY + 1)
    }

    private fun buildIce(cursor: Cursor) {
        gameField.ground.fillIceTile(cursor.tileX, cursor.tileY)
        gameField.ground.fillIceTile(cursor.tileX, cursor.tileY + 1)
        gameField.ground.fillIceTile(cursor.tileX + 1, cursor.tileY)
        gameField.ground.fillIceTile(cursor.tileX + 1, cursor.tileY + 1)
    }

    private fun buildWallRight(cursor: Cursor, setter: WallsHandle.(Tile, Tile) -> Unit) {
        gameField.walls.clearTile(cursor.tileX, cursor.tileY)
        gameField.walls.clearTile(cursor.tileX, cursor.tileY + 1)
        gameField.walls.setter(cursor.tileX + 1, cursor.tileY)
        gameField.walls.setter(cursor.tileX + 1, cursor.tileY + 1)
    }

    private fun buildWallBottom(cursor: Cursor, setter: WallsHandle.(Tile, Tile) -> Unit) {
        gameField.walls.setter(cursor.tileX, cursor.tileY + 1)
        gameField.walls.setter(cursor.tileX + 1, cursor.tileY + 1)
        gameField.walls.clearTile(cursor.tileX, cursor.tileY)
        gameField.walls.clearTile(cursor.tileX + 1, cursor.tileY)
    }

    private fun buildWallLeft(cursor: Cursor, setter: WallsHandle.(Tile, Tile) -> Unit) {
        gameField.walls.setter(cursor.tileX, cursor.tileY)
        gameField.walls.setter(cursor.tileX, cursor.tileY + 1)
        gameField.walls.clearTile(cursor.tileX + 1, cursor.tileY)
        gameField.walls.clearTile(cursor.tileX + 1, cursor.tileY + 1)
    }

    private fun buildWallTop(cursor: Cursor, setter: WallsHandle.(Tile, Tile) -> Unit) {
        gameField.walls.setter(cursor.tileX, cursor.tileY)
        gameField.walls.setter(cursor.tileX + 1, cursor.tileY)
        gameField.walls.clearTile(cursor.tileX, cursor.tileY + 1)
        gameField.walls.clearTile(cursor.tileX + 1, cursor.tileY + 1)
    }

    private fun buildWallFull(cursor: Cursor, setter: WallsHandle.(Tile, Tile) -> Unit) {
        gameField.walls.setter(cursor.tileX, cursor.tileY)
        gameField.walls.setter(cursor.tileX + 1, cursor.tileY)
        gameField.walls.setter(cursor.tileX, cursor.tileY + 1)
        gameField.walls.setter(cursor.tileX + 1, cursor.tileY + 1)
    }

    private fun clear(cursor: Cursor) {
        gameField.walls.clearTile(cursor.tileX, cursor.tileY)
        gameField.walls.clearTile(cursor.tileX, cursor.tileY + 1)
        gameField.walls.clearTile(cursor.tileX + 1, cursor.tileY)
        gameField.walls.clearTile(cursor.tileX + 1, cursor.tileY + 1)
        gameField.trees.clearTile(cursor.tileX, cursor.tileY)
        gameField.trees.clearTile(cursor.tileX, cursor.tileY + 1)
        gameField.trees.clearTile(cursor.tileX + 1, cursor.tileY)
        gameField.trees.clearTile(cursor.tileX + 1, cursor.tileY + 1)
        gameField.ground.clearTile(cursor.tileX, cursor.tileY)
        gameField.ground.clearTile(cursor.tileX, cursor.tileY + 1)
        gameField.ground.clearTile(cursor.tileX + 1, cursor.tileY)
        gameField.ground.clearTile(cursor.tileX + 1, cursor.tileY + 1)
        if (!gameField.base.isHidden && gameField.base.bounds.intersects(cursor.bounds)) {
            gameField.base.isHidden = true
        }
    }

    private val Cursor.tileX get() = (x - gameField.bounds.x).toTile()
    private val Cursor.tileY get() = (y - gameField.bounds.y).toTile()

    override fun nextStructure() {
        structure = Structure.entries[(structure.ordinal + 1) % Structure.entries.size]
    }

    fun prevStructure() {
        structure = Structure.entries[if (structure.ordinal == 0) Structure.entries.size - 1 else structure.ordinal - 1]
    }
}