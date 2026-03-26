package com.siptech.siptrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siptech.siptrack.engine.BACStatus
import com.siptech.siptrack.models.AppMode
import com.siptech.siptrack.ui.theme.bacToColor
import com.siptech.siptrack.viewmodels.DashboardUiState

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onLogDrinkClick: () -> Unit,
    onEndSessionClick: () -> Unit,
) {
    val bacColor = bacToColor(uiState.currentBac)

    if (uiState.appMode == AppMode.DISCREET) {
        DiscreetDashboard(uiState)
        return
    }
    if (uiState.appMode == AppMode.RECOVERY) {
        RecoveryDashboard(uiState)
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // BAC Gauge
        BACGaugeSection(bac = uiState.currentBac, color = bacColor)

        Spacer(Modifier.height(24.dp))

        // Status chip
        BACStatusChip(status = uiState.bacStatus, color = bacColor)

        Spacer(Modifier.height(24.dp))

        // Stats row
        StatsRow(
            drinkCount = uiState.drinkCount,
            standardDrinks = uiState.totalStandardDrinks,
            calories = uiState.totalCalories,
        )

        Spacer(Modifier.height(32.dp))

        // Drive-safe countdown
        if (uiState.safeToDriveAt != null && !uiState.isSafeToDrive) {
            DriveSafeCard(safeToDriveAt = uiState.safeToDriveAt.toString())
            Spacer(Modifier.height(16.dp))
        }

        // Log drink FAB
        Button(
            onClick = onLogDrinkClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("+ Log Drink", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (uiState.activeSession != null) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onEndSessionClick) {
                Text("End Session", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun BACGaugeSection(bac: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("BLOOD ALCOHOL", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(
            text = "%.3f".format(bac),
            fontSize = 72.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color,
        )
        Text("g/dL  ⚠️ Estimate only", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
private fun BACStatusChip(status: BACStatus, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = when (status) {
                BACStatus.SOBER -> "✅ Sober"
                BACStatus.MINIMAL -> "🟢 Minimal Effect"
                BACStatus.CAUTION -> "🟡 Caution"
                BACStatus.OVER_LIMIT -> "🔴 Over Limit"
                BACStatus.SIGNIFICANTLY_IMPAIRED -> "🔴 Significantly Impaired"
                BACStatus.SEVERELY_IMPAIRED -> "🚨 Severely Impaired"
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun StatsRow(drinkCount: Int, standardDrinks: Float, calories: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatItem(label = "Drinks", value = drinkCount.toString())
        StatItem(label = "Standard", value = "%.1f".format(standardDrinks))
        StatItem(label = "Calories", value = "%.0f".format(calories))
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
private fun DriveSafeCard(safeToDriveAt: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("🚗", fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Do NOT drive", fontWeight = FontWeight.Bold)
                Text("Safe to drive at: $safeToDriveAt", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun DiscreetDashboard(state: DashboardUiState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (state.isSafeToDrive) "✓ OK" else "⏱",
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = if (state.isSafeToDrive) "Good to go" else "${state.sessionDurationMinutes}m",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Gray,
        )
    }
}

@Composable
private fun RecoveryDashboard(state: DashboardUiState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🌟", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text("You're doing great!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Streak: ${state.currentStreak ?: 0} days drink-free",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
        )
    }
}

private val DashboardUiState.currentStreak: Int? get() = null
