package com.devofure.workoutschedule.data.exercise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Query("SELECT rowid,* " +
            "FROM exercisesFts " +
            "WHERE name MATCH :query " +
            "OR level MATCH :query " +
            "OR equipment MATCH :query " +
            "OR primaryMuscles MATCH :query " +
            "OR secondaryMuscles MATCH :query " +
            "OR category MATCH :query "
    )
    fun searchExercises(query: String): List<ExerciseFtsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)
}
