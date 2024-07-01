package com.devofure.workoutschedule.data

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.devofure.workoutschedule.ui.settings.ThemeType
import com.devofure.workoutschedule.ui.theme.Colors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class SettingsDataStoreManager(private val context: Context) {

    companion object {
        val REMINDER_HOUR = intPreferencesKey("reminderHour")
        val REMINDER_MINUTE = intPreferencesKey("reminderMinute")
        val THEME = stringPreferencesKey("theme")
        val PRIMARY_COLOR = intPreferencesKey("primaryColor")
        val FIRST_DAY_OF_WEEK = stringPreferencesKey("firstDayOfWeek")
        val DAY_NAMING_PREFERENCE = stringPreferencesKey("dayNamingPreference")
    }

    val reminderHour: Flow<Int> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[REMINDER_HOUR] ?: 0
        }

    val reminderMinute: Flow<Int> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[REMINDER_MINUTE] ?: 0
        }

    val theme: Flow<String> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[THEME] ?: ThemeType.SYSTEM.name
        }

    val primaryColor: Flow<Int> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PRIMARY_COLOR] ?: Colors.DefaultThemeColor.toArgb()
        }

    val firstDayOfWeek: Flow<String> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[FIRST_DAY_OF_WEEK] ?: FirstDayOfWeek.MONDAY.name
        }

    val dayNamingPreference: Flow<String> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DAY_NAMING_PREFERENCE] ?: DayOfWeek.DayNamingPreference.DAY_NUMBERS.name
        }

    suspend fun setReminderHour(hour: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[REMINDER_HOUR] = hour
        }
    }

    suspend fun setReminderMinute(minute: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[REMINDER_MINUTE] = minute
        }
    }

    suspend fun setTheme(theme: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[THEME] = theme
        }
    }

    suspend fun setPrimaryColor(color: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[PRIMARY_COLOR] = color
        }
    }

    suspend fun setFirstDayOfWeek(firstDay: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[FIRST_DAY_OF_WEEK] = firstDay
        }
    }

    suspend fun setDayNamingPreference(preference: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[DAY_NAMING_PREFERENCE] = preference
        }
    }
}
