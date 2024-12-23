package com.kalikov.game

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class LoadingScene(
    private val game: Game,
    private val imageManager: LoadingImageManager,
    private val soundManager: LoadingSoundManager,
    private val fontManager: FontManager,
    private val stageManager: StageManager,
) : Scene {
    private val remainingJobs = AtomicInteger()
    private val failure = AtomicBoolean()

    private val executor = Executors.newSingleThreadExecutor()

    private val stages = ConcurrentHashMap<Int, Stage>()
    private var constructionMap: StageMapConfig? = null
    private var demoStage: Stage? = null

    init {
        LeaksDetector.add(this)

        loadMusic()
        loadSounds()

        loadImages()

        loadFonts()

        loadStages()
    }

    override fun update() {
        if (failure.get()) {
            executor.shutdown()

            game.eventManager.fireEvent(BasicGame.Quit)
        } else if (remainingJobs.get() == 0) {
            executor.shutdown()

            stageManager.init(getStages(), requireNotNull(constructionMap), demoStage)
            game.eventManager.fireEvent(Scene.Start {
                MainMenuScene(game, stageManager)
            })
        }
    }

    private fun getStages(): List<Stage> {
        val result = ArrayList<Stage>(stages.size)
        for (i in 0 until stages.size) {
            result.add(requireNotNull(stages[i]))
        }
        return result
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)
    }

    override fun destroy() {
        executor.shutdownNow()

        LeaksDetector.remove(this)
    }

    private fun loadMusic() {
        remainingJobs.incrementAndGet()
        executor.submit {
            game.config.music.forEach { (name, path) ->
                loadMusic(name, path)
            }
            remainingJobs.decrementAndGet()
        }
    }

    private fun loadMusic(name: String, path: String) {
        remainingJobs.incrementAndGet()
        executor.submit {
            try {
                soundManager.loadMusic(name, path)
            } catch (e: Throwable) {
                e.printStackTrace(System.err)
                failure.set(true)
            }
            remainingJobs.decrementAndGet()
        }
    }

    private fun loadSounds() {
        remainingJobs.incrementAndGet()
        executor.submit {
            game.config.sounds.forEach { (name, path) ->
                loadSound(name, path)
            }
            remainingJobs.decrementAndGet()
        }
    }

    private fun loadSound(name: String, path: String) {
        remainingJobs.incrementAndGet()
        executor.submit {
            try {
                soundManager.loadSound(name, path)
            } catch (e: Throwable) {
                e.printStackTrace(System.err)
                failure.set(true)
            }
            remainingJobs.decrementAndGet()
        }
    }

    private fun loadStages() {
        remainingJobs.incrementAndGet()
        executor.submit {
            for ((index, stageConfig) in game.config.stages.withIndex()) {
                loadStage(index, stageConfig)
            }
            loadConstructionMap(game.config.construction)
            game.config.demo?.let {
                loadDemoStage(it)
            }
            remainingJobs.decrementAndGet()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadStage(index: Int, stageConfig: StageConfig) {
        remainingJobs.incrementAndGet()
        executor.submit {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val map: StageMapConfig = FileInputStream(File(stageConfig.map)).use {
                json.decodeFromStream(it)
            }
            stages[index] = Stage(map, stageConfig.enemySpawnDelay ?: 3000, stageConfig.enemies)
            remainingJobs.decrementAndGet()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadConstructionMap(path: String) {
        remainingJobs.incrementAndGet()
        executor.submit {
            val json = Json {
                ignoreUnknownKeys = true
            }
            constructionMap = FileInputStream(File(path)).use {
                json.decodeFromStream(it)
            }
            remainingJobs.decrementAndGet()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadDemoStage(stageConfig: StageConfig) {
        remainingJobs.incrementAndGet()
        executor.submit {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val map: StageMapConfig = FileInputStream(File(stageConfig.map)).use {
                json.decodeFromStream(it)
            }
            demoStage = Stage(map, stageConfig.enemySpawnDelay ?: 3000, stageConfig.enemies)
            remainingJobs.decrementAndGet()
        }
    }

    private fun loadImages() {
        remainingJobs.incrementAndGet()
        executor.submit {
            game.config.images.forEach { (name, path) ->
                loadImage(name, path)
            }
            remainingJobs.decrementAndGet()
        }
    }

    private fun loadImage(name: String, path: String) {
        remainingJobs.incrementAndGet()
        executor.submit {
            try {
                imageManager.load(name, path)
            } catch (e: Throwable) {
                e.printStackTrace(System.err)
                failure.set(true)
            }
            remainingJobs.decrementAndGet()
        }
    }

    private fun loadFonts() {
        remainingJobs.incrementAndGet()
        executor.submit {
            game.config.fonts.forEach { (name, config) ->
                loadFont(name, config)
            }
            remainingJobs.decrementAndGet()
        }
    }

    private fun loadFont(name: String, config: FontConfig) {
        remainingJobs.incrementAndGet()
        executor.submit {
            try {
                fontManager.load(name, config.path, config.size)
            } catch (e: Throwable) {
                e.printStackTrace(System.err)
                failure.set(true)
            }
            remainingJobs.decrementAndGet()
        }
    }
}