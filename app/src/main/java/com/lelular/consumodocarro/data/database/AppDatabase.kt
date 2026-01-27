package com.lelular.consumodocarro.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.lelular.consumodocarro.data.dao.FuelEntryDao
import com.lelular.consumodocarro.data.dao.FuelEntryHistoryDao
import com.lelular.consumodocarro.data.entity.FuelEntry
import com.lelular.consumodocarro.data.entity.FuelEntryHistory
import com.lelular.consumodocarro.data.entity.FuelType

@Database(
    entities = [FuelEntry::class, FuelEntryHistory::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fuelEntryDao(): FuelEntryDao
    abstract fun fuelEntryHistoryDao(): FuelEntryHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "consumo_carro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromFuelType(value: FuelType): String {
        return value.name
    }

    @TypeConverter
    fun toFuelType(value: String): FuelType {
        return FuelType.valueOf(value)
    }
}
