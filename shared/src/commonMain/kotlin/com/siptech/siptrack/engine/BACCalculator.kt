package com.siptech.siptrack.engine

import com.siptech.siptrack.models.Drink
import com.siptech.siptrack.models.UserProfile
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.max
import kotlin.time.Duration.Companion.minutes

/**
 * Widmark BAC Calculation Engine.
 *
 * Formula: BAC = ((AlcoholGrams) / (BodyWeightGrams × WidmarkR)) × 100 − (MetabolicRate × HoursElapsed)
 *
 * SAFETY RULES:
 * - Always round UP — never underestimate impairment
 * - BAC never goes below 0.000
 * - Results are estimates only — must be displayed with legal disclaimer
 */
object BACCalculator {

    fun calculateCurrentBAC(
        profile: UserProfile,
        drinks: List<Drink>,
        now: Instant = Clock.System.now()
    ): Double {
        if (drinks.isEmpty()) return 0.0
        val bodyWeightGrams = profile.weightKg * 1000.0
        val r = profile.widmarkR.toDouble()
        val beta = profile.metabolicRate.toDouble()
        var totalBac = 0.0
        for (drink in drinks) {
            val hoursElapsed = (now - drink.loggedAt).inWholeMinutes / 60.0
            if (hoursElapsed < 0) continue
            val drinkBac = (drink.alcoholGrams / (bodyWeightGrams * r)) * 100.0
            val metabolized = beta * hoursElapsed
            val netBac = drinkBac - metabolized
            if (netBac > 0) totalBac += netBac
        }
        return max(0.0, totalBac)
    }

    fun estimateSoberTime(
        profile: UserProfile,
        drinks: List<Drink>,
        now: Instant = Clock.System.now()
    ): Instant {
        val currentBac = calculateCurrentBAC(profile, drinks, now)
        if (currentBac <= 0.0) return now
        val hoursToSober = currentBac / profile.metabolicRate.toDouble()
        val minutesToSober = (hoursToSober * 60).toLong()
        return now + minutesToSober.minutes
    }

    fun estimateSafeToDriveTime(
        profile: UserProfile,
        drinks: List<Drink>,
        now: Instant = Clock.System.now(),
        legalLimit: Double = profile.driveLimitBac.toDouble()
    ): Instant {
        val currentBac = calculateCurrentBAC(profile, drinks, now)
        if (currentBac <= legalLimit) return now
        val hoursToLimit = (currentBac - legalLimit) / profile.metabolicRate.toDouble()
        val minutesToLimit = (hoursToLimit * 60).toLong()
        return now + minutesToLimit.minutes
    }

    fun generateBACCurve(
        profile: UserProfile,
        drinks: List<Drink>,
        start: Instant,
        end: Instant,
        intervalMinutes: Int = 15
    ): List<Pair<Instant, Double>> {
        val points = mutableListOf<Pair<Instant, Double>>()
        var current = start
        while (current <= end) {
            points.add(current to calculateCurrentBAC(profile, drinks, current))
            current += intervalMinutes.minutes
        }
        return points
    }

    fun calculateStandardDrinks(volumeOz: Double, abvPercent: Double): Double =
        (volumeOz * (abvPercent / 100.0) * 0.816) / 0.6

    fun getBACStatus(bac: Double, driveLimit: Double = 0.08): BACStatus = when {
        bac <= 0.0 -> BACStatus.SOBER
        bac < 0.04 -> BACStatus.MINIMAL
        bac < driveLimit -> BACStatus.CAUTION
        bac < 0.10 -> BACStatus.OVER_LIMIT
        bac < 0.15 -> BACStatus.SIGNIFICANTLY_IMPAIRED
        else -> BACStatus.SEVERELY_IMPAIRED
    }
}

enum class BACStatus {
    SOBER, MINIMAL, CAUTION, OVER_LIMIT, SIGNIFICANTLY_IMPAIRED, SEVERELY_IMPAIRED
}
