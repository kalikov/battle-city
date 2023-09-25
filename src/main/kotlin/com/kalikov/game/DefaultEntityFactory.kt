package com.kalikov.game

import java.time.Clock

class DefaultEntityFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val clock: Clock
) : EntityFactory {
    private val map = HashMap<String, (Int, Int) -> Sprite>()

    init {
        map[BrickWall.CLASS_NAME] = { x, y ->
            BrickWall(eventManager, imageManager, x, y)
        }
        map[SteelWall.CLASS_NAME] = { x, y ->
            SteelWall(eventManager, imageManager, x, y)
        }
        map[Water.CLASS_NAME] = { x, y ->
            Water(eventManager, imageManager, clock, x, y)
        }
        map[Trees.CLASS_NAME] = { x, y ->
            Trees(eventManager, imageManager, x, y)
        }
        map[Ice.CLASS_NAME] = { x, y ->
            Ice(eventManager, imageManager, x, y)
        }
    }

    override fun create(type: String, x: Int, y: Int): Sprite {
        val factory = map[type] ?: throw RuntimeException("Factory \"$type\" not found")
        return factory(x, y)
    }
}