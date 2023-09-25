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
    private val sounds: MutableMap<String, Sound> = ConcurrentHashMap()

    private val executor: ExecutorService = Executors.newFixedThreadPool(8)

    private val playbacks: MutableMap<String, Future<*>> = ConcurrentHashMap()

    init {
        eventManager.addSubscriber(this, setOf(SoundManager.Play::class, SoundManager.Stop::class))
    }

    override fun load(name: String, path: String) {
        val sound = audio.load(path)
        sounds[name] = sound
    }

    override fun notify(event: Event) {
        if (event is SoundManager.Play) {
            play(event.name)
        } else if (event is SoundManager.Stop) {
            stop(event.name)
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

    override fun destroy() {
        eventManager.removeSubscriber(this, setOf(SoundManager.Play::class, SoundManager.Stop::class))

        executor.shutdown()

        playbacks.values.forEach {
            it.cancel(true)
        }

        executor.awaitTermination(1, TimeUnit.MINUTES)
        executor.shutdownNow()
    }
}