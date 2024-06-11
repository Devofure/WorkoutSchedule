package com.devofure.workoutschedule.ui

import android.app.Application
import android.content.res.AssetManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.devofure.workoutschedule.data.ExerciseRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class WorkoutViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: WorkoutViewModel

    @MockK
    private lateinit var repository: ExerciseRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        // Mock the context and its methods
        val context = mockk<Application>(relaxed = true)
        val assetManager = mockk<AssetManager>(relaxed = true)
        every { context.assets } returns assetManager

        // Mock the open method to return an empty InputStream
        val jsonStream: InputStream = ByteArrayInputStream("{}".toByteArray())
        every { assetManager.open(any()) } returns jsonStream

        // Initialize the ExerciseRepository with the mocked context
        repository = ExerciseRepository(context)

        // Initialize the ViewModel with the mocked context
        viewModel = WorkoutViewModel(context)
    }

    @Test
    fun testGenerateSampleSchedule() = runTest {
        viewModel.generateSampleSchedule()
        val workouts = viewModel.workouts.value
        assertTrue(workouts.isNotEmpty())
    }

    @Test
    fun testLoadUserSchedule() = runTest {
        viewModel.loadUserSchedule()
        val workouts = viewModel.workouts.value
        assertNotNull(workouts)
    }
}
