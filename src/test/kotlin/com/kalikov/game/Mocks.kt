package com.kalikov.game

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.Clock

fun mockGame(
    screen: Screen = mock(),
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    soundManager: SoundManager = mock(),
    config: GameConfig = GameConfig(),
    clock: Clock = mock(),
): Game {
    val game: Game = mock {
        on { this.screen } doReturn screen
        on { this.eventManager } doReturn eventManager
        on { this.imageManager } doReturn imageManager
        on { this.soundManager } doReturn soundManager
        on { this.config } doReturn config
        on { this.clock } doReturn clock
    }
    return game
}

fun stubEnemyTank(
    game: Game = mockGame(),
    pauseManager: PauseManager = mock(),
    x: Pixel = px(0),
    y: Pixel = px(0),
    enemyType: EnemyTank.EnemyType = EnemyTank.EnemyType.BASIC,
): EnemyTank {
    return EnemyTank.create(game, pauseManager, x, y, enemyType)
}

fun stubPlayerTank(
    game: Game = mockGame(),
    pauseManager: PauseManager = mock(),
    x: Pixel = px(0),
    y: Pixel = px(0),
    player: Player = Player(game),
): PlayerTank {
    return PlayerTank.create(game, pauseManager, x, y, player)
}

fun stubBullet(
    game: Game = mockGame(),
    tank: Tank,
    x: Pixel = px(0),
    y: Pixel = px(0),
): Bullet {
    return Bullet(game, tank, speed = tank.bulletSpeed, x = x, y = y)
}

fun stubTankExplosion(
    game: Game = mockGame(),
    tank: Tank
): TankExplosion {
    return TankExplosion(game, tank)
}

fun stubBaseExplosion(
    game: Game = mockGame()
): BaseExplosion {
    return BaseExplosion(game)
}

fun stubPoints(
    game: Game = mockGame(),
    value: Int = 100,
    x: Pixel = px(0),
    y: Pixel = px(0),
): Points {
    return Points(game, value, x, y, 200)
}

fun stubPowerUp(
    game: Game = mockGame(),
    position: PixelPoint = PixelPoint(),
): PowerUp {
    return PowerUp(game, position)
}

fun stubCursor(
    game: Game = mockGame(),
    builder: BuilderHandler = mock(),
): Cursor {
    return Cursor(game, builder)
}