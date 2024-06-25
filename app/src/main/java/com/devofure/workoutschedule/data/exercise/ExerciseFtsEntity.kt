package com.devofure.workoutschedule.data.exercise

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.PrimaryKey

@Entity(tableName = "exercisesFts")
@Fts4(contentEntity = ExerciseEntity::class, tokenizer = FtsOptions.TOKENIZER_PORTER)
data class ExerciseFtsEntity(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Int,
    val name: String,
    val equipment: String?,
    val primaryMuscles: String,
    val secondaryMuscles: String,
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val rowid: Int,
    val name: String,
    val force: String?,
    val level: String?,
    val mechanic: String?,
    val equipment: String?,
    val primaryMuscles: String,
    val secondaryMuscles: String,
    val instructions: String? = null,
    val category: String? = null,
)

fun ExerciseFtsEntity.toExercise(): Exercise {
    return Exercise(
        rowid = this.rowid,
        name = this.name,
        equipment = this.equipment,
        primaryMuscles = this.primaryMuscles.split(","),
        secondaryMuscles = this.secondaryMuscles.split(","),
    )
}
