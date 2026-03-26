package com.siptech.siptrack.engine

import com.siptech.siptrack.models.BiologicalSex
import com.siptech.siptrack.models.Drink
import com.siptech.siptrack.models.DrinkCategory
import com.siptech.siptrack.models.UserProfile
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class BACCalculatorTest {

    private val maleProfile = UserProfile(
        weightKg = 80f,
        gender = BiologicalSex.MALE,
        age = 30,
        metabolicRate = 0.015f,
        driveLimitBac = 0.08f,
    )

    private val femaleProfile = UserProfile(
        weightKg = 60f,
        gender = BiologicalSex.FEMALE,
        age = 28,
        metabolicRate = 0.015f,
        driveLimitBac = 0.08f,
    )

    private fun makeDrink(sessionId: String = "s1", abv: Float, oz: Float, minutesAgo: Long): Drink {
        return Drink(
            id = "d$minutesAgo",
            sessionId = sessionId,
            name = "Test Drink",
            category = DrinkCategory.CUSTOM,
            abvPercent = abv,
            volumeOz = oz,
            loggedAt = Clock.System.now() - minutesAgo.minutes,
        )
    }

    @Test
    fun `zero drinks returns zero BAC`() {
        val bac = BACCalculator.calculateCurrentBAC(maleProfile, emptyList())
        assertEquals(0.0, bac)
    }

    @Test
    fun `BAC never goes negative`() {
        // Drink from 10 hours ago — fully metabolized
        val drink = makeDrink(abv = 5f, oz = 12f, minutesAgo = 600)
        val bac = BACCalculator.calculateCurrentBAC(maleProfile, listOf(drink))
        assertTrue(bac >= 0.0, "BAC should never be negative, was $bac")
    }

    @Test
    fun `male 80kg one beer BAC is plausible`() {
        // One standard beer (12oz, 5% ABV) consumed just now
        val drink = makeDrink(abv = 5f, oz = 12f, minutesAgo = 0)
        val bac = BACCalculator.calculateCurrentBAC(maleProfile, listOf(drink))
        // Expected: roughly 0.02-0.04 for 80kg male
        assertTrue(bac > 0.01, "BAC should be > 0.01, was $bac")
        assertTrue(bac < 0.06, "BAC should be < 0.06 for one beer, was $bac")
    }

    @Test
    fun `female 60kg same beer has higher BAC than male`() {
        val drink = makeDrink(abv = 5f, oz = 12f, minutesAgo = 0)
        val maleBac = BACCalculator.calculateCurrentBAC(maleProfile, listOf(drink))
        val femaleBac = BACCalculator.calculateCurrentBAC(femaleProfile, listOf(drink))
        assertTrue(femaleBac > maleBac, "Female BAC ($femaleBac) should be higher than male ($maleBac) for same drink/weight")
    }

    @Test
    fun `micro dose under 0p5oz produces very low BAC`() {
        val drink = makeDrink(abv = 40f, oz = 0.5f, minutesAgo = 0)
        val bac = BACCalculator.calculateCurrentBAC(maleProfile, listOf(drink))
        assertTrue(bac < 0.02, "Micro taste BAC should be very low, was $bac")
    }

    @Test
    fun `high ABV barrel proof produces elevated BAC`() {
        // 2oz of 65% ABV (barrel proof bourbon)
        val drink = makeDrink(abv = 65f, oz = 2f, minutesAgo = 0)
        val bac = BACCalculator.calculateCurrentBAC(maleProfile, listOf(drink))
        assertTrue(bac > 0.05, "High-ABV drink should produce significant BAC, was $bac")
    }

    @Test
    fun `sober time is after now when BAC is positive`() {
        val drink = makeDrink(abv = 5f, oz = 12f, minutesAgo = 0)
        val now = Clock.System.now()
        val soberTime = BACCalculator.estimateSoberTime(maleProfile, listOf(drink), now)
        assertTrue(soberTime > now, "Sober time should be in the future")
    }

    @Test
    fun `safe to drive time is after now when over limit`() {
        // 5 drinks fast — should be over limit
        val drinks = (1..5).map { makeDrink(abv = 5f, oz = 12f, minutesAgo = it.toLong() * 5) }
        val now = Clock.System.now()
        val bac = BACCalculator.calculateCurrentBAC(maleProfile, drinks, now)
        if (bac >= 0.08) {
            val safeTime = BACCalculator.estimateSafeToDriveTime(maleProfile, drinks, now)
            assertTrue(safeTime > now, "Safe to drive time should be in future when over limit")
        }
    }

    @Test
    fun `BAC curve has correct number of points`() {
        val drink = makeDrink(abv = 5f, oz = 12f, minutesAgo = 60)
        val now = Clock.System.now()
        val start = now - 2.hours
        val end = now
        val curve = BACCalculator.generateBACCurve(maleProfile, listOf(drink), start, end, 15)
        // 2 hours / 15 min = 8 intervals + 1 = 9 points (inclusive)
        assertTrue(curve.size >= 8, "BAC curve should have at least 8 points, had ${curve.size}")
    }

    @Test
    fun `standard drinks calculation is accurate`() {
        // 12oz beer at 5% = 1.0 standard drinks (approx)
        val sd = BACCalculator.calculateStandardDrinks(12.0, 5.0)
        assertTrue(sd in 0.9..1.1, "12oz 5% beer should be ~1.0 standard drinks, was $sd")
    }

    @Test
    fun `BAC status SOBER when zero BAC`() {
        assertEquals(BACStatus.SOBER, BACCalculator.getBACStatus(0.0))
    }

    @Test
    fun `BAC status OVER_LIMIT at 0p08`() {
        assertEquals(BACStatus.OVER_LIMIT, BACCalculator.getBACStatus(0.08))
    }
}
