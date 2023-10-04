package com.kalikov.game

class Ice(
    eventRouter: EventRouter,
    imageManager: ImageManager,
    x: Int,
    y: Int
) : Sprite(eventRouter, x, y, Globals.UNIT_SIZE, Globals.UNIT_SIZE), Entity {
    companion object {
        const val CLASS_NAME = "Ice"
    }

    private val image = imageManager.getImage("ice")

    init {
        LeaksDetector.add(this)

        z = -1
    }

    override fun toStageObject(stageX: Int, stageY: Int): StageObject {
        return StageObject(CLASS_NAME, (x - stageX) / Globals.TILE_SIZE, (y - stageY) / Globals.TILE_SIZE)
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(x, y, image)
        surface.draw(x + Globals.TILE_SIZE, y, image)
        surface.draw(x, y + Globals.TILE_SIZE, image)
        surface.draw(x + Globals.TILE_SIZE, y + Globals.TILE_SIZE, image)
    }

    override fun dispose() {
        LeaksDetector.remove(this)
    }
}