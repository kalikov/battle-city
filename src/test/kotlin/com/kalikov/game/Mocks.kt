package com.kalikov.game

import org.mockito.kotlin.mock
import java.time.Clock

fun mockTank(
    eventManager: EventManager = mock(),
    pauseManager: PauseManager = mock(),
    imageManager: ImageManager = mock(),
    clock: Clock = mock(),
    x: Int = 0,
    y: Int = 0
): Tank {
    return Tank(eventManager, pauseManager, imageManager, clock, x, y)
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

fun mockPoints(
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    clock: Clock = mock(),
    x: Int = 0,
    y: Int = 0
): Points {
    return Points(eventManager, imageManager, clock, x, y)
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