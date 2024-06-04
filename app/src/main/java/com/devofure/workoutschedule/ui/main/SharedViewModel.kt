package com.devofure.workoutschedule.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devofure.workoutschedule.data.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val _selectedWorkout = MutableStateFlow<Workout?>(null)
    val selectedWorkout: StateFlow<Workout?> = _selectedWorkout.asStateFlow()

    fun selectWorkout(workout: Workout) {
        viewModelScope.launch {
            _selectedWorkout.emit(workout)
        }
    }

    fun clearSelectedWorkout() {
        viewModelScope.launch {
            _selectedWorkout.emit(null)
        }
    }
}
