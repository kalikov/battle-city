package com.kalikov.game

import org.mockito.kotlin.mock
import java.time.Clock

fun mockEnemyTank(
    eventManager: EventManager = mock(),
    pauseManager: PauseManager = mock(),
    imageManager: ImageManager = mock(),
    clock: Clock = mock(),
    x: Int = 0,
    y: Int = 0,
    enemyType: EnemyTank.EnemyType = EnemyTank.EnemyType.BASIC,
): EnemyTank {
    return EnemyTank.create(eventManager, pauseManager, imageManager, clock, x, y, enemyType)
}

fun mockPlayerTank(
    eventManager: EventManager = mock(),
    pauseManager: PauseManager = mock(),
    imageManager: ImageManager = mock(),
    clock: Clock = mock(),
    x: Int = 0,
    y: Int = 0,
    player: Player = Player(eventManager),
): PlayerTank {
    return PlayerTank.create(eventManager, pauseManager, imageManager, clock, x, y, player)
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
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    tank: Tank
): TankExplosion {
    return TankExplosion(eventManager, imageManager, tank)
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