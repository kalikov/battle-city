package com.kalikov.engine

import com.kalikov.game.KeyEventConfig
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import java.awt.Component
import java.awt.event.KeyEvent
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
open class AwtInputBenchmark {
    private lateinit var component: Component
    private lateinit var input: AwtInput

    private lateinit var spacePressed: KeyEvent
    private lateinit var spaceReleased: KeyEvent

    @Setup(Level.Iteration)
    fun setup() {
        component = object : Component() {}
        input = AwtInput(component, mapOf(KeyEvent.VK_SPACE.toString() to KeyEventConfig(Keyboard.Key.START, 0)))
        spacePressed = KeyEvent(component, KeyEvent.KEY_PRESSED, 1704063600, 0, KeyEvent.VK_SPACE, ' ')
        spaceReleased = KeyEvent(component, KeyEvent.KEY_RELEASED, 1704063600, 0, KeyEvent.VK_SPACE, ' ')
    }

    @Benchmark
    fun benchmarkInput() {
        input.keyPressed(spacePressed)
        input.keyReleased(spaceReleased)
        input.pollEvent()
        input.pollEvent()
    }
}