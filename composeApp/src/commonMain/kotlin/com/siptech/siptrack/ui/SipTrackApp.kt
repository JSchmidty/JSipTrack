package com.siptech.siptrack.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.siptech.siptrack.ui.screens.DashboardScreen
import com.siptech.siptrack.ui.screens.HistoryScreen
import com.siptech.siptrack.ui.screens.SettingsScreen
import com.siptech.siptrack.ui.theme.SipTrackTheme
import com.siptech.siptrack.viewmodels.DashboardUiState
import com.siptech.siptrack.viewmodels.HistoryUiState
import com.siptech.siptrack.viewmodels.SettingsUiState

@Composable
fun SipTrackApp() {
    SipTrackTheme {
        var selectedTab by remember { mutableStateOf(0) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, "Dashboard") },
                        label = { Text("Dashboard") },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, "History") },
                        label = { Text("History") },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, "Settings") },
                        label = { Text("Settings") },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    0 -> DashboardScreen(
                        uiState = DashboardUiState(),
                        onLogDrinkClick = {},
                        onEndSessionClick = {},
                    )
                    1 -> HistoryScreen(
                        uiState = HistoryUiState(),
                        onSessionClick = {},
                    )
                    2 -> SettingsScreen(
                        uiState = SettingsUiState(),
                        onWeightChange = {},
                        onGenderChange = {},
                        onDriveLimitChange = {},
                        onAppModeChange = {},
                        onNotificationsChange = {},
                        onHealthKitChange = {},
                        onSave = {},
                    )
                }
            }
        }
    }
}
