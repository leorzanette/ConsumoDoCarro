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
    exportSchema = true  // Changed to true to track schema changes
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
                    // Add migrations here when schema changes
                    // .addMigrations(MIGRATION_2_3, MIGRATION_3_4, etc.)

                    // For now, using destructive migration during beta
                    // In production, replace with proper migrations
                    .fallbackToDestructiveMigration()

                    // Optional: Enable this for production to catch migration issues
                    // .fallbackToDestructiveMigrationOnDowngrade()

                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Production-ready database configuration
         * Use this when ready to deploy with proper migrations
         */
        fun getDatabaseWithMigrations(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "consumo_carro_database"
                )
                    // Add all migrations in sequence
                    // .addMigrations(MIGRATION_2_3)
                    // .addMigrations(MIGRATION_3_4)
                    // .addMigrations(MIGRATION_4_5)

                    // Only use destructive migration on downgrade
                    .fallbackToDestructiveMigrationOnDowngrade()

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
