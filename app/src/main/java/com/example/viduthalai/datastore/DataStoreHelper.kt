package com.example.viduthalai.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object DataStoreHelper {
    // Keys
    private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
    private val RESTORE_KEY = booleanPreferencesKey("restore")
    private val TOTAL_TIME_KEY = longPreferencesKey("total_time_millis")
    private val REMAINING_TIME_KEY = longPreferencesKey("remaining_time_millis")

    // First Launch Operations
    suspend fun isFirstLaunch(context: Context): Boolean =
        context.dataStore.data
            .map { it[FIRST_LAUNCH_KEY] ?: true }
            .first()

    suspend fun isRestore(context: Context): Boolean =
        context.dataStore.data
            .map { it[RESTORE_KEY] ?: true }
            .first()

    suspend fun setFirstLaunchCompleted(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[FIRST_LAUNCH_KEY] = false
        }
    }

    // Timer State Operations
    suspend fun saveTimerState(
        context: Context,
        totalTime: Long,
        remainingTime: Long
    ) {
        context.dataStore.edit { prefs ->
            prefs[TOTAL_TIME_KEY] = totalTime
            prefs[REMAINING_TIME_KEY] = remainingTime
        }
    }

    suspend fun setRestoreState(context: Context, restore: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[RESTORE_KEY] = restore
        }
    }

    suspend fun clearTimerState(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[TOTAL_TIME_KEY] = 0L
            prefs[REMAINING_TIME_KEY] = 0L
        }
    }


    suspend fun getTimerState(context: Context): Pair<Long, Long> =
        context.dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { prefs ->
                val total = prefs[TOTAL_TIME_KEY] ?: 0L
                val remaining = prefs[REMAINING_TIME_KEY] ?: total
                total to remaining
            }
            .first()

    // Blocking version for non-coroutine contexts
    fun getTimerStateBlocking(context: Context): Pair<Long, Long> {
        return runBlocking {
            val prefs = context.dataStore.data.first()
            val total = prefs[TOTAL_TIME_KEY] ?: 0L
            val remaining = prefs[REMAINING_TIME_KEY] ?: total
            total to remaining
        }
    }
}