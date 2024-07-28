package com.kalikov.game

@kotlinx.serialization.Serializable
data class GameConfig(
    val name: String = "Battle City",
    val resolution: Size = Size(800, 600),
    val fpsLimit: Int = 125,
    val debug: Boolean = false,
    val keyboard: Map<String, KeyEventConfig> = emptyMap(),
    val fonts: Map<String, FontConfig> = emptyMap(),
    val sounds: Map<String, String> = emptyMap(),
    val images: Map<String, String> = emptyMap(),
    val construction: String = "data/construction.json",
    val stages: List<StageConfig> = emptyList()
)