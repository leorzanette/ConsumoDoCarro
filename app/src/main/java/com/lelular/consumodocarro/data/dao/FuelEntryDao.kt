package com.lelular.consumodocarro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lelular.consumodocarro.data.entity.FuelEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FuelEntry): Long

    @Update
    suspend fun update(entry: FuelEntry)

    @Delete
    suspend fun delete(entry: FuelEntry)

    @Query("SELECT * FROM fuel_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<FuelEntry>>

    @Query("SELECT * FROM fuel_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): FuelEntry?

    @Query("SELECT * FROM fuel_entries ORDER BY date DESC LIMIT 1")
    suspend fun getLastEntry(): FuelEntry?

    @Query("SELECT * FROM fuel_entries ORDER BY date DESC LIMIT :limit")
    fun getRecentEntries(limit: Int): Flow<List<FuelEntry>>

    @Query("DELETE FROM fuel_entries")
    suspend fun deleteAll()

    @Query("SELECT * FROM fuel_entries ORDER BY date DESC")
    suspend fun getAllEntriesSync(): List<FuelEntry>
}
