package com.siptech.siptrack.wear.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.siptech.siptrack.engine.BACCalculator
import com.siptech.siptrack.models.UserProfile

/**
 * Wear OS (Samsung Galaxy Watch) companion app.
 * Shows BAC, drive-safe status, and quick log button.
 * Connects to phone via DataLayer API (implementation pending).
 */
@Composable
fun WearSipTrackApp() {
    // Placeholder state — real implementation uses WearDataLayerRepository
    val bac by remember { mutableStateOf(0.0) }
    val profile = UserProfile()
    val status = BACCalculator.getBACStatus(bac)

    val bacColor = when {
        bac <= 0.0 -> Color(0xFF2ECC71)
        bac < 0.04 -> Color(0xFF2ECC71)
        bac < profile.driveLimitBac -> Color(0xFFF39C12)
        else -> Color(0xFFE74C3C)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "BAC",
            style = MaterialTheme.typography.caption2,
            color = Color.Gray,
        )
        Text(
            text = "%.3f".format(bac),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = bacColor,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (bac < profile.driveLimitBac) "✅ OK" else "🚗 Don't Drive",
            style = MaterialTheme.typography.caption1,
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { /* Send quick log intent to phone */ },
            modifier = Modifier.fillMaxWidth(0.8f),
        ) {
            Text("+ Log Drink", fontSize = 12.sp)
        }
    }
}
