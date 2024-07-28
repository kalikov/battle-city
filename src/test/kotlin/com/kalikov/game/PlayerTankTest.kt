package com.kalikov.game

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PlayerTankTest : TankTest<PlayerTank>() {
    override fun createTank(): PlayerTank {
        return PlayerTank.create(eventManager, mock(), imageManager, clock, 0, 0, Player(eventManager))
    }

    @Test
    fun `should fire player destroy event`() {
        tank.destroy()
        tank.update()
        verify(eventManager).fireEvent(PlayerTank.PlayerDestroyed(tank))
    }

    @Test
    fun `should be in invincible state when appearing state ends`() {
        tank.state = TankStateAppearing(mock(), mock(), tank)
        tank.notify(TankStateAppearing.End(tank))
        assertIs<TankStateInvincible>(tank.state)
    }

    @Test
    fun `should upgrade to first level`() {
        assertEquals(0, tank.upgradeLevel)
        assertEquals(Bullet.Speed.NORMAL, tank.bulletSpeed)

        tank.upgrade()

        assertEquals(1, tank.upgradeLevel)
        assertEquals(Bullet.Speed.FAST, tank.bulletSpeed)
    }

    @Test
    fun `should upgrade to second level`() {
        tank.upgrade()

        assertEquals(1, tank.upgradeLevel)
        assertEquals(1, tank.bulletsLimit)

        tank.upgrade()

        assertEquals(2, tank.upgradeLevel)
        assertEquals(2, tank.bulletsLimit)
    }

    @Test
    fun `should upgrade to third level`() {
        tank.upgrade()
        tank.upgrade()

        assertEquals(2, tank.upgradeLevel)
        assertEquals(Bullet.Type.REGULAR, tank.bulletType)

        tank.upgrade()

        assertEquals(3, tank.upgradeLevel)
        assertEquals(Bullet.Type.ENHANCED, tank.bulletType)
    }

    @Test
    fun `should remain in third level and not upgrade to fourth level`() {
        tank.upgrade()
        tank.upgrade()
        tank.upgrade()

        assertEquals(3, tank.upgradeLevel)

        tank.upgrade()

        assertEquals(3, tank.upgradeLevel)
    }

    @Test
    fun `should face up direction when appearing state ends`() {
        tank.state = TankStateAppearing(mock(), mock(), tank)
        tank.direction = Direction.DOWN
        tank.notify(TankStateAppearing.End(tank))
        assertEquals(Direction.UP, tank.direction)
    }

    @Test
    @DisplayName("should draw tank in normal state with right direction")
    fun shouldDrawNormalRight() {
        shouldDrawTank(Direction.RIGHT, "tank_player_right_c0_t1")
    }

    @Test
    @DisplayName("should draw tank in normal state with up direction")
    fun shouldDrawNormalUp() {
        shouldDrawTank(Direction.UP, "tank_player_up_c0_t1")
    }

    @Test
    @DisplayName("should draw tank in normal state with down direction")
    fun shouldDrawNormalDown() {
        shouldDrawTank(Direction.DOWN, "tank_player_down_c0_t1")
    }

    @Test
    @DisplayName("should draw tank in normal state with left direction")
    fun shouldDrawNormalLeft() {
        shouldDrawTank(Direction.LEFT, "tank_player_left_c0_t1")
    }

    @Test
    @DisplayName("should draw tank in invincible state with right direction")
    fun shouldDrawInvincibleRight() {
        tank.state = TankStateInvincible(eventManager, imageManager, tank)
        shouldDrawTank(Direction.RIGHT, "tank_player_right_c0_t1_i")
    }

    @Test
    @DisplayName("should draw tank in invincible state with up direction")
    fun shouldDrawInvincibleUp() {
        tank.state = TankStateInvincible(eventManager, imageManager, tank)
        shouldDrawTank(Direction.UP, "tank_player_up_c0_t1_i")
    }

    @Test
    @DisplayName("should draw tank in invincible state with down direction")
    fun shouldDrawInvincibleDown() {
        tank.state = TankStateInvincible(eventManager, imageManager, tank)
        shouldDrawTank(Direction.DOWN, "tank_player_down_c0_t1_i")
    }

    @Test
    @DisplayName("should draw tank in invincible state with left direction")
    fun shouldDrawInvincibleLeft() {
        tank.state = TankStateInvincible(eventManager, imageManager, tank)
        shouldDrawTank(Direction.LEFT, "tank_player_left_c0_t1_i")
    }

    private fun shouldDrawTank(direction: Direction, imageName: String) {
        val image = BufferedImage(Globals.UNIT_SIZE, Globals.UNIT_SIZE, BufferedImage.TYPE_INT_ARGB)
        tank.direction = direction
        tank.draw(AwtScreenSurface(mock(), image))

        assertImageEquals("$imageName.png", image)
    }
}