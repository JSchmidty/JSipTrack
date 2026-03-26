package com.siptech.siptrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.siptech.siptrack.models.DrinkSession
import com.siptech.siptrack.viewmodels.HistoryUiState

@Composable
fun HistoryScreen(uiState: HistoryUiState, onSessionClick: (DrinkSession) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("History", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // Weekly stats summary
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.weeklyDrinkCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("This Week", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.drinkFreeDays.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("AF Days", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${uiState.currentStreak}🔥", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Streak", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🍃", style = MaterialTheme.typography.displayMedium)
                    Text("No sessions yet", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.sessions) { session ->
                    SessionCard(session = session, onClick = { onSessionClick(session) })
                }
            }
        }
    }
}

@Composable
private fun SessionCard(session: DrinkSession, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = session.startTime.toString().take(10),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${session.totalDrinks} drinks · %.1f std".format(session.totalStandardDrinks),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
            Text(
                text = if (session.isActive) "ACTIVE" else "✓",
                color = if (session.isActive) MaterialTheme.colorScheme.secondary else Color.Gray,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
