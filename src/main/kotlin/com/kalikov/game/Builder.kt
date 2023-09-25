package com.kalikov.game

import java.time.Clock

class Builder(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val clock: Clock
) {
    data class StructureCreated(val sprites: List<Sprite>, val cursor: Cursor) : Event()

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
        private set

    private val brickWallFactory = BrickWallFactory(eventManager, imageManager)
    private val steelWallFactory = SteelWallFactory(eventManager, imageManager)

    fun build(cursor: Cursor) {
        val structure = when (structure) {
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
            Structure.CLEAR -> emptyList()
        }
        eventManager.fireEvent(StructureCreated(structure, cursor))
    }


    private fun buildBrickWallRight(cursor: Cursor): List<Sprite> {
        return buildWallRight(cursor, brickWallFactory)
    }

    private fun buildBrickWallBottom(cursor: Cursor): List<Sprite> {
        return buildWallBottom(cursor, brickWallFactory)
    }

    private fun buildBrickWallLeft(cursor: Cursor): List<Sprite> {
        return buildWallLeft(cursor, brickWallFactory)
    }

    private fun buildBrickWallTop(cursor: Cursor): List<Sprite> {
        return buildWallTop(cursor, brickWallFactory)
    }

    private fun buildBrickWallFull(cursor: Cursor): List<Sprite> {
        return buildWallFull(cursor, brickWallFactory)
    }

    private fun buildSteelWallRight(cursor: Cursor): List<Sprite> {
        return buildWallRight(cursor, steelWallFactory)
    }

    private fun buildSteelWallBottom(cursor: Cursor): List<Sprite> {
        return buildWallBottom(cursor, steelWallFactory)
    }

    private fun buildSteelWallLeft(cursor: Cursor): List<Sprite> {
        return buildWallLeft(cursor, steelWallFactory)
    }

    private fun buildSteelWallTop(cursor: Cursor): List<Sprite> {
        return buildWallTop(cursor, steelWallFactory)
    }

    private fun buildSteelWallFull(cursor: Cursor): List<Sprite> {
        return buildWallFull(cursor, steelWallFactory)
    }

    private fun buildWater(cursor: Cursor): List<Sprite> {
        val water = Water(eventManager, imageManager, clock, cursor.x, cursor.y)
        water.static = true
        return listOf(water)
    }

    private fun buildTrees(cursor: Cursor): List<Sprite> {
        val trees = Trees(eventManager, imageManager, cursor.x, cursor.y)
        return listOf(trees)
    }

    private fun buildIce(cursor: Cursor): List<Sprite> {
        val ice = Ice(eventManager, imageManager, cursor.x, cursor.y)
        return listOf(ice)
    }

    private fun buildWallRight(cursor: Cursor, factory: WallFactory): List<Sprite> {
        val wallTop = factory.create(cursor.x + Globals.TILE_SIZE, cursor.y)

        val wallBottom = factory.create(cursor.x + Globals.TILE_SIZE, cursor.y + Globals.TILE_SIZE)

        return listOf(wallTop, wallBottom)
    }

    private fun buildWallBottom(cursor: Cursor, factory: WallFactory): List<Sprite> {
        val wallLeft = factory.create(cursor.x, cursor.y + Globals.TILE_SIZE)

        val wallRight = factory.create(cursor.x + Globals.TILE_SIZE, cursor.y + Globals.TILE_SIZE)

        return listOf(wallLeft, wallRight)
    }

    private fun buildWallLeft(cursor: Cursor, factory: WallFactory): List<Sprite> {
        val wallTop = factory.create(cursor.x, cursor.y)

        val wallBottom = factory.create(cursor.x, cursor.y + Globals.TILE_SIZE)

        return listOf(wallTop, wallBottom)
    }

    private fun buildWallTop(cursor: Cursor, factory: WallFactory): List<Sprite> {
        val wallLeft = factory.create(cursor.x, cursor.y)

        val wallRight = factory.create(cursor.x + Globals.TILE_SIZE, cursor.y)

        return listOf(wallLeft, wallRight)
    }

    private fun buildWallFull(cursor: Cursor, factory: WallFactory): List<Sprite> {
        val wallTopLeft = factory.create(cursor.x, cursor.y)

        val wallTopRight = factory.create(cursor.x + Globals.TILE_SIZE, cursor.y)

        val wallBottomLeft = factory.create(cursor.x, cursor.y + Globals.TILE_SIZE)

        val wallBottomRight = factory.create(cursor.x + Globals.TILE_SIZE, cursor.y + Globals.TILE_SIZE)

        return listOf(wallTopLeft, wallTopRight, wallBottomLeft, wallBottomRight)
    }

    fun nextStructure() {
        structure = Structure.entries[(structure.ordinal + 1) % Structure.entries.size]
    }

    fun prevStructure() {
        structure = Structure.entries[if (structure.ordinal == 0) Structure.entries.size - 1 else structure.ordinal - 1]
    }
}