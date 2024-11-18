package com.devofure.workoutschedule.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.workoutDataStore: DataStore<Preferences> by preferencesDataStore(name = "workout_data")

class WorkoutDataStoreManager(private val context: Context) {

    companion object {
        val IS_FIRST_LAUNCH = stringPreferencesKey("isFirstLaunch")
        val USER_SCHEDULE = stringPreferencesKey("userSchedule")
        fun NICKNAME_KEY(dayIndex: Int) = stringPreferencesKey("nicknames_$dayIndex")
    }

    val isFirstLaunch: Flow<String?> = context.workoutDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_FIRST_LAUNCH]
        }

    fun getNickname(dayIndex: Int): Flow<String?> = context.workoutDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NICKNAME_KEY(dayIndex)]
        }

    suspend fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        context.workoutDataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = isFirstLaunch.toString()
        }
    }

    suspend fun setNickname(dayIndex: Int, nickname: String) {
        context.workoutDataStore.edit { preferences ->
            preferences[NICKNAME_KEY(dayIndex)] = nickname
        }
    }

    suspend fun setUserSchedule(userSchedule: String) {
        context.workoutDataStore.edit { preferences ->
            preferences[USER_SCHEDULE] = userSchedule
        }
    }
}