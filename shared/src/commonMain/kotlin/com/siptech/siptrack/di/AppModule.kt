package com.siptech.siptrack.di

import com.siptech.siptrack.repository.BeverageApiService
import com.siptech.siptrack.viewmodels.DashboardViewModel
import com.siptech.siptrack.viewmodels.HistoryViewModel
import com.siptech.siptrack.viewmodels.LogDrinkViewModel
import com.siptech.siptrack.viewmodels.SettingsViewModel
import org.koin.dsl.module

val appModule = module {
    // API service (singleton)
    single { BeverageApiService() }

    // ViewModels (factory — new instance per screen)
    factory { (profile: com.siptech.siptrack.models.UserProfile) ->
        DashboardViewModel(profile)
    }
    factory { HistoryViewModel() }
    factory { (sessionId: String) -> LogDrinkViewModel(sessionId) }
    factory { SettingsViewModel() }
}
