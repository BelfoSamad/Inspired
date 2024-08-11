package com.samadtch.inspired.data.datasources.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map

class AppPreferencesDataSource(private val dataStore: DataStore<Preferences>) {
    companion object {
        val FIRST_OPEN_KEY = booleanPreferencesKey("first_open")
        val LOGGED_IN_KEY = booleanPreferencesKey("logged_in")
    }

    val isFirstOpen = dataStore.data.map { prefs -> prefs[FIRST_OPEN_KEY] ?: true }
    val isLoggedIn = dataStore.data.map { prefs -> prefs[LOGGED_IN_KEY] ?: false }

    suspend fun setFirstOpen() = dataStore.edit { settings ->
        settings[FIRST_OPEN_KEY] = false
    }

    suspend fun setLoggedIn(loggedIn: Boolean) = dataStore.edit { settings ->
        settings[FIRST_OPEN_KEY] = loggedIn
    }
}