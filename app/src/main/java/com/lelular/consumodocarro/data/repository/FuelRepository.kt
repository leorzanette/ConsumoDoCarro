package com.lelular.consumodocarro.data.repository

import com.lelular.consumodocarro.data.dao.FuelEntryDao
import com.lelular.consumodocarro.data.entity.FuelEntry
import kotlinx.coroutines.flow.Flow

class FuelRepository(private val fuelEntryDao: FuelEntryDao) {

    val allEntries: Flow<List<FuelEntry>> = fuelEntryDao.getAllEntries()

    suspend fun insert(entry: FuelEntry): Long {
        return fuelEntryDao.insert(entry)
    }

    suspend fun update(entry: FuelEntry) {
        fuelEntryDao.update(entry)
    }

    suspend fun delete(entry: FuelEntry) {
        fuelEntryDao.delete(entry)
    }

    suspend fun getEntryById(id: Long): FuelEntry? {
        return fuelEntryDao.getEntryById(id)
    }

    suspend fun getLastEntry(): FuelEntry? {
        return fuelEntryDao.getLastEntry()
    }

    fun getRecentEntries(limit: Int): Flow<List<FuelEntry>> {
        return fuelEntryDao.getRecentEntries(limit)
    }

    suspend fun deleteAll() {
        fuelEntryDao.deleteAll()
    }

    /**
     * Calcula o consumo médio (km/l) entre dois abastecimentos consecutivos
     * @param currentEntry Abastecimento atual
     * @param previousEntry Abastecimento anterior
     * @return Consumo em km/l ou null se não for possível calcular
     */
    fun calculateConsumption(currentEntry: FuelEntry, previousEntry: FuelEntry): Double? {
        val kmTraveled = currentEntry.odometerKm - previousEntry.odometerKm
        if (kmTraveled <= 0 || previousEntry.liters <= 0) {
            return null
        }
        return kmTraveled / previousEntry.liters
    }
}
