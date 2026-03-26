package com.siptech.siptrack.di

import com.siptech.siptrack.db.SipTrackDatabase
import com.siptech.siptrack.repository.BeverageApiService
import com.siptech.siptrack.repository.DrinkRepository
import com.siptech.siptrack.repository.SqlDelightDrinkRepository
import com.siptech.siptrack.viewmodels.DashboardViewModel
import com.siptech.siptrack.viewmodels.HistoryViewModel
import com.siptech.siptrack.viewmodels.LogDrinkViewModel
import com.siptech.siptrack.viewmodels.SettingsViewModel
import org.koin.dsl.module

fun appModule() = module {
    // API service (singleton)
    single { BeverageApiService() }

    // Repository (singleton, backed by SQLDelight)
    single<DrinkRepository> { SqlDelightDrinkRepository(get<SipTrackDatabase>()) }

    // ViewModels (factory — new instance per screen)
    factory { (profile: com.siptech.siptrack.models.UserProfile) ->
        DashboardViewModel(profile)
    }
    factory { HistoryViewModel() }
    factory { (sessionId: String) -> LogDrinkViewModel(sessionId) }
    factory { SettingsViewModel() }
}
