package com.lelular.consumodocarro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelular.consumodocarro.data.entity.FuelEntry
import com.lelular.consumodocarro.data.repository.FuelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface FuelUiState {
    data object Loading : FuelUiState
    data class Success(
        val entries: List<FuelEntry>,
        val lastConsumption: Double? = null
    ) : FuelUiState
    data class Error(val message: String) : FuelUiState
}

class FuelViewModel(private val repository: FuelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<FuelUiState>(FuelUiState.Loading)
    val uiState: StateFlow<FuelUiState> = _uiState.asStateFlow()

    val allEntries: StateFlow<List<FuelEntry>> = repository.allEntries
        .catch { exception ->
            _uiState.value = FuelUiState.Error(exception.message ?: "Erro desconhecido")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            repository.allEntries
                .catch { exception ->
                    _uiState.value = FuelUiState.Error(exception.message ?: "Erro ao carregar dados")
                }
                .collect { entries ->
                    val lastConsumption = calculateLastConsumption(entries)
                    _uiState.value = FuelUiState.Success(
                        entries = entries,
                        lastConsumption = lastConsumption
                    )
                }
        }
    }

    fun insertEntry(entry: FuelEntry) {
        viewModelScope.launch {
            try {
                repository.insert(entry)
            } catch (e: Exception) {
                _uiState.value = FuelUiState.Error("Erro ao inserir: ${e.message}")
            }
        }
    }

    fun updateEntry(entry: FuelEntry) {
        viewModelScope.launch {
            try {
                repository.update(entry)
            } catch (e: Exception) {
                _uiState.value = FuelUiState.Error("Erro ao atualizar: ${e.message}")
            }
        }
    }

    fun deleteEntry(entry: FuelEntry) {
        viewModelScope.launch {
            try {
                repository.delete(entry)
            } catch (e: Exception) {
                _uiState.value = FuelUiState.Error("Erro ao deletar: ${e.message}")
            }
        }
    }

    fun deleteAllEntries() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
            } catch (e: Exception) {
                _uiState.value = FuelUiState.Error("Erro ao limpar dados: ${e.message}")
            }
        }
    }

    private suspend fun calculateLastConsumption(entries: List<FuelEntry>): Double? {
        if (entries.size < 2) return null

        val sortedEntries = entries.sortedByDescending { it.date }
        val current = sortedEntries[0]
        val previous = sortedEntries[1]

        return repository.calculateConsumption(current, previous)
    }

    // Método público para calcular consumo entre dois abastecimentos
    fun calculateConsumption(currentEntry: FuelEntry, previousEntry: FuelEntry): Double? {
        return repository.calculateConsumption(currentEntry, previousEntry)
    }
}
