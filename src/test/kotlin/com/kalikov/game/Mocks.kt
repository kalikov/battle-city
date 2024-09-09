package com.kalikov.game

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.Clock

fun mockGame(
    screen: Screen = mock(),
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    config: GameConfig = GameConfig(),
    clock: Clock = mock(),
): Game {
    val game: Game = mock {
        on { this.screen } doReturn screen
        on { this.eventManager } doReturn eventManager
        on { this.imageManager } doReturn imageManager
        on { this.config } doReturn config
        on { this.clock } doReturn clock
    }
    return game
}

fun mockEnemyTank(
    game: Game = mockGame(),
    pauseManager: PauseManager = mock(),
    x: Int = 0,
    y: Int = 0,
    enemyType: EnemyTank.EnemyType = EnemyTank.EnemyType.BASIC,
): EnemyTank {
    return EnemyTank.create(game, pauseManager, x, y, enemyType)
}

fun mockPlayerTank(
    game: Game = mockGame(),
    pauseManager: PauseManager = mock(),
    x: Int = 0,
    y: Int = 0,
    player: Player = Player(game.eventManager),
): PlayerTank {
    return PlayerTank.create(game, pauseManager, x, y, player)
}

fun mockBase(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    x: Int = 0,
    y: Int = 0
): Base {
    return Base(eventManager, imageManager, x, y)
}

fun mockWater(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    clock: Clock = mock(),
    x: Int = 0,
    y: Int = 0
): Water {
    return Water(eventManager, imageManager, clock, x, y)
}

fun mockTrees(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    x: Int = 0,
    y: Int = 0
): Trees {
    return Trees(eventManager, imageManager, x, y)
}

fun mockIce(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    x: Int = 0,
    y: Int = 0
): Ice {
    return Ice(eventManager, imageManager, x, y)
}

fun mockBrickWall(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    x: Int = 0,
    y: Int = 0
): BrickWall {
    return BrickWall(eventManager, imageManager, x, y)
}

fun mockSteelWall(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    x: Int = 0,
    y: Int = 0
): SteelWall {
    return SteelWall(eventManager, imageManager, x, y)
}

fun mockTankExplosion(
    game: Game = mockGame(),
    tank: Tank
): TankExplosion {
    return TankExplosion(game, tank)
}

fun mockBaseExplosion(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    clock: Clock = mock()
): BaseExplosion {
    return BaseExplosion(eventManager, imageManager, clock)
}

fun mockPoints(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    clock: Clock = mock(),
    value: Int = 100,
    x: Int = 0,
    y: Int = 0
): Points {
    return Points(eventManager, imageManager, clock, value, x, y, 200)
}

fun mockPowerUp(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    position: Point = Point(),
    clock: Clock = mock()
): PowerUp {
    return PowerUp(eventManager, imageManager, position, clock)
}

fun mockCursor(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    clock: Clock = mock(),
    builder: Builder = Builder(eventManager, imageManager, clock)
): Cursor {
    return Cursor(eventManager, imageManager, builder, clock)
}