package com.siptech.siptrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siptech.siptrack.models.Drink
import com.siptech.siptrack.models.DrinkCategory
import com.siptech.siptrack.viewmodels.LogDrinkUiState

@Composable
fun LogDrinkScreen(
    uiState: LogDrinkUiState,
    quickDrinks: List<DrinkCategory>,
    onQuickDrink: (DrinkCategory) -> Drink,
    onDrinkLogged: (Drink) -> Unit,
    onCustomAbvChange: (Float) -> Unit,
    onCustomVolumeChange: (Float) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onCustomDrink: () -> Drink,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Log a Drink", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            TextButton(onClick = onClose) { Text("Cancel") }
        }
        Spacer(Modifier.height(16.dp))

        Text("Quick Log", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp),
        ) {
            items(quickDrinks) { category ->
                QuickDrinkButton(category = category) {
                    onDrinkLogged(onQuickDrink(category))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Text("Custom Drink", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.customName,
            onValueChange = onCustomNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = uiState.customAbv.toString(),
                onValueChange = { it.toFloatOrNull()?.let(onCustomAbvChange) },
                label = { Text("ABV %") },
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = uiState.customVolumeOz.toString(),
                onValueChange = { it.toFloatOrNull()?.let(onCustomVolumeChange) },
                label = { Text("Oz") },
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onDrinkLogged(onCustomDrink()) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add Custom Drink")
        }
    }
}

@Composable
private fun QuickDrinkButton(category: DrinkCategory, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        contentPadding = PaddingValues(4.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (category) {
                    DrinkCategory.BEER -> "🍺"
                    DrinkCategory.WINE -> "🍷"
                    DrinkCategory.SPIRIT -> "🥃"
                    DrinkCategory.COCKTAIL -> "🍹"
                    DrinkCategory.HARD_SELTZER -> "🫧"
                    DrinkCategory.MICRO_TASTE -> "🔬"
                    DrinkCategory.CIDER -> "🍎"
                    DrinkCategory.CUSTOM -> "✏️"
                },
                fontSize = 20.sp,
            )
            Text(category.displayName, style = MaterialTheme.typography.labelSmall)
        }
    }
}
