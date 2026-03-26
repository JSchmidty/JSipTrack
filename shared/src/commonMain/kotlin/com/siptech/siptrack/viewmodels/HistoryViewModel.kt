package com.siptech.siptrack.viewmodels

import com.siptech.siptrack.models.DrinkSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryUiState(
    val sessions: List<DrinkSession> = emptyList(),
    val weeklyDrinkCount: Int = 0,
    val drinkFreeDays: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isLoading: Boolean = false,
)

class HistoryViewModel {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadSessions(sessions: List<DrinkSession>) {
        val sortedSessions = sessions.sortedByDescending { it.startTime }
        _uiState.value = _uiState.value.copy(
            sessions = sortedSessions,
            weeklyDrinkCount = sortedSessions.sumOf { it.totalDrinks },
            isLoading = false,
        )
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }
}
