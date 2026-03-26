package com.siptech.siptrack.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: Int = 1,
    val weightKg: Float = 70f,
    val gender: BiologicalSex = BiologicalSex.OTHER,
    val age: Int = 30,
    val metabolicRate: Float = 0.015f, // g/dL/hr — default Widmark
    val personalLimitBac: Float = 0.06f,
    val driveLimitBac: Float = 0.08f,  // US legal limit; configurable
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val preferMetric: Boolean = false,
    val enableNotifications: Boolean = true,
    val enableHealthKit: Boolean = false,
    val appMode: AppMode = AppMode.NORMAL,
    val onboardingComplete: Boolean = false,
) {
    /** Widmark body water constant */
    val widmarkR: Float get() = when (gender) {
        BiologicalSex.MALE -> 0.68f
        BiologicalSex.FEMALE -> 0.55f
        BiologicalSex.OTHER -> 0.615f // midpoint
    }

    val weightLbs: Float get() = weightKg * 2.20462f
}

@Serializable
enum class BiologicalSex(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other / Prefer not to say")
}
