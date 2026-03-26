package com.siptech.siptrack.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Drink(
    val id: String,
    val sessionId: String,
    val name: String,
    val category: DrinkCategory,
    val abvPercent: Float,
    val volumeOz: Float,
    val loggedAt: Instant,
    val productId: String? = null,
    val notes: String? = null,
    val isMicroDose: Boolean = false,
) {
    /** Standard drinks = (volumeOz × ABV × 0.816) / 0.6 */
    val standardDrinks: Float
        get() = (volumeOz * (abvPercent / 100f) * 0.816f) / 0.6f

    /** Alcohol grams for Widmark formula */
    val alcoholGrams: Float
        get() = volumeOz * 29.5735f * (abvPercent / 100f) * 0.789f

    /** Estimated calories (7 cal/gram of alcohol) */
    val calories: Float
        get() = alcoholGrams * 7f
}

@Serializable
enum class DrinkCategory(val displayName: String, val defaultAbv: Float, val defaultVolumeOz: Float) {
    BEER("Beer", 5.0f, 12.0f),
    WINE("Wine", 12.0f, 5.0f),
    SPIRIT("Spirit", 40.0f, 1.5f),
    COCKTAIL("Cocktail", 15.0f, 4.0f),
    HARD_SELTZER("Hard Seltzer", 5.0f, 12.0f),
    CIDER("Cider", 5.5f, 12.0f),
    MICRO_TASTE("Taste", 40.0f, 0.5f),
    CUSTOM("Custom", 5.0f, 12.0f)
}
