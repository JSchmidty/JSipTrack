package com.siptech.siptrack.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class DrinkSession(
    val id: String,
    val startTime: Instant,
    val endTime: Instant? = null,
    val drinks: List<Drink> = emptyList(),
    val mode: AppMode = AppMode.NORMAL,
    val notes: String? = null,
) {
    val isActive: Boolean get() = endTime == null
    val totalDrinks: Int get() = drinks.size
    val totalStandardDrinks: Float get() = drinks.sumOf { it.standardDrinks.toDouble() }.toFloat()
    val totalCalories: Float get() = drinks.sumOf { it.calories.toDouble() }.toFloat()
    val totalAlcoholGrams: Float get() = drinks.sumOf { it.alcoholGrams.toDouble() }.toFloat()
}

@Serializable
enum class AppMode(val displayName: String) {
    NORMAL("Standard"),
    DISCREET("Discreet"),
    PROFESSIONAL("Professional Tasting"),
    RECOVERY("Recovery"),
    DESIGNATED_DRIVER("Designated Driver")
}
