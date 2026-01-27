package com.lelular.consumodocarro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lelular.consumodocarro.data.database.AppDatabase
import com.lelular.consumodocarro.data.repository.FuelRepository

class FuelViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FuelViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val repository = FuelRepository(database.fuelEntryDao())
            @Suppress("UNCHECKED_CAST")
            return FuelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
