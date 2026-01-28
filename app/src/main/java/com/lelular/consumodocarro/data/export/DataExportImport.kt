package com.lelular.consumodocarro.data.export

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lelular.consumodocarro.data.repository.FuelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Handles export and import of fuel entry data
 * Uses JSON format for portability and human readability
 */
class DataExportImport(
    private val repository: FuelRepository,
    private val context: Context
) {

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    /**
     * Exports all data to JSON format
     * @return ExportData object ready to be serialized
     */
    suspend fun exportData(): ExportData = withContext(Dispatchers.IO) {
        // Get all entries
        val entries = repository.getAllEntriesSync()
        val exportEntries = entries.map { ExportFuelEntry.fromEntity(it) }

        // Get all history records
        val history = repository.getAllHistorySync()
        val exportHistory = history.map { ExportFuelEntryHistory.fromEntity(it) }

        ExportData(
            version = 1,
            exportDate = System.currentTimeMillis(),
            entries = exportEntries,
            history = exportHistory
        )
    }

    /**
     * Exports data to a file URI (using Android Storage Access Framework)
     * @param uri The URI where to write the data (obtained from Intent.ACTION_CREATE_DOCUMENT)
     * @return Result with success or error message
     */
    suspend fun exportToFile(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val exportData = exportData()
            val jsonString = gson.toJson(exportData)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(jsonString)
                }
            } ?: return@withContext Result.failure(Exception("Failed to open output stream"))

            Result.success("Data exported successfully: ${exportData.entries.size} entries")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Imports data from JSON string
     * @param jsonString The JSON string to import
     * @param mode Import mode (APPEND or REPLACE)
     * @return Result with success message or error
     */
    suspend fun importData(
        jsonString: String,
        mode: ImportMode = ImportMode.APPEND
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val exportData = gson.fromJson(jsonString, ExportData::class.java)

            // Validate version
            if (exportData.version > 1) {
                return@withContext Result.failure(
                    Exception("Unsupported export version: ${exportData.version}")
                )
            }

            // Clear existing data if REPLACE mode
            if (mode == ImportMode.REPLACE) {
                repository.deleteAll()
                repository.deleteAllHistory()
            }

            // Import entries
            val entries = exportData.entries.map { it.toEntity() }
            for (entry in entries) {
                // For APPEND mode, don't use original IDs (let Room auto-generate)
                if (mode == ImportMode.APPEND) {
                    repository.insert(entry.copy(id = 0))
                } else {
                    // For REPLACE mode, we can try to preserve IDs
                    repository.insertWithId(entry)
                }
            }

            // Import history (only in REPLACE mode to avoid ID conflicts)
            if (mode == ImportMode.REPLACE) {
                val historyEntries = exportData.history.map { it.toEntity() }
                repository.insertHistory(historyEntries)
            }

            val message = when (mode) {
                ImportMode.APPEND -> "Imported ${entries.size} entries (added to existing data)"
                ImportMode.REPLACE -> "Imported ${entries.size} entries (replaced all data)"
            }

            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Imports data from a file URI
     * @param uri The URI to read from (obtained from Intent.ACTION_OPEN_DOCUMENT)
     * @param mode Import mode (APPEND or REPLACE)
     * @return Result with success or error message
     */
    suspend fun importFromFile(uri: Uri, mode: ImportMode = ImportMode.APPEND): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readText()
                    }
                } ?: return@withContext Result.failure(Exception("Failed to open input stream"))

                importData(jsonString, mode)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

/**
 * Import modes
 * APPEND: Add imported data to existing data
 * REPLACE: Delete all existing data and replace with imported data
 */
enum class ImportMode {
    APPEND,
    REPLACE
}
