package com.lelular.consumodocarro.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lelular.consumodocarro.data.entity.FuelEntryHistory
import com.lelular.consumodocarro.ui.viewmodel.FuelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EntryHistoryScreen(
    viewModel: FuelViewModel,
    entryId: Long,
    onNavigateBack: () -> Unit
) {
    val history by viewModel.getHistoryForEntry(entryId).collectAsState(initial = emptyList())

    BackHandler {
        onNavigateBack()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Histórico de Modificações",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (history.isEmpty()) {
                Text(
                    text = "Nenhuma modificação registrada",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history) { historyItem ->
                        HistoryItem(historyItem)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(history: FuelEntryHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (history.action == "CREATED")
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Data e ação
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(Date(history.modifiedAt)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (history.action == "CREATED") "✓ Criação" else "✎ Edição",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (history.action == "UPDATED") {
                Spacer(modifier = Modifier.height(8.dp))

                // Campo modificado
                Text(
                    text = "Campo: ${formatFieldName(history.fieldName)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Valor anterior
                Text(
                    text = "De: ${formatValue(history.fieldName, history.oldValue)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                // Novo valor
                Text(
                    text = "Para: ${formatValue(history.fieldName, history.newValue)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun formatFieldName(fieldName: String): String {
    return when (fieldName) {
        "odometerKm" -> "Quilometragem"
        "liters" -> "Litros"
        "fuelType" -> "Tipo de combustível"
        "pricePerLiter" -> "Preço por litro"
        "totalPrice" -> "Preço total"
        "notes" -> "Observações"
        "date" -> "Data"
        else -> fieldName
    }
}

fun formatValue(fieldName: String, value: String): String {
    return when (fieldName) {
        "odometerKm" -> "$value km"
        "liters" -> "$value L"
        "pricePerLiter", "totalPrice" -> "R$ $value"
        "fuelType" -> when (value) {
            "GASOLINE" -> "Gasolina"
            "ETHANOL" -> "Etanol"
            else -> value
        }
        "date" -> {
            try {
                val date = Date(value.toLong())
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
            } catch (e: Exception) {
                value
            }
        }
        else -> value
    }
}
