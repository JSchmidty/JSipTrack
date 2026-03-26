package com.siptech.siptrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siptech.siptrack.models.AppMode
import com.siptech.siptrack.models.BiologicalSex
import com.siptech.siptrack.models.UserProfile

/**
 * Onboarding — 3-step first-run flow.
 * Step 1: Welcome + weight/sex
 * Step 2: Drive limit + legal disclaimer
 * Step 3: App mode selection
 */
@Composable
fun OnboardingScreen(
    onComplete: (UserProfile) -> Unit,
) {
    var step by remember { mutableStateOf(1) }
    var weightKg by remember { mutableStateOf(70f) }
    var sex by remember { mutableStateOf(BiologicalSex.OTHER) }
    var driveLimit by remember { mutableStateOf(0.08f) }
    var appMode by remember { mutableStateOf(AppMode.NORMAL) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { step / 3f },
            modifier = Modifier.fillMaxWidth(),
        )
        Text("Step $step of 3", style = MaterialTheme.typography.labelMedium, color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        when (step) {
            1 -> {
                Text("👋 Welcome to SipTrack", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Let's set up your BAC profile for accurate estimates.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(
                    value = weightKg.toString(),
                    onValueChange = { it.toFloatOrNull()?.let { v -> weightKg = v } },
                    label = { Text("Your weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Text("Biological Sex (for BAC calculation)", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BiologicalSex.entries.forEach { s ->
                        FilterChip(
                            selected = sex == s,
                            onClick = { sex = s },
                            label = { Text(s.displayName) },
                        )
                    }
                }
            }
            2 -> {
                Text("🚗 Drive Limit", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "Set the BAC threshold where SipTrack warns you not to drive.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(
                    value = driveLimit.toString(),
                    onValueChange = { it.toFloatOrNull()?.let { v -> driveLimit = v } },
                    label = { Text("Drive limit BAC (US default: 0.08)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = "⚠️ BAC estimates are approximate. Never drive based solely on this app. When in doubt, don't drive.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            3 -> {
                Text("🎛️ Choose Your Mode", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "You can change this anytime in Settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )
                Spacer(Modifier.height(16.dp))
                AppMode.entries.forEach { mode ->
                    ElevatedCard(
                        onClick = { appMode = mode },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (appMode == mode)
                            CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        else CardDefaults.elevatedCardColors(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(mode.displayName, fontWeight = FontWeight.SemiBold)
                                Text(
                                    when (mode) {
                                        AppMode.NORMAL -> "Full UI with all features"
                                        AppMode.DISCREET -> "Minimal, no alcohol text"
                                        AppMode.PROFESSIONAL -> "Tasting + micro-dose focus"
                                        AppMode.RECOVERY -> "Zero-drink + streak tracking"
                                        AppMode.DESIGNATED_DRIVER -> "DD mode, logs 0 drinks"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                )
                            }
                            if (appMode == mode) Text("✓", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        if (step < 3) {
            Button(
                onClick = { step++ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) { Text("Next", fontSize = 18.sp) }
        } else {
            Button(
                onClick = {
                    onComplete(
                        UserProfile(
                            weightKg = weightKg,
                            gender = sex,
                            driveLimitBac = driveLimit,
                            appMode = appMode,
                            onboardingComplete = true,
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) { Text("Get Started 🚀", fontSize = 18.sp) }
        }
        if (step > 1) {
            TextButton(onClick = { step-- }) { Text("Back") }
        }
    }
}
