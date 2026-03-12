//package com.kalikov.game
//
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.argumentCaptor
//import org.mockito.kotlin.isA
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import kotlin.test.assertFalse
//import kotlin.test.assertNotNull
//import kotlin.test.assertTrue
//
//class PowerUpFactoryTest {
//    private lateinit var game: BattleCityGame
//    private lateinit var spriteContainer: SpriteContainer
//    private lateinit var factory: GamePowerUpManager
//
//    @BeforeEach
//    fun beforeEach() {
//        game = mockGame()
//        spriteContainer = mock()
//        factory = GamePowerUpManager(game, spriteContainer, PixelRect(), mock())
//    }
//
//    @Test
//    fun `should subscribe`() {
//        verify(game.eventManager).addSubscriber(
//            factory,
//            arrayOf(GameEnemyTanksManager.FlashingTankHit::class, GameEnemyTanksManager.EnemyCreated::class)
//        )
//    }
//
//    @Test
//    fun `should unsubscribe`() {
//        factory.dispose()
//        verify(game.eventManager).removeSubscriber(
//            factory,
//            arrayOf(GameEnemyTanksManager.FlashingTankHit::class, GameEnemyTanksManager.EnemyCreated::class)
//        )
//    }
//
//    @Test
//    fun `should create power-up when flashing tank is destroyed`() {
//        factory.notify(GameEnemyTanksManager.FlashingTankHit)
//
//        verify(spriteContainer).addSprite(isA<PowerUp>())
//    }
//
//    @Test
//    fun `should destroy power up when new flashing type appears`() {
//        factory.notify(GameEnemyTanksManager.FlashingTankHit)
//
//        val captor = argumentCaptor<PowerUp>()
//        verify(spriteContainer).addSprite(captor.capture())
//
//        val powerUp = captor.firstValue
//        assertNotNull(powerUp)
//        assertFalse(powerUp.isDestroyed)
//
//        val newTank = stubEnemyTank(game)
//        factory.notify(GameEnemyTanksManager.EnemyCreated(newTank, true))
//
//        assertTrue(powerUp.isDestroyed)
//    }
//
//    @Test
//    fun `should destroy power up when new flashing tank is destroyed`() {
//        factory.notify(GameEnemyTanksManager.FlashingTankHit)
//
//        val captor = argumentCaptor<PowerUp>()
//        verify(spriteContainer).addSprite(captor.capture())
//
//        val powerUp = captor.firstValue
//        assertNotNull(powerUp)
//        assertFalse(powerUp.isDestroyed)
//
//        factory.notify(GameEnemyTanksManager.FlashingTankHit)
//
//        assertTrue(powerUp.isDestroyed)
//    }
//}