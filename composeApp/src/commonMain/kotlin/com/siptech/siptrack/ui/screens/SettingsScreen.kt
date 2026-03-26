package com.siptech.siptrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.siptech.siptrack.models.AppMode
import com.siptech.siptrack.models.BiologicalSex
import com.siptech.siptrack.viewmodels.SettingsUiState

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onWeightChange: (Float) -> Unit,
    onGenderChange: (BiologicalSex) -> Unit,
    onDriveLimitChange: (Float) -> Unit,
    onAppModeChange: (AppMode) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onHealthKitChange: (Boolean) -> Unit,
    onSave: () -> Unit,
) {
    val profile = uiState.profile
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // BAC Profile section
        SectionHeader("BAC Profile")
        OutlinedTextField(
            value = profile.weightKg.toString(),
            onValueChange = { it.toFloatOrNull()?.let(onWeightChange) },
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth(),
        )

        // Gender selection
        Text("Biological Sex", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BiologicalSex.entries.forEach { sex ->
                FilterChip(
                    selected = profile.gender == sex,
                    onClick = { onGenderChange(sex) },
                    label = { Text(sex.displayName) },
                )
            }
        }

        OutlinedTextField(
            value = profile.driveLimitBac.toString(),
            onValueChange = { it.toFloatOrNull()?.let(onDriveLimitChange) },
            label = { Text("Drive Limit BAC (default 0.08)") },
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalDivider()

        // App Mode section
        SectionHeader("App Mode")
        AppMode.entries.forEach { mode ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(mode.displayName, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = when (mode) {
                            AppMode.NORMAL -> "Full UI, all features"
                            AppMode.DISCREET -> "Minimal UI — no alcohol text visible"
                            AppMode.PROFESSIONAL -> "Micro-dose presets, tasting focus"
                            AppMode.RECOVERY -> "Zero-drink focus, streak tracking"
                            AppMode.DESIGNATED_DRIVER -> "DD mode, auto-logs 0 drinks"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                }
                RadioButton(
                    selected = profile.appMode == mode,
                    onClick = { onAppModeChange(mode) },
                )
            }
        }

        HorizontalDivider()

        // Notifications & Health
        SectionHeader("Features")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Notifications")
            Switch(checked = profile.enableNotifications, onCheckedChange = onNotificationsChange)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("HealthKit / Health Connect")
            Switch(checked = profile.enableHealthKit, onCheckedChange = onHealthKitChange)
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
        ) {
            if (uiState.isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Save Settings")
        }

        if (uiState.saveSuccess) {
            Text("✅ Saved!", color = MaterialTheme.colorScheme.secondary)
        }
        if (uiState.errorMessage != null) {
            Text("❌ ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
        }

        // Legal disclaimer
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Text(
                text = "⚠️ LEGAL DISCLAIMER\n\nBAC estimates are for informational purposes only. Never drive based solely on this app's readings. Always err on the side of caution. If in doubt, don't drive.",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold,
    )
}
