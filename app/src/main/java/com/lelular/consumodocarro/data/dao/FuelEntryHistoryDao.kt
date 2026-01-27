package com.lelular.consumodocarro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lelular.consumodocarro.data.entity.FuelEntryHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelEntryHistoryDao {

    @Insert
    suspend fun insert(history: FuelEntryHistory)

    @Insert
    suspend fun insertAll(historyList: List<FuelEntryHistory>)

    @Query("SELECT * FROM fuel_entry_history WHERE entryId = :entryId ORDER BY modifiedAt DESC")
    fun getHistoryForEntry(entryId: Long): Flow<List<FuelEntryHistory>>

    @Query("SELECT * FROM fuel_entry_history WHERE entryId = :entryId ORDER BY modifiedAt DESC")
    suspend fun getHistoryForEntrySync(entryId: Long): List<FuelEntryHistory>

    @Query("DELETE FROM fuel_entry_history WHERE entryId = :entryId")
    suspend fun deleteHistoryForEntry(entryId: Long)
}
