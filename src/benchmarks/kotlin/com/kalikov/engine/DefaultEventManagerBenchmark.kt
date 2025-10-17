package com.kalikov.engine

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
open class DefaultEventManagerBenchmark {
    data object EventA : Event()
    data object EventB : Event()

    private companion object {
        private val subscriptions = arrayOf(EventA::class, EventB::class)
    }

    private lateinit var manager: DefaultEventManager

    private val subscriber = NoOpSubscriber()

    @Setup(Level.Iteration)
    fun setup() {
        manager = DefaultEventManager()

        repeat(100) {
            manager.addSubscriber(NoOpSubscriber(), arrayOf(EventA::class))
        }
    }

    @Benchmark
    fun benchmarkFireEvent() {
        manager.fireEvent(EventA)
    }

    @Benchmark
    fun benchmarkSubscription() {
        manager.addSubscriber(subscriber, subscriptions)
        manager.removeSubscriber(subscriber, subscriptions)
    }

    private class NoOpSubscriber : EventSubscriber {
        override val identity: Int
            get() = TODO("Not yet implemented")

        override fun notify(event: Event) = Unit
    }
}