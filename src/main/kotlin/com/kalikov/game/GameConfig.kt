package com.kalikov.game

import kotlinx.serialization.Serializable

@Serializable
data class GameConfig(
    val name: String = "Battle City",
    val resolution: PixelSize = PixelSize(px(800), px(600)),
    val fpsLimit: Int = 125,
    val debug: Boolean = false,
    val keyboard: Map<String, KeyEventConfig> = emptyMap(),
    val fonts: Map<String, FontConfig> = emptyMap(),
    val music: Map<String, String> = emptyMap(),
    val sounds: Map<String, String> = emptyMap(),
    val images: Map<String, String> = emptyMap(),
    val construction: String = "data/construction.json",
    val demo: StageConfig? = null,
    val stages: List<StageConfig> = emptyList()
)