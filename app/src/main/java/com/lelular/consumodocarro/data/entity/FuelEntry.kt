package com.lelular.consumodocarro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_entries")
data class FuelEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,                    // Unix timestamp em milissegundos
    val odometerKm: Double,            // Quilometragem do odômetro
    val liters: Double,                // Litros abastecidos
    val fuelType: FuelType,            // Tipo de combustível (gasolina ou etanol)
    val pricePerLiter: Double,         // Preço por litro
    val totalPrice: Double,            // Preço total do abastecimento
    val notes: String = ""             // Observações opcionais
)

enum class FuelType {
    GASOLINE,    // Gasolina
    ETHANOL      // Etanol
}
