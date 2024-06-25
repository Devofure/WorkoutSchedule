package com.devofure.workoutschedule.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.devofure.workoutschedule.data.AppDatabase
import com.devofure.workoutschedule.data.Workout
import com.devofure.workoutschedule.data.exercise.ExerciseRepository
import com.devofure.workoutschedule.data.log.LogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val logDao = database.logDao()
    private val exerciseRepository =
        ExerciseRepository(application.applicationContext, database.exerciseDao(), database)

    private var cachedLogDatesForMonth = mutableMapOf<String, List<LocalDate>>()

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
}
