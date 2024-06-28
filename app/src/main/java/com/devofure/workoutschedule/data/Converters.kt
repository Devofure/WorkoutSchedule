package com.devofure.workoutschedule.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromSetDetailsList(value: List<SetDetails>?): String? {
        if (value == null) return null
        val gson = Gson()
        val type = object : TypeToken<List<SetDetails>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toSetDetailsList(value: String?): List<SetDetails>? {
        if (value == null) return null
        val gson = Gson()
        val type = object : TypeToken<List<SetDetails>>() {}.type
        return gson.fromJson(value, type)
    }
}