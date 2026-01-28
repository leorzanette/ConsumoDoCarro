package com.lelular.consumodocarro.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database migrations
 * These handle schema changes between versions
 */

/**
 * Migration from version 2 to 3
 * Example: Adds a nullable 'isFullTank' column to track if the tank was filled completely
 * This is a simple migration that demonstrates the pattern
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new nullable column with default value false
        database.execSQL(
            "ALTER TABLE fuel_entries ADD COLUMN isFullTank INTEGER NOT NULL DEFAULT 0"
        )
    }
}

/**
 * Future migration example: Version 3 to 4
 * This shows how to add an index for performance
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create index on date column for faster queries
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_fuel_entries_date ON fuel_entries(date)"
        )
    }
}

/**
 * Complex migration example: Version 4 to 5
 * This shows how to handle table restructuring
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Example: Split notes into notes and location
        // Step 1: Create new table with updated schema
        database.execSQL(
            """
            CREATE TABLE fuel_entries_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                odometerKm REAL NOT NULL,
                liters REAL NOT NULL,
                fuelType TEXT NOT NULL,
                pricePerLiter REAL NOT NULL,
                totalPrice REAL NOT NULL,
                notes TEXT NOT NULL DEFAULT '',
                location TEXT NOT NULL DEFAULT '',
                isFullTank INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL,
                lastModifiedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Step 2: Copy data from old table to new table
        database.execSQL(
            """
            INSERT INTO fuel_entries_new
            (id, date, odometerKm, liters, fuelType, pricePerLiter, totalPrice,
             notes, location, isFullTank, createdAt, lastModifiedAt)
            SELECT
                id, date, odometerKm, liters, fuelType, pricePerLiter, totalPrice,
                notes, '', isFullTank, createdAt, lastModifiedAt
            FROM fuel_entries
            """.trimIndent()
        )

        // Step 3: Remove old table
        database.execSQL("DROP TABLE fuel_entries")

        // Step 4: Rename new table to original name
        database.execSQL("ALTER TABLE fuel_entries_new RENAME TO fuel_entries")

        // Step 5: Recreate indices if any
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_fuel_entries_date ON fuel_entries(date)"
        )
    }
}

/**
 * Notes on migration best practices:
 *
 * 1. SIMPLE MIGRATIONS (adding nullable columns, adding indices):
 *    - Use ALTER TABLE ADD COLUMN
 *    - Always provide DEFAULT values
 *    - Test thoroughly
 *
 * 2. COMPLEX MIGRATIONS (changing column types, splitting/merging columns):
 *    - Create new table with correct schema
 *    - Copy data from old to new table with transformations
 *    - Drop old table
 *    - Rename new table
 *    - Recreate indices and foreign keys
 *
 * 3. TESTING:
 *    - Test migration on devices with old schema
 *    - Verify data integrity after migration
 *    - Test both upgrade and fresh install paths
 *
 * 4. EXPORT/IMPORT AS SAFETY NET:
 *    - For risky migrations, prompt users to export first
 *    - Provide automatic backup before migration
 *    - Allow import if migration fails
 *
 * 5. VERSION STRATEGY:
 *    - Increment version for any schema change
 *    - Document each migration in this file
 *    - Keep migrations in source control
 *    - Never modify existing migrations after release
 */
