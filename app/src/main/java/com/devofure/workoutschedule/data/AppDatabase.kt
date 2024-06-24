package com.devofure.workoutschedule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devofure.workoutschedule.data.exercise.ExerciseDao
import com.devofure.workoutschedule.data.exercise.ExerciseEntity
import com.devofure.workoutschedule.data.exercise.ExerciseFtsEntity
import com.devofure.workoutschedule.data.log.LogDao
import com.devofure.workoutschedule.data.log.LogEntity

@Database(
    entities = [LogEntity::class, ExerciseEntity::class, ExerciseFtsEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
