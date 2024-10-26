package com.kalikov.game

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ConcurrentSoundManager(
    private val audio: Audio,
    private val eventManager: EventManager
) : LoadingSoundManager, EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            SoundManager.Play::class,
            SoundManager.Loop::class,
            SoundManager.Stop::class,
            SoundManager.Pause::class,
            SoundManager.Resume::class
        )
    }

    private val sounds: MutableMap<String, Sound> = ConcurrentHashMap()

    private val executor: ExecutorService = Executors.newFixedThreadPool(8)

    private val playbacks: MutableMap<String, Future<*>> = ConcurrentHashMap()

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override var enabled: Boolean = true
        set(value) {
            field = value
            stopAll()
        }

    override fun load(name: String, path: String) {
        val sound = audio.load(path)
        sounds[name] = sound
    }

    override fun isPlaying(name: String): Boolean {
        return playbacks[name]?.let {
            !it.isDone
        } ?: false
    }

    override fun notify(event: Event) {
        when (event) {
            is SoundManager.Play -> play(event.name, Sound::play)
            is SoundManager.Loop -> play(event.name, Sound::loop)
            is SoundManager.Stop -> stop(event.name)
            is SoundManager.Pause -> pause()
            is SoundManager.Resume -> resume()
            else -> {}
        }
    }

    private fun play(name: String, playback: (Sound) -> Unit) {
        val sound = sounds[name] ?: throw SoundNotFoundException(name)

        if (!enabled) {
            return
        }

        playbacks.compute(name) { _, value ->
            value?.cancel(true)
            executor.submit {
                playback(sound)
            }
        }
    }

    private fun stop(name: String) {
        playbacks.compute(name) { _, value ->
            value?.cancel(true)
            null
        }
        sounds[name]?.let {
            executor.submit {
                it.stop()
            }
        }
    }

    private fun pause() {
        for (key in playbacks.keys) {
            sounds[key]?.pause()
        }
    }

    private fun resume() {
        for (key in playbacks.keys) {
            sounds[key]?.resume()
        }
    }

    private fun stopAll() {
        playbacks.values.forEach {
            it.cancel(true)
        }
        playbacks.clear()

        sounds.values.forEach { it.stop() }
    }

    fun destroy() {
        eventManager.removeSubscriber(this, subscriptions)

        executor.shutdown()

        stopAll()
        sounds.clear()

        executor.awaitTermination(1, TimeUnit.MINUTES)
        executor.shutdownNow()
    }
}