package com.lelular.consumodocarro.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lelular.consumodocarro.R
import com.lelular.consumodocarro.data.export.ImportMode
import com.lelular.consumodocarro.ui.viewmodel.FuelViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagementScreen(
    viewModel: FuelViewModel,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }

    // Export launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                isLoading = true
                val result = viewModel.exportData(it)
                isLoading = false

                result.fold(
                    onSuccess = { message ->
                        snackbarHostState.showSnackbar(message)
                    },
                    onFailure = { error ->
                        snackbarHostState.showSnackbar(
                            "Erro ao exportar: ${error.message}"
                        )
                    }
                )
            }
        }
    }

    // Import launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingImportUri = it
            showImportDialog = true
        }
    }

    // Import mode selection dialog
    if (showImportDialog) {
        ImportModeDialog(
            onDismiss = {
                showImportDialog = false
                pendingImportUri = null
            },
            onConfirm = { mode ->
                showImportDialog = false
                pendingImportUri?.let { uri ->
                    scope.launch {
                        isLoading = true
                        val result = viewModel.importData(uri, mode)
                        isLoading = false

                        result.fold(
                            onSuccess = { message ->
                                snackbarHostState.showSnackbar(message)
                            },
                            onFailure = { error ->
                                snackbarHostState.showSnackbar(
                                    "Erro ao importar: ${error.message}"
                                )
                            }
                        )
                    }
                }
                pendingImportUri = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Dados") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            }

            Text(
                text = "Exportar e Importar Dados",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Gerencie seus dados de abastecimento com segurança",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Export Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.exporticon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Exportar Dados",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Faça backup de todos os seus abastecimentos em formato JSON. " +
                                "Recomendado antes de atualizações importantes.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            val fileName = "consumo_carro_backup_${
                                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                                    .format(Date())
                            }.json"
                            exportLauncher.launch(fileName)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("Exportar Agora")
                    }
                }
            }

            // Import Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.importicon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Importar Dados",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Restaure seus dados de um backup anterior. " +
                                "Você pode adicionar aos dados existentes ou substituí-los.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(
                        onClick = {
                            importLauncher.launch(arrayOf("application/json"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("Importar Arquivo")
                    }
                }
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Dicas Importantes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Exporte seus dados regularmente como backup",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Os arquivos são salvos em formato JSON legível",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Use o modo ADICIONAR para manter dados existentes",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Use o modo SUBSTITUIR apenas para restauração completa",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ImportModeDialog(
    onDismiss: () -> Unit,
    onConfirm: (ImportMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Modo de Importação")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Como deseja importar os dados?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ADICIONAR: Mantém os dados existentes e adiciona os novos",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "SUBSTITUIR: Remove todos os dados atuais e importa os novos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onConfirm(ImportMode.APPEND) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar aos Dados Existentes")
                }
                OutlinedButton(
                    onClick = { onConfirm(ImportMode.REPLACE) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Substituir Todos os Dados")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
