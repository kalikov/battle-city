package com.kalikov.game

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class JavaAudioTest {
    private lateinit var sound: Sound

    @BeforeEach
    fun beforeEach() {
         val audio = JavaAudio()
         sound = ClassLoader.getSystemResourceAsStream("silent.wav").use {
            requireNotNull(it)
            audio.load(it)
        }
    }

    @Test
    fun `should play sound once`() {
        val start = System.currentTimeMillis()
        sound.play()
        assertTrue(System.currentTimeMillis() - start >= 500)
    }

    @Test
    fun `should play sound multiple times`() {
        val start = System.currentTimeMillis()
        sound.play()
        sound.play()
        sound.play()
        assertTrue(System.currentTimeMillis() - start >= 1500)
    }

    @Test
    @Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
    fun `should loop and stop sound`() {
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
        val thread = Thread {
            sound.play()
        }
        thread.priority = Thread.MIN_PRIORITY
        thread.start()
        while (sound.state != Sound.State.PLAYING) {
            // do nothing
        }
        sound.pause()

        Thread.sleep(1000)

        assertEquals(Sound.State.PAUSED, sound.state)

        sound.resume()

        thread.join()
    }
}