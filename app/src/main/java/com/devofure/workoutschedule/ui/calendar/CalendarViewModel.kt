package com.devofure.workoutschedule.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.AppDatabase
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.data.exercise.ExerciseRepository
import com.devofure.workoutschedule.data.log.LogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val logDao = database.logDao()
    private val exerciseRepository =
        ExerciseRepository(application.applicationContext, database.exerciseDao(), database)

    private var cachedLogDatesForMonth = mutableMapOf<String, List<LocalDate>>()

    private val _deleteEvent = MutableStateFlow<LogEntity?>(null)
    val deleteEvent: StateFlow<LogEntity?> = _deleteEvent

    fun getLogsForDate(date: LocalDate): Flow<List<LogEntity>> {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = date.format(dateFormat)
        return logDao.getLogsForDate(formattedDate)
    }

    fun getLogDatesForMonth(year: Int, month: Int): Flow<List<LocalDate>> {
        val yearMonth = String.format("%04d-%02d", year, month)
        val cachedDates = cachedLogDatesForMonth[yearMonth]
        return if (cachedDates != null) {
            flowOf(cachedDates)
        } else {
            logDao.getLogDatesForMonth(yearMonth).map { dates ->
                dates.map { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
            }.onEach { dates ->
                cachedLogDatesForMonth[yearMonth] = dates
            }
        }
    }

    fun getWorkoutByName(log: LogEntity): Workout? {
        val exercise = exerciseRepository.getExerciseByName(log.exerciseName) ?: return null
        return Workout(
            id = log.id,
            exercise = exercise,
            repsList = log.repsList,
        )
    }

    fun deleteLog(log: LogEntity) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            logDao.deleteLog(log.id)
        }
        _deleteEvent.value = log
    }

    fun undoDeleteLog(log: LogEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                logDao.insertLog(log)
            }
            _deleteEvent.value = null
        }
    }
}
