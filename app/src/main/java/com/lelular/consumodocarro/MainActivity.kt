package com.lelular.consumodocarro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.lelular.consumodocarro.ui.screens.AddFuelEntryScreen
import com.lelular.consumodocarro.ui.screens.FuelHistoryScreen
import com.lelular.consumodocarro.ui.theme.ConsumoDoCarroTheme
import com.lelular.consumodocarro.ui.viewmodel.FuelViewModel
import com.lelular.consumodocarro.ui.viewmodel.FuelViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FuelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewModel
        viewModel = ViewModelProvider(
            this,
            FuelViewModelFactory(applicationContext)
        )[FuelViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            ConsumoDoCarroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FuelApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class Screen {
    HISTORY,
    ADD_ENTRY
}

@Composable
fun FuelApp(
    viewModel: FuelViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(Screen.HISTORY) }

    when (currentScreen) {
        Screen.HISTORY -> {
            FuelHistoryScreen(
                viewModel = viewModel,
                onAddClick = { currentScreen = Screen.ADD_ENTRY }
            )
        }

        Screen.ADD_ENTRY -> {
            AddFuelEntryScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.HISTORY }
            )
        }
    }
}