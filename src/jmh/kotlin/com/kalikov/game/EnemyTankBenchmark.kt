package com.kalikov.game

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
open class EnemyTankBenchmark {
    private lateinit var tank: EnemyTank

    @Setup(Level.Invocation)
    fun setup() {
        tank = stubEnemyTank()
    }

    @Benchmark
    fun benchmarkDestroy() {
        tank.destroy()
        tank.update()
    }
}