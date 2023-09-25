package com.kalikov.game

import java.time.Clock

class ConstructionScene(
    private val screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val clock: Clock
) : Scene, EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            Keyboard.KeyPressed::class,
            Builder.StructureCreated::class,
        )
    }

    val spriteContainer: SpriteContainer

    private val gameField: GameField

    val cursor: Cursor
    private val cursorController: CursorController

    private val basePosition: Point
    private val playerPosition: Point
    private val enemyPositions: List<Point>

    init {
        spriteContainer = ConcurrentSpriteContainer(eventManager)
        gameField = GameField(eventManager, imageManager, entityFactory, spriteContainer)

        eventManager.addSubscriber(this, subscriptions)

        cursor = Cursor(eventManager, imageManager, Builder(eventManager, imageManager, clock), clock)
        cursor.setPosition(Point(gameField.bounds.x, gameField.bounds.y))

        cursorController = CursorController(eventManager, cursor, gameField.bounds, clock)

        spriteContainer.addSprite(cursor)

        val map = stageManager.constructionMap
        basePosition = map.base
        playerPosition = map.playerSpawnPoint
        enemyPositions = map.enemySpawnPoints
        val base = Base(
            eventManager,
            imageManager,
            gameField.bounds.x + map.base.x * Globals.TILE_SIZE,
            gameField.bounds.y + map.base.y * Globals.TILE_SIZE
        )
        spriteContainer.addSprite(base)
        for (item in map.objects) {
            val sprite = entityFactory.create(
                item.type,
                gameField.bounds.x + item.x * Globals.TILE_SIZE,
                gameField.bounds.y + item.y * Globals.TILE_SIZE,
            )
            sprite.static = true
            spriteContainer.addSprite(sprite)
            if (sprite.bounds.intersects(base.bounds)) {
                base.destroy()
            }
        }
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed) {
            keyPressed(event.key)
        } else if (event is Builder.StructureCreated) {
            destroySpritesUnderCursor(event.cursor)
            addStructure(event.sprites)
        }
    }

    private fun destroySpritesUnderCursor(cursor: Cursor) {
        spriteContainer.sprites.forEach {
            if (it != cursor && it.bounds.intersects(cursor.bounds)) {
                it.destroy()
            }
        }
    }

    private fun addStructure(sprites: List<Sprite>) {
        sprites.forEach {
            spriteContainer.addSprite(it)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (key == Keyboard.Key.START) {
            cursor.destroy()
            val surface = screen.createSurface(screen.surface.width, screen.surface.height)
            draw(surface)

            stageManager.constructionMap = getConstructionMap()
            stageManager.curtainBackground = surface
            eventManager.fireEvent(Scene.Start {
                val mainMenu = MainMenuScene(screen, eventManager, imageManager, stageManager, entityFactory, clock)
                mainMenu.setMenuItem(2)
                mainMenu.arrived()
                mainMenu
            })
        }
    }

    private fun getConstructionMap(): StageMapConfig {
        return StageMapConfig(
            spriteContainer.sprites.filterIsInstance(Entity::class.java).map { it.toStageObject() },
            basePosition,
            playerPosition,
            enemyPositions
        )
    }

    override fun update() {
        gameField.update()
        cursorController.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.rgb(0x808080))

        gameField.draw(surface)
    }

    override fun destroy() {
        cursorController.dispose()

        eventManager.removeSubscriber(this, subscriptions)

        gameField.dispose()
        spriteContainer.dispose()
    }
}