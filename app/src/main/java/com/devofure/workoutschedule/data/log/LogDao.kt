package com.devofure.workoutschedule.data.log

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntity)

    @Query("SELECT * FROM logged_workouts WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<LogEntity>>

    @Query("SELECT DISTINCT strftime('%Y-%m-%d', date) FROM logged_workouts WHERE strftime('%Y-%m', date) = :yearMonth")
    fun getLogDatesForMonth(yearMonth: String): Flow<List<String>>

    @Query("DELETE FROM logged_workouts WHERE id = :logId")
    suspend fun deleteLog(logId: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLog(log: LogEntity)
}
