package com.kalikov.game

import java.time.Clock

data class AITankControllerParams(
    val clock: Clock = Clock.systemUTC(),

    val shootInterval: Int = 480,
    val shootProbability: Double = 0.7,

    val directionUpdateInterval: Int = 640,
    val directionUpdateProbability: Double = 0.6,
    val directionRetreatProbability: Double = 0.4
)