package com.devofure.workoutschedule.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntity)

    @Query("SELECT * FROM logged_workouts WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<LogEntity>>
}
