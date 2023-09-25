package com.kalikov.game

import java.time.Clock

class Water(
    eventManager: EventManager,
    imageManager: ImageManager,
    clock: Clock,
    x: Int,
    y: Int
) : Sprite(eventManager, x, y, Globals.UNIT_SIZE, Globals.UNIT_SIZE), Entity {
    companion object {
        const val CLASS_NAME = "Water"
    }

    private val image = imageManager.getImage("water")
    private val tileWidth = width / 2
    private val tileHeight = height / 2

    init {
        LeaksDetector.add(this)

        z = -1
    }

    private val animation = Animation.pauseAware(eventManager, frameLoopOf(1, 2), clock, 1000)

    override fun toStageObject(): StageObject {
        return StageObject(CLASS_NAME, x / Globals.TILE_SIZE, y / Globals.TILE_SIZE)
    }

    override fun updateHook() {
        if (!animation.isRunning) {
            animation.restart()
        }
        animation.update()
    }

    override fun dispose() {
        LeaksDetector.remove(this)
    }

    override fun draw(surface: ScreenSurface) {
        val index = if (static) 0 else animation.frame
        val srcX = tileWidth * index
        surface.draw(x, y, image, srcX, 0, tileWidth, tileHeight)
        surface.draw(x + tileWidth, y, image, srcX, 0, tileWidth, tileHeight)
        surface.draw(x, y + tileHeight, image, srcX, 0, tileWidth, tileHeight)
        surface.draw(x + tileWidth, y + tileHeight, image, srcX, 0, tileWidth, tileHeight)
    }
}