package com.siptech.siptrack.viewmodels

import com.siptech.siptrack.models.Drink
import com.siptech.siptrack.models.DrinkCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class LogDrinkUiState(
    val customName: String = "",
    val customAbv: Float = 5.0f,
    val customVolumeOz: Float = 12.0f,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
)

class LogDrinkViewModel(private val sessionId: String) {
    private val _uiState = MutableStateFlow(LogDrinkUiState())
    val uiState: StateFlow<LogDrinkUiState> = _uiState.asStateFlow()

    val quickDrinks: List<DrinkCategory> = listOf(
        DrinkCategory.BEER,
        DrinkCategory.WINE,
        DrinkCategory.SPIRIT,
        DrinkCategory.COCKTAIL,
        DrinkCategory.HARD_SELTZER,
        DrinkCategory.MICRO_TASTE,
    )

    @OptIn(ExperimentalUuidApi::class)
    fun createQuickDrink(category: DrinkCategory): Drink = Drink(
        id = Uuid.random().toString(),
        sessionId = sessionId,
        name = category.displayName,
        category = category,
        abvPercent = category.defaultAbv,
        volumeOz = category.defaultVolumeOz,
        loggedAt = Clock.System.now(),
        isMicroDose = category == DrinkCategory.MICRO_TASTE,
    )

    @OptIn(ExperimentalUuidApi::class)
    fun createCustomDrink(): Drink {
        val state = _uiState.value
        return Drink(
            id = Uuid.random().toString(),
            sessionId = sessionId,
            name = state.customName.ifBlank { "Custom Drink" },
            category = DrinkCategory.CUSTOM,
            abvPercent = state.customAbv,
            volumeOz = state.customVolumeOz,
            loggedAt = Clock.System.now(),
        )
    }

    fun updateCustomName(name: String) { _uiState.value = _uiState.value.copy(customName = name) }
    fun updateCustomAbv(abv: Float) { _uiState.value = _uiState.value.copy(customAbv = abv) }
    fun updateCustomVolume(oz: Float) { _uiState.value = _uiState.value.copy(customVolumeOz = oz) }
    fun updateSearchQuery(q: String) { _uiState.value = _uiState.value.copy(searchQuery = q) }
}
