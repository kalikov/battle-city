package com.kalikov.game

import com.kalikov.engine.SoundManager

inline val SoundManager.stageStart get() = this.music("stage_start")
inline val SoundManager.playerMovement get() = this.music("movement_player")
inline val SoundManager.enemyMovement get() = this.music("movement_enemy")

inline val SoundManager.pause get() = this.sound("pause")
inline val SoundManager.bulletShot get() = this.sound("bullet_shot")
inline val SoundManager.playerExplosion get() = this.sound("explosion_player")
inline val SoundManager.enemyExplosion get() = this.sound("explosion_enemy")
inline val SoundManager.bulletHitSteel get() = this.sound("bullet_hit_steel")
inline val SoundManager.bulletHitBrick get() = this.sound("bullet_hit_brick")
inline val SoundManager.bulletHitEnemy get() = this.sound("bullet_hit_enemy")
inline val SoundManager.gameOver get() = this.sound("game_over")
inline val SoundManager.highScore get() = this.sound("high_score")
inline val SoundManager.incrementLife get() = this.sound("increment_life")
inline val SoundManager.powerUpPick get() = this.sound("powerup_pick")
inline val SoundManager.powerUpAppear get() = this.sound("powerup_appear")
inline val SoundManager.statistics get() = this.sound("statistics")
inline val SoundManager.bonus get() = this.sound("bonus")
inline val SoundManager.slip get() = this.sound("slip")