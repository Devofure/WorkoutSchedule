// MainActivityTest.kt
package com.devofure.workoutschedule.ui.main

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.devofure.workoutschedule.MainActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Ignore("This test is not working with datastore")
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val Context.dataStore by preferencesDataStore(name = "workout_data")

    @Before
    fun setUp() {
        // Clear DataStore before each test
        runBlocking {
            composeTestRule.activity.dataStore.edit { it.clear() }
        }

        // Set the content for each test
        composeTestRule.activity.setContent {
            composeTestRule.activity.WorkoutApp()
        }

        // Wait for Compose to be idle
        composeTestRule.waitForIdle()

        // Interact with the AlertDialog to generate a sample schedule
        composeTestRule.onNodeWithText("Generate Sample Schedule")
            .assertExists("Generate Sample Schedule not found")
        composeTestRule.onNodeWithText("Yes").performClick()

        // Wait for the schedule to be generated
        composeTestRule.waitForIdle()
    }

    @After
    fun tearDown() {
        // Clear DataStore after each test
        runBlocking {
            composeTestRule.activity.dataStore.edit { it.clear() }
        }
    }

    @Test
    fun testMainComponentsVisibility() {
        // Check if the "Workout Schedule" title is displayed
        composeTestRule.onNodeWithText("Workout Schedule").assertExists()

        // Check if the "Settings" button is displayed
        composeTestRule.onNodeWithContentDescription("Settings").assertExists()

        // Check if the FAB button is displayed
        composeTestRule.onNodeWithContentDescription("More Options").assertExists()
    }

    @Test
    fun testNavigateToSettingsScreen() {
        // Click on the Settings button
        composeTestRule.onNodeWithContentDescription("Settings").performClick()

        // Wait for the navigation to complete
        composeTestRule.waitForIdle()

        // Check if the "Settings" screen is displayed
        composeTestRule.onNodeWithText("Settings").assertExists()
    }
}
