package com.kalikov.game

class StageScore {
    var tanksCount = 0
        private set

    private val tanks: Array<Int> = Array(Tank.EnemyType.entries.size) { 0 }

    init {
        tanksCount = 0
    }

    fun increment(tank: Tank) {
        tank.enemyType?.let {
            if (tank.value > 0) {
                tanks[it.ordinal]++
                tanksCount++
            }
        }
    }

    fun getTanks(type: Tank.EnemyType): Int {
        return tanks[type.ordinal]
    }
}