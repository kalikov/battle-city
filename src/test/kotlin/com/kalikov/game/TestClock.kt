package com.kalikov.game

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class TestClock : Clock() {
    private var millis: Long = System.currentTimeMillis()

    override fun millis(): Long {
        return millis
    }

    override fun instant(): Instant {
        throw UnsupportedOperationException()
    }

    override fun withZone(zone: ZoneId?): Clock {
        throw UnsupportedOperationException()
    }

    override fun getZone(): ZoneId {
        throw UnsupportedOperationException()
    }

    fun tick(millis: Long) {
        this.millis += millis
    }

    fun tick(millis: Int) {
        this.millis += millis
    }
}