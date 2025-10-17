package com.kalikov.game

import com.kalikov.engine.EventManager
import com.kalikov.engine.SceneManager
import com.kalikov.engine.Screen
import com.kalikov.engine.SoundManager
import java.time.Clock

interface BattleCityGame {
    val config: GameConfig

    val clock: Clock

    val screen: Screen

    val eventManager: EventManager
    val imageManager: ImageManager
    val soundManager: SoundManager
    val sceneManager: SceneManager
    val stageManager: StageManager
}