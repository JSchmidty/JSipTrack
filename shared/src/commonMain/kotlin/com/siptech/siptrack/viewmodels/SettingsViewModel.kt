package com.siptech.siptrack.viewmodels

import com.siptech.siptrack.models.AppMode
import com.siptech.siptrack.models.BiologicalSex
import com.siptech.siptrack.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val profile: UserProfile = UserProfile(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
)

class SettingsViewModel(initialProfile: UserProfile = UserProfile()) {
    private val _uiState = MutableStateFlow(SettingsUiState(profile = initialProfile))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateWeightKg(kg: Float) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(weightKg = kg))
    }
    fun updateGender(sex: BiologicalSex) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(gender = sex))
    }
    fun updateAge(age: Int) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(age = age))
    }
    fun updateDriveLimit(limit: Float) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(driveLimitBac = limit))
    }
    fun updatePersonalLimit(limit: Float) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(personalLimitBac = limit))
    }
    fun updateMetabolicRate(rate: Float) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(metabolicRate = rate))
    }
    fun updateAppMode(mode: AppMode) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(appMode = mode))
    }
    fun updateEmergencyContact(name: String, phone: String) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(
                emergencyContactName = name,
                emergencyContactPhone = phone
            )
        )
    }
    fun updateNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(enableNotifications = enabled))
    }
    fun updateHealthKit(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(enableHealthKit = enabled))
    }
    fun updatePreferMetric(metric: Boolean) {
        _uiState.value = _uiState.value.copy(profile = _uiState.value.profile.copy(preferMetric = metric))
    }
    fun getProfile(): UserProfile = _uiState.value.profile
}
