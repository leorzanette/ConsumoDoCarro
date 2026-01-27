package com.lelular.consumodocarro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_entry_history")
data class FuelEntryHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryId: Long,                 // ID da entrada original
    val modifiedAt: Long,              // Timestamp da modificação
    val action: String,                // "CREATED" ou "UPDATED"
    val fieldName: String,             // Nome do campo modificado
    val oldValue: String,              // Valor anterior
    val newValue: String               // Novo valor
)
