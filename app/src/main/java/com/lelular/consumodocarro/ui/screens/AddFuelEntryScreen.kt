package com.lelular.consumodocarro.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lelular.consumodocarro.data.entity.FuelEntry
import com.lelular.consumodocarro.data.entity.FuelType
import com.lelular.consumodocarro.ui.viewmodel.FuelViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddFuelEntryScreen(
    viewModel: FuelViewModel,
    entryId: Long? = null,  // Se fornecido, está editando
    onNavigateBack: (String?) -> Unit = {}
) {
    var odometerKm by remember { mutableStateOf("") }
    var liters by remember { mutableStateOf("") }
    var pricePerLiter by remember { mutableStateOf("") }
    var selectedFuelType by remember { mutableStateOf(FuelType.GASOLINE) }
    var notes by remember { mutableStateOf("") }
    var originalDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isLoading by remember { mutableStateOf(entryId != null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Carrega dados se estiver editando
    androidx.compose.runtime.LaunchedEffect(entryId) {
        if (entryId != null) {
            val entry = viewModel.getEntryById(entryId)
            if (entry != null) {
                odometerKm = entry.odometerKm.toString()
                liters = entry.liters.toString()
                pricePerLiter = entry.pricePerLiter.toString()
                selectedFuelType = entry.fuelType
                notes = entry.notes
                originalDate = entry.date
            }
            isLoading = false
        }
    }

    // Calcula o preço total automaticamente
    val totalPrice = remember(liters, pricePerLiter) {
        val litersValue = liters.toDoubleOrNull() ?: 0.0
        val priceValue = pricePerLiter.toDoubleOrNull() ?: 0.0
        litersValue * priceValue
    }

    // Intercepta o botão voltar do Android
    BackHandler {
        onNavigateBack(null)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = if (entryId == null) "Novo Abastecimento" else "Editar Abastecimento",
                style = MaterialTheme.typography.headlineMedium
            )

            // Data atual (readonly)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Data do abastecimento",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(originalDate)),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Odômetro
            OutlinedTextField(
                value = odometerKm,
                onValueChange = { odometerKm = it },
                label = { Text("Quilometragem (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Litros
            OutlinedTextField(
                value = liters,
                onValueChange = { liters = it },
                label = { Text("Litros abastecidos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Preço por litro
            OutlinedTextField(
                value = pricePerLiter,
                onValueChange = { pricePerLiter = it },
                label = { Text("Preço por litro (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Preço total calculado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Valor total",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "R$ %.2f".format(totalPrice),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            // Tipo de combustível
            Text(
                text = "Tipo de combustível",
                style = MaterialTheme.typography.titleMedium
            )

            Column {
                FuelType.entries.forEach { fuelType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFuelType == fuelType,
                            onClick = { selectedFuelType = fuelType }
                        )
                        Text(
                            text = when (fuelType) {
                                FuelType.GASOLINE -> "Gasolina"
                                FuelType.ETHANOL -> "Etanol"
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Observações
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botões de ação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigateBack(null) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        // Validação
                        val odometerValue = odometerKm.toDoubleOrNull()
                        val litersValue = liters.toDoubleOrNull()
                        val priceValue = pricePerLiter.toDoubleOrNull()

                        when {
                            odometerValue == null || odometerValue <= 0 -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Digite uma quilometragem válida")
                                }
                            }
                            litersValue == null || litersValue <= 0 -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Digite uma quantidade de litros válida")
                                }
                            }
                            priceValue == null || priceValue <= 0 -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Digite um preço válido")
                                }
                            }
                            else -> {
                                if (entryId == null) {
                                    // Criar nova entrada
                                    val entry = FuelEntry(
                                        date = System.currentTimeMillis(),
                                        odometerKm = odometerValue,
                                        liters = litersValue,
                                        fuelType = selectedFuelType,
                                        pricePerLiter = priceValue,
                                        totalPrice = totalPrice,
                                        notes = notes.trim()
                                    )

                                    viewModel.insertEntry(entry)
                                    onNavigateBack("Abastecimento registrado!")
                                } else {
                                    // Editar entrada existente
                                    val entry = FuelEntry(
                                        id = entryId,
                                        date = originalDate,  // Mantém data original
                                        odometerKm = odometerValue,
                                        liters = litersValue,
                                        fuelType = selectedFuelType,
                                        pricePerLiter = priceValue,
                                        totalPrice = totalPrice,
                                        notes = notes.trim()
                                    )

                                    viewModel.updateEntryWithHistory(entry)
                                    onNavigateBack("Abastecimento atualizado!")
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}
