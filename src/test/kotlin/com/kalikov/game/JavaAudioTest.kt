package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JavaAudioTest {
    private lateinit var sound: ManagedSound
    private lateinit var music: ManagedMusic

    @BeforeEach
    fun beforeEach() {
        val audio = JavaAudio()
        sound = ClassLoader.getSystemResourceAsStream("silent.wav").use {
            requireNotNull(it)
            audio.loadSound(it)
        }
        music = ClassLoader.getSystemResourceAsStream("silent.wav").use {
            requireNotNull(it)
            audio.loadMusic(it)
        }
    }

    @Test
    fun `should play sound once`() {
        sound.play()
        assertEquals(1, sound.playingCount)

        Thread.sleep(1000)

        assertEquals(0, sound.playingCount)
    }

    @Test
    fun `should play music once`() {
        music.play()
        assertEquals(Music.State.PLAYING, music.state)

        Thread.sleep(1000)

        assertEquals(Music.State.STOPPED, music.state)
    }

    @Test
    fun `should play sound only once on multiple invocations`() {
        sound.play()
        sound.play()
        sound.play()
        assertEquals(3, sound.playingCount)

        Thread.sleep(1000)

        assertEquals(0, sound.playingCount)
    }

    @Test
    fun `should loop and stop music`() {
        music.loop()
        assertEquals(Music.State.PLAYING, music.state)

        Thread.sleep(1000)
        assertEquals(Music.State.PLAYING, music.state)

        music.stop()
        assertEquals(Music.State.STOPPED, music.state)
    }

    @Test
    fun `should pause and resume sound`() {
        sound.play()
        sound.pause()
        assertEquals(1, sound.playingCount)

        Thread.sleep(1000)
        assertEquals(1, sound.playingCount)

        sound.resume()
        assertEquals(1, sound.playingCount)

        Thread.sleep(1000)
        assertEquals(0, sound.playingCount)
    }

    @Test
    fun `should pause and resume music`() {
        music.play()
        music.pause()
        assertEquals(Music.State.PAUSED, music.state)

        Thread.sleep(1000)
        assertEquals(Music.State.PAUSED, music.state)

        music.resume()
        assertEquals(Music.State.PLAYING, music.state)

        Thread.sleep(1000)
        assertEquals(Music.State.STOPPED, music.state)
    }
}