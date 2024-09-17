package com.kalikov.game

class Cursor(
    game: Game,
    private val builder: BuilderHandler,
    x: Pixel = px(0),
    y: Pixel = px(0)
) : Sprite(game.eventManager, x, y, Tank.SIZE, Tank.SIZE) {

    private val blinkTimer = BlinkTimer(game.clock, 320)

    private val image = game.imageManager.getImage("tank_player1")

    init {
        z = 10000
    }

    override fun draw(surface: ScreenSurface) {
        if (blinkTimer.isOpaque) {
            surface.draw(x, y, image, Direction.UP.index * width, px(0), width, height)
        }
    }

    override fun updateHook() {
        if (blinkTimer.isStopped) {
            blinkTimer.restart()
        }
        blinkTimer.update()
    }

    override fun dispose() {
    }

    fun build() {
        builder.build(this)
    }

    fun buildNext() {
        builder.nextStructure()
        builder.build(this)
    }
}