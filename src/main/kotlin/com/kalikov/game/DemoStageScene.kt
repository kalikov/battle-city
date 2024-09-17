package com.kalikov.game

class DemoStageScene(
    private val game: Game,
    private val demoStage: Stage,
) : Scene {
    @Suppress("JoinDeclarationAndAssignment")
    private val spriteContainer: SpriteContainer
    private val overlayContainer: SpriteContainer

    private val gameField: GameField

    init {
        spriteContainer = DefaultSpriteContainer(game.eventManager)
        overlayContainer = DefaultSpriteContainer(game.eventManager)

        gameField = GameField(game, spriteContainer, overlayContainer)
        gameField.load(demoStage.map, 2)
    }

    override fun update() {
        gameField.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.rgb(0x808080))

        gameField.draw(surface)
    }

    override fun destroy() {
        spriteContainer.dispose()
    }
}