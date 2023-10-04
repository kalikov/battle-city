package com.kalikov.game

class SteelWall(
    private val eventRouter: EventRouter,
    imageManager: ImageManager,
    x: Int,
    y: Int
) : Wall(eventRouter, x, y), Entity {
    companion object {
        const val CLASS_NAME = "SteelWall"
    }

    override val hitRect get() = bounds

    private val image = imageManager.getImage("wall_steel")

    override fun toStageObject(stageX: Int, stageY: Int): StageObject {
        return StageObject(CLASS_NAME, (x - stageX) / Globals.TILE_SIZE, (y - stageY) / Globals.TILE_SIZE)
    }

    override fun hit(bullet: Bullet) {
        if (bullet.tank.isPlayer) {
            eventRouter.fireEvent(SoundManager.Play("bullet_hit_1"))
        }
        if (bullet.type == Bullet.Type.ENHANCED) {
            destroy()
        }
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(x, y, image)
    }

    override fun boundsHook() {
    }
}