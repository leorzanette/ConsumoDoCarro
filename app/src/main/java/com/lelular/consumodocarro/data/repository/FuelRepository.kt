package com.lelular.consumodocarro.data.repository

import com.lelular.consumodocarro.data.dao.FuelEntryDao
import com.lelular.consumodocarro.data.dao.FuelEntryHistoryDao
import com.lelular.consumodocarro.data.entity.FuelEntry
import com.lelular.consumodocarro.data.entity.FuelEntryHistory
import kotlinx.coroutines.flow.Flow

class FuelRepository(
    private val fuelEntryDao: FuelEntryDao,
    private val historyDao: FuelEntryHistoryDao
) {

    val allEntries: Flow<List<FuelEntry>> = fuelEntryDao.getAllEntries()

    suspend fun insert(entry: FuelEntry): Long {
        val entryId = fuelEntryDao.insert(entry)

        // Registra criação no histórico
        val historyEntries = listOf(
            FuelEntryHistory(
                entryId = entryId,
                modifiedAt = System.currentTimeMillis(),
                action = "CREATED",
                fieldName = "ALL",
                oldValue = "",
                newValue = "Entry created"
            )
        )
        historyDao.insertAll(historyEntries)

        return entryId
    }

    suspend fun updateWithHistory(newEntry: FuelEntry) {
        val oldEntry = fuelEntryDao.getEntryById(newEntry.id)

        if (oldEntry != null) {
            // Cria lista de mudanças
            val changes = mutableListOf<FuelEntryHistory>()
            val timestamp = System.currentTimeMillis()

            if (oldEntry.date != newEntry.date) {
                changes.add(
                    FuelEntryHistory(
                        entryId = newEntry.id,
                        modifiedAt = timestamp,
                        action = "UPDATED",
                        fieldName = "date",
                        oldValue = oldEntry.date.toString(),
                        newValue = newEntry.date.toString()
                    )
                )
            }

            if (oldEntry.odometerKm != newEntry.odometerKm) {
                changes.add(
                    FuelEntryHistory(
                        entryId = newEntry.id,
                        modifiedAt = timestamp,
                        action = "UPDATED",
                        fieldName = "odometerKm",
                        oldValue = "%.2f".format(oldEntry.odometerKm),
                        newValue = "%.2f".format(newEntry.odometerKm)
                    )
                )
            }

            if (oldEntry.liters != newEntry.liters) {
                changes.add(
                    FuelEntryHistory(
                        entryId = newEntry.id,
                        modifiedAt = timestamp,
                        action = "UPDATED",
                        fieldName = "liters",
                        oldValue = "%.2f".format(oldEntry.liters),
                        newValue = "%.2f".format(newEntry.liters)
                    )
                )
            }

            if (oldEntry.fuelType != newEntry.fuelType) {
                changes.add(
                    FuelEntryHistory(
                        entryId = newEntry.id,
                        modifiedAt = timestamp,
                        action = "UPDATED",
                        fieldName = "fuelType",
                        oldValue = oldEntry.fuelType.name,
                        newValue = newEntry.fuelType.name
                    )
                )
            }

            if (oldEntry.pricePerLiter != newEntry.pricePerLiter) {
                changes.add(
                    FuelEntryHistory(
                        entryId = newEntry.id,
                        modifiedAt = timestamp,
                        action = "UPDATED",
                        fieldName = "pricePerLiter",
                        oldValue = "%.2f".format(oldEntry.pricePerLiter),
                        newValue = "%.2f".format(newEntry.pricePerLiter)
                    )
                )
            }

            if (oldEntry.totalPrice != newEntry.totalPrice) {
                changes.add(
                    FuelEntryHistory(
                        entryId = newEntry.id,
                        modifiedAt = timestamp,
                        action = "UPDATED",
                        fieldName = "totalPrice",
                        oldValue = "%.2f".format(oldEntry.totalPrice),
                        newValue = "%.2f".format(newEntry.totalPrice)
                    )
                )
            }

            if (oldEntry.notes != newEntry.notes) {
                changes.add(
                    FuelEntryHistory(
                        entryId = newEntry.id,
                        modifiedAt = timestamp,
                        action = "UPDATED",
                        fieldName = "notes",
                        oldValue = oldEntry.notes,
                        newValue = newEntry.notes
                    )
                )
            }

            // Salva as mudanças no histórico
            if (changes.isNotEmpty()) {
                historyDao.insertAll(changes)
            }

            // Atualiza o registro com novo timestamp de modificação
            val updatedEntry = newEntry.copy(lastModifiedAt = timestamp)
            fuelEntryDao.update(updatedEntry)
        }
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

    /**
     * Obtém o histórico de modificações de uma entrada
     */
    fun getHistoryForEntry(entryId: Long): Flow<List<FuelEntryHistory>> {
        return historyDao.getHistoryForEntry(entryId)
    }

    /**
     * Obtém o histórico de modificações de forma síncrona
     */
    suspend fun getHistoryForEntrySync(entryId: Long): List<FuelEntryHistory> {
        return historyDao.getHistoryForEntrySync(entryId)
    }
}
