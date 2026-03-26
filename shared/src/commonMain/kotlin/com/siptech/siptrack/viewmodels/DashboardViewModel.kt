package com.siptech.siptrack.viewmodels

import com.siptech.siptrack.engine.BACCalculator
import com.siptech.siptrack.engine.BACStatus
import com.siptech.siptrack.models.AppMode
import com.siptech.siptrack.models.DrinkSession
import com.siptech.siptrack.models.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

data class DashboardUiState(
    val currentBac: Double = 0.0,
    val bacStatus: BACStatus = BACStatus.SOBER,
    val soberAt: Instant? = null,
    val safeToDriveAt: Instant? = null,
    val isSafeToDrive: Boolean = true,
    val activeSession: DrinkSession? = null,
    val drinkCount: Int = 0,
    val totalCalories: Float = 0f,
    val totalStandardDrinks: Float = 0f,
    val sessionDurationMinutes: Long = 0,
    val appMode: AppMode = AppMode.NORMAL,
    val isLoading: Boolean = false,
)

class DashboardViewModel(
    private val profile: UserProfile,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var activeSession: DrinkSession? = null

    init { startRefreshTimer() }

    private fun startRefreshTimer() {
        scope.launch {
            while (true) {
                refresh()
                kotlinx.coroutines.delay(60.seconds)
            }
        }
    }

    fun refresh() {
        val session = activeSession ?: return updateEmpty()
        val now = Clock.System.now()
        val bac = BACCalculator.calculateCurrentBAC(profile, session.drinks, now)
        val status = BACCalculator.getBACStatus(bac, profile.driveLimitBac.toDouble())
        val soberAt = BACCalculator.estimateSoberTime(profile, session.drinks, now)
        val safeToDriveAt = BACCalculator.estimateSafeToDriveTime(profile, session.drinks, now)
        val durationMinutes = (now - session.startTime).inWholeMinutes
        _uiState.value = DashboardUiState(
            currentBac = bac,
            bacStatus = status,
            soberAt = soberAt,
            safeToDriveAt = safeToDriveAt,
            isSafeToDrive = bac < profile.driveLimitBac,
            activeSession = session,
            drinkCount = session.drinks.size,
            totalCalories = session.totalCalories,
            totalStandardDrinks = session.totalStandardDrinks,
            sessionDurationMinutes = durationMinutes,
            appMode = profile.appMode,
        )
    }

    fun loadSession(session: DrinkSession?) {
        activeSession = session
        refresh()
    }

    private fun updateEmpty() {
        _uiState.value = DashboardUiState(appMode = profile.appMode)
    }
}
