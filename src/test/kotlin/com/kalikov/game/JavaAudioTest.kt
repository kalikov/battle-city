package com.kalikov.game

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

class JavaAudioTest {
    @Test
    fun `should play sound once`() {
        val audio = JavaAudio()
        val sound = ClassLoader.getSystemResourceAsStream("silent.wav").use {
            requireNotNull(it)
            audio.load(it)
        }
        val start = System.currentTimeMillis()
        sound.play()
        assertTrue(System.currentTimeMillis() - start >= 500)
    }

    @Test
    fun `should play sound multiple times`() {
        val audio = JavaAudio()
        val sound = ClassLoader.getSystemResourceAsStream("silent.wav").use {
            requireNotNull(it)
            audio.load(it)
        }
        val start = System.currentTimeMillis()
        sound.play()
        sound.play()
        sound.play()
        assertTrue(System.currentTimeMillis() - start >= 1500)
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    fun `should loop and stop sound`() {
        val audio = JavaAudio()
        val sound = ClassLoader.getSystemResourceAsStream("silent.wav").use {
            requireNotNull(it)
            audio.load(it)
        }
        val thread = Thread {
            sound.loop()
        }
        thread.start()
        Thread.sleep(500)

        assertTrue(sound.state == Sound.State.PLAYING)
        sound.stop()

        thread.join()
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    fun `should pause and resume sound`() {
        val audio = JavaAudio()
        val sound = ClassLoader.getSystemResourceAsStream("silent.wav").use {
            requireNotNull(it)
            audio.load(it)
        }
        val thread = Thread {
            sound.play()
        }
        thread.start()
        while (sound.state != Sound.State.PLAYING) {
            // do nothing
        }
        sound.pause()

        Thread.sleep(1000)

        assertTrue(sound.state == Sound.State.PAUSED)

        sound.resume()

        thread.join()
    }
}