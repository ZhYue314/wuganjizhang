package com.seamless.bookkeeper.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val RECORD_MODE = stringPreferencesKey("record_mode")
        val DARK_MODE = stringPreferencesKey("dark_mode")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DATABASE_PASSWORD_SET = booleanPreferencesKey("database_password_set")
    }

    val recordMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[RECORD_MODE] ?: "AUTO"
    }

    val darkMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE] ?: "SYSTEM"
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETED] ?: false
    }

    val isDatabasePasswordSet: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DATABASE_PASSWORD_SET] ?: false
    }

    suspend fun setRecordMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[RECORD_MODE] = mode }
    }

    suspend fun getRecordMode(): String {
        return context.dataStore.data.first()[RECORD_MODE] ?: "AUTO"
    }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[DARK_MODE] = mode }
    }

    suspend fun getDarkMode(): String {
        return context.dataStore.data.first()[DARK_MODE] ?: "SYSTEM"
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETED] = completed }
    }
}
