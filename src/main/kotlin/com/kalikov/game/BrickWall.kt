package com.kalikov.game

class BrickWall(
    private val eventRouter: EventRouter,
    imageManager: ImageManager,
    x: Int,
    y: Int
) : Wall(eventRouter, x, y), Entity {
    companion object {
        const val CLASS_NAME = "BrickWall"
    }

    var isHitTop = false
        private set
    var isHitLeft = false
        private set
    var isHitRight = false
        private set
    var isHitBottom = false
        private set

    val isHit get() = isHitLeft || isHitRight || isHitTop || isHitBottom

    override var hitRect = bounds
        private set

    private val image = imageManager.getImage("wall_brick")

    override fun boundsHook() {
        updateHitRect()
    }

    private fun updateHitRect() {
        if (!isHit) {
            hitRect = bounds
            return
        }
        var left = x
        var top = y
        var right = x + width
        var bottom = y + height
        if (isHitTop) {
            top += height / 2
        }
        if (isHitLeft) {
            left += width / 2
        }
        if (isHitRight) {
            right -= width / 2
        }
        if (isHitBottom) {
            bottom -= width / 2
        }
        hitRect = Rect(left, top, right - left, bottom - top)
    }

    override fun hit(bullet: Bullet) {
        if (bullet.tank.isPlayer) {
            eventRouter.fireEvent(SoundManager.Play("bullet_hit_2"))
        }
        if (bullet.type == Bullet.Type.ENHANCED) {
            destroy()
            return
        }

        when (bullet.direction) {
            Direction.RIGHT -> hitLeft()
            Direction.LEFT -> hitRight()
            Direction.DOWN -> hitTop()
            Direction.UP -> hitBottom()
        }
    }

    private fun hitLeft() {
        if (isHitLeft || isHitRight) {
            destroy()
            return
        }
        isHitLeft = true
        updateHitRect()
    }

    private fun hitRight() {
        if (isHitRight || isHitLeft) {
            destroy()
            return
        }
        isHitRight = true
        updateHitRect()
    }

    private fun hitTop() {
        if (isHitTop || isHitBottom) {
            destroy()
            return
        }
        isHitTop = true
        updateHitRect()
    }

    private fun hitBottom() {
        if (isHitBottom || isHitTop) {
            destroy()
            return
        }
        isHitBottom = true
        updateHitRect()
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(x, y, image) { dst, src, x, y ->
            val dx = x - this@BrickWall.x
            val dy = y - this@BrickWall.y
            if (isHitTop && dy < height / 2) {
                dst
            } else if (isHitBottom && dy >= height / 2) {
                dst
            } else if (isHitLeft && dx < width / 2) {
                dst
            } else if (isHitRight && dx >= width / 2) {
                dst
            } else {
                src.over(dst)
            }
        }
    }

    override fun toStageObject(): StageObject {
        return StageObject(CLASS_NAME, x / Globals.TILE_SIZE, y / Globals.TILE_SIZE)
    }
}