package com.kalikov.game

import java.time.Clock

class CursorController(
    private val eventManager: EventManager,
    private val cursor: Cursor,
    private val level: Rect,
    clock: Clock
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            Keyboard.KeyPressed::class,
            Keyboard.KeyReleased::class
        )
        private const val LONG_MOVE_INTERVAL = 480
        private const val SHORT_MOVE_INTERVAL = 160
    }

    private var prevBuildStart: Point? = null
    private var currBuildStart: Point? = null

    private var direction: Direction = Direction.RIGHT

    private val speed = Globals.UNIT_SIZE

    private val moveTimer = BasicTimer(clock, LONG_MOVE_INTERVAL, ::onMove)

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        when (event) {
            is Keyboard.KeyPressed -> {
                keyPressed(event.key)
            }

            is Keyboard.KeyReleased -> {
                keyReleased(event.key)
            }

            else -> Unit
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        when (key) {
            Keyboard.Key.LEFT -> {
                direction = Direction.LEFT
                move()
            }

            Keyboard.Key.RIGHT -> {
                direction = Direction.RIGHT
                move()
            }

            Keyboard.Key.UP -> {
                direction = Direction.UP
                move()
            }

            Keyboard.Key.DOWN -> {
                direction = Direction.DOWN
                move()
            }

            Keyboard.Key.ACTION -> {
                val position = Point(cursor.x, cursor.y)
                if (prevBuildStart == null) {
                    cursor.build()
                } else if (position != prevBuildStart) {
                    cursor.build()
                } else {
                    cursor.buildNext()
                }
                prevBuildStart = currBuildStart
                currBuildStart = position
            }

            else -> Unit
        }
    }

    private fun keyReleased(key: Keyboard.Key) {
        if (direction == Direction.LEFT && key == Keyboard.Key.LEFT ||
            direction == Direction.RIGHT && key == Keyboard.Key.RIGHT ||
            direction == Direction.UP && key == Keyboard.Key.UP ||
            direction == Direction.DOWN && key == Keyboard.Key.DOWN
        ) {
            stop()
        }
        if (key == Keyboard.Key.ACTION) {
            prevBuildStart = currBuildStart
            currBuildStart = null
        }
    }

    fun update() {
        moveTimer.update()
    }

    private fun onMove(count: Int) {
        if (moveTimer.interval != SHORT_MOVE_INTERVAL) {
            moveTimer.restart(SHORT_MOVE_INTERVAL)
        }
        doMove(count)
    }

    private fun move() {
        if (!moveTimer.isStopped) {
            moveTimer.update()
            return
        }
        doMove(1)
        moveTimer.restart(LONG_MOVE_INTERVAL)
    }

    private fun doMove(count: Int) {
        cursor.setPosition(getNewX(count), getNewY(count))
        if (!level.contains(cursor.bounds)) {
            when (direction) {
                Direction.RIGHT -> cursor.setPosition(level.right - cursor.width + 1, cursor.y)
                Direction.LEFT -> cursor.setPosition(level.left, cursor.y)
                Direction.UP -> cursor.setPosition(cursor.x, level.top)
                Direction.DOWN -> cursor.setPosition(cursor.x, level.bottom - cursor.height + 1)
            }
        }
        if (currBuildStart != null) {
            prevBuildStart = null
            cursor.build()
        }
    }

    private fun getNewX(count: Int): Int {
        return when (direction) {
            Direction.RIGHT -> cursor.x + speed * count
            Direction.LEFT -> cursor.x - speed * count
            else -> cursor.x
        }
    }

    private fun getNewY(count: Int): Int {
        return when (direction) {
            Direction.UP -> cursor.y - speed * count
            Direction.DOWN -> cursor.y + speed * count
            else -> cursor.y
        }
    }

    fun stop() {
        moveTimer.stop()
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}