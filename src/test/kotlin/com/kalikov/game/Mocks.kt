package com.kalikov.game

import com.kalikov.engine.EventManager
import com.kalikov.engine.ManagedMusic
import com.kalikov.engine.ManagedSound
import com.kalikov.engine.Pixel
import com.kalikov.engine.SceneManager
import com.kalikov.engine.Screen
import com.kalikov.engine.SoundManager
import com.kalikov.engine.px
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.Clock

fun mockSoundManager(): SoundManager {
    val sound = HashMap<String, ManagedSound>()
    val music = HashMap<String, ManagedMusic>()
    val soundManager: SoundManager = mock {
        on { this.sound(any()) } doAnswer {
            sound.computeIfAbsent(it.getArgument(0)) { mock() }
        }
        on { this.music(any()) } doAnswer {
            music.computeIfAbsent(it.getArgument(0)) { mock() }
        }
    }
    return soundManager
}


fun mockGame(
    screen: Screen = mock(),
    eventManager: EventManager = mock(),
    imageManager: ImageManager = mock(),
    soundManager: SoundManager = mockSoundManager(),
    sceneManager: SceneManager = mock(),
    stageManager: StageManager = mock(),
    config: GameConfig = GameConfig(),
    clock: Clock = mock(),
): BattleCityGame {
    val game: BattleCityGame = mock {
        on { this.screen } doReturn screen
        on { this.eventManager } doReturn eventManager
        on { this.imageManager } doReturn imageManager
        on { this.soundManager } doReturn soundManager
        on { this.sceneManager } doReturn sceneManager
        on { this.stageManager } doReturn stageManager
        on { this.config } doReturn config
        on { this.clock } doReturn clock
    }
    return game
}

fun stubEnemyTank(
    game: BattleCityGame = mockGame(),
    pauseManager: PauseManager = mock(),
    x: Pixel = 0.px,
    y: Pixel = 0.px,
    enemyType: EnemyTank.EnemyType = EnemyTank.EnemyType.BASIC,
): EnemyTank {
    return EnemyTank.create(game, pauseManager, x, y, enemyType)
}

fun stubPlayerTank(
    game: BattleCityGame = mockGame(),
    pauseManager: PauseManager = mock(),
    x: Pixel = 0.px,
    y: Pixel = 0.px,
    player: Player = Player(game),
): PlayerTank {
    return PlayerTank.create(game, pauseManager, x, y, player)
}

fun stubBullet(
    game: BattleCityGame = mockGame(),
    tank: Tank,
    x: Pixel = 0.px,
    y: Pixel = 0.px,
): Bullet {
    return Bullet(game, tank, speed = tank.bulletSpeed, x = x, y = y)
}

fun stubTankExplosion(
    game: BattleCityGame = mockGame(),
    pauseManager: PauseManager = mock(),
    tank: Tank
): TankExplosion {
    return TankExplosion(game, pauseManager, tank)
}

fun stubBaseExplosion(
    game: BattleCityGame = mockGame(),
    pauseManager: PauseManager = mock(),
): BaseExplosion {
    return BaseExplosion(game, pauseManager)
}

fun stubPoints(
    game: BattleCityGame = mockGame(),
    pauseManager: PauseManager = mock(),
    value: Int = 100,
    x: Pixel = 0.px,
    y: Pixel = 0.px,
): Points {
    return Points(game, pauseManager, value, x, y, 200)
}

fun stubPowerUp(
    game: BattleCityGame = mockGame(),
    position: PixelPoint = PixelPoint(),
): PowerUp {
    return PowerUp(game, position)
}

fun stubCursor(
    game: BattleCityGame = mockGame(),
    builder: BuilderHandler = mock(),
): Cursor {
    return Cursor(game, builder)
}