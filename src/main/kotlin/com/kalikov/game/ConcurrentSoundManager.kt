package com.kalikov.game

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class ConcurrentSoundManager(
    private val audio: Audio,
    private val eventManager: EventManager
) : SoundManager, EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            SoundManager.Play::class,
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

    override fun load(name: String, path: String) {
        val sound = audio.load(path)
        sounds[name] = sound
    }

    override fun notify(event: Event) {
        when (event) {
            is SoundManager.Play -> play(event.name)
            is SoundManager.Stop -> stop(event.name)
            is SoundManager.Pause -> pause()
            is SoundManager.Resume -> resume()
            else -> {}
        }
    }

    private fun play(name: String) {
        val sound = sounds[name] ?: throw SoundNotFoundException(name)

        playbacks.compute(name) { _, value ->
            value?.cancel(true)
            executor.submit {
                sound.play()
            }
        }
    }

    private fun stop(name: String) {
        playbacks.compute(name) { _, value ->
            value?.cancel(true)
            null
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

    override fun destroy() {
        eventManager.removeSubscriber(this, subscriptions)

        executor.shutdown()

        playbacks.values.forEach {
            it.cancel(true)
        }
        playbacks.clear()

        sounds.values.forEach { it.stop() }
        sounds.clear()

        executor.awaitTermination(1, TimeUnit.MINUTES)
        executor.shutdownNow()
    }
}