package com.devofure.workoutschedule.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LogDao {
    @Insert
    suspend fun insertLog(log: LogEntity)

    @Query("SELECT * FROM logged_workouts WHERE date = :date")
    suspend fun getLogsByDate(date: String): List<LogEntity>
}