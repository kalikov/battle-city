package com.kalikov.game

class StageScore {
    var tanksCount = 0
        private set

    private val tanks: Array<Int> = Array(EnemyTank.EnemyType.entries.size) { 0 }

    init {
        tanksCount = 0
    }

    fun increment(tank: EnemyTank) {
        if (tank.value > 0) {
            tanks[tank.enemyType.ordinal]++
            tanksCount++
        }
    }

    fun getTanks(type: EnemyTank.EnemyType): Int {
        return tanks[type.ordinal]
    }
}