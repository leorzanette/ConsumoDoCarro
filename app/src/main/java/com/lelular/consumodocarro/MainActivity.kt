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
import com.lelular.consumodocarro.ui.screens.DataManagementScreen
import com.lelular.consumodocarro.ui.screens.EntryHistoryScreen
import com.lelular.consumodocarro.ui.screens.FuelHistoryScreen
import com.lelular.consumodocarro.ui.theme.ConsumoDoCarroTheme
import com.lelular.consumodocarro.ui.viewmodel.FuelViewModel
import com.lelular.consumodocarro.ui.viewmodel.FuelViewModelFactory
import com.google.android.gms.ads.MobileAds
import com.lelular.consumodocarro.ui.ads.AdBanner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FuelViewModel
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        // Inicializa o Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Inicializa o AdMob
        MobileAds.initialize(this)
        // Inicializa o ViewModel
        viewModel = ViewModelProvider(
            this,
            FuelViewModelFactory(applicationContext)
        )[FuelViewModel::class.java]

        // Inicializa funcionalidade de Export/Import
        viewModel.initializeExportImport(applicationContext)

        enableEdgeToEdge()
        setContent {
            ConsumoDoCarroTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AdBanner() // 👈 O ANÚNCIO ENTRA AQUI
                    },
                    topBar = {
                        AdBanner() // 👈 O ANÚNCIO ENTRA AQUI
                }) { innerPadding ->
                    FuelApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

sealed class Screen {
    data class History(val successMessage: String? = null) : Screen()
    data class AddEntry(val entryId: Long? = null) : Screen()
    data class EntryHistory(val entryId: Long) : Screen()
    data object DataManagement : Screen()
}

@Composable
fun FuelApp(
    viewModel: FuelViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.History()) }

    when (val screen = currentScreen) {
        is Screen.History -> {
            FuelHistoryScreen(
                viewModel = viewModel,
                onAddClick = { currentScreen = Screen.AddEntry() },
                onEditClick = { entryId -> currentScreen = Screen.AddEntry(entryId) },
                onHistoryClick = { entryId -> currentScreen = Screen.EntryHistory(entryId) },
                onDataManagementClick = { currentScreen = Screen.DataManagement },
                successMessage = screen.successMessage
            )
        }

        is Screen.AddEntry -> {
            AddFuelEntryScreen(
                viewModel = viewModel,
                entryId = screen.entryId,
                onNavigateBack = { message -> currentScreen = Screen.History(message) }
            )
        }

        is Screen.EntryHistory -> {
            EntryHistoryScreen(
                viewModel = viewModel,
                entryId = screen.entryId,
                onNavigateBack = { currentScreen = Screen.History() }
            )
        }

        is Screen.DataManagement -> {
            DataManagementScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.History() }
            )
        }
    }
}