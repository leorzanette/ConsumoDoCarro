package com.lelular.consumodocarro.data.export

import com.lelular.consumodocarro.data.entity.FuelEntry
import com.lelular.consumodocarro.data.entity.FuelEntryHistory
import com.lelular.consumodocarro.data.entity.FuelType

/**
 * Data models for export/import functionality
 * These models provide a stable JSON format that won't change even if database schema changes
 */

data class ExportData(
    val version: Int = 1,
    val exportDate: Long = System.currentTimeMillis(),
    val entries: List<ExportFuelEntry>,
    val history: List<ExportFuelEntryHistory>
)

data class ExportFuelEntry(
    val id: Long,
    val date: Long,
    val odometerKm: Double,
    val liters: Double,
    val fuelType: String,
    val pricePerLiter: Double,
    val totalPrice: Double,
    val notes: String,
    val createdAt: Long,
    val lastModifiedAt: Long
) {
    companion object {
        fun fromEntity(entity: FuelEntry): ExportFuelEntry {
            return ExportFuelEntry(
                id = entity.id,
                date = entity.date,
                odometerKm = entity.odometerKm,
                liters = entity.liters,
                fuelType = entity.fuelType.name,
                pricePerLiter = entity.pricePerLiter,
                totalPrice = entity.totalPrice,
                notes = entity.notes,
                createdAt = entity.createdAt,
                lastModifiedAt = entity.lastModifiedAt
            )
        }
    }

    fun toEntity(): FuelEntry {
        return FuelEntry(
            id = id,
            date = date,
            odometerKm = odometerKm,
            liters = liters,
            fuelType = try {
                FuelType.valueOf(fuelType)
            } catch (e: IllegalArgumentException) {
                FuelType.GASOLINE // Default fallback
            },
            pricePerLiter = pricePerLiter,
            totalPrice = totalPrice,
            notes = notes,
            createdAt = createdAt,
            lastModifiedAt = lastModifiedAt
        )
    }
}

data class ExportFuelEntryHistory(
    val id: Long,
    val entryId: Long,
    val modifiedAt: Long,
    val action: String,
    val fieldName: String,
    val oldValue: String,
    val newValue: String
) {
    companion object {
        fun fromEntity(entity: FuelEntryHistory): ExportFuelEntryHistory {
            return ExportFuelEntryHistory(
                id = entity.id,
                entryId = entity.entryId,
                modifiedAt = entity.modifiedAt,
                action = entity.action,
                fieldName = entity.fieldName,
                oldValue = entity.oldValue,
                newValue = entity.newValue
            )
        }
    }

    fun toEntity(): FuelEntryHistory {
        return FuelEntryHistory(
            id = id,
            entryId = entryId,
            modifiedAt = modifiedAt,
            action = action,
            fieldName = fieldName,
            oldValue = oldValue,
            newValue = newValue
        )
    }
}
