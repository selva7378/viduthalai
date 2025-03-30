package com.example.viduthalai.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "settings")

object DataStoreHelper {
    private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")

    suspend fun isFirstLaunch(context: Context): Boolean {
        return context.dataStore.data.first()[FIRST_LAUNCH_KEY] ?: true
    }

    suspend fun setFirstLaunchCompleted(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = false
        }
    }

    // For non-suspending contexts (like in Activity)
    fun isFirstLaunchBlocking(context: Context): Boolean {
        return runBlocking {
            context.dataStore.data.first()[FIRST_LAUNCH_KEY] ?: true
        }
    }
}