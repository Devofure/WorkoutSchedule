// SettingsScreenTest.kt
package com.devofure.workoutschedule.ui.settings

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.devofure.workoutschedule.MainActivity
import com.devofure.workoutschedule.ui.Navigate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun grantPermissions() {
        // Grant POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val uiAutomation = instrumentation.uiAutomation
            uiAutomation.executeShellCommand(
                "pm grant ${ApplicationProvider.getApplicationContext<Context>().packageName} ${Manifest.permission.POST_NOTIFICATIONS}"
            ).close()
        }

        // Grant SCHEDULE_EXACT_ALARM permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val uiAutomation = instrumentation.uiAutomation
            uiAutomation.executeShellCommand(
                "appops set ${ApplicationProvider.getApplicationContext<Context>().packageName} SCHEDULE_EXACT_ALARM allow"
            ).close()
        }
    }

    @Test
    fun testSettingsScreenDisplaysCorrectly() {
        val settingsViewModel = SettingsViewModel(composeTestRule.activity.application)
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.setContent {
                val navigate = Navigate(rememberNavController())
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    navigate = navigate,
                    currentTheme = settingsViewModel.theme.collectAsState().value,
                    onThemeChange = { settingsViewModel.setTheme(it) },
                    onPrimaryColorChange = { settingsViewModel.setPrimaryColor(it) }
                )
            }
        }

        // Check if the Settings screen is displayed
        composeTestRule.onNodeWithText("Settings").assertExists()
        composeTestRule.onNodeWithText("Workout Reminders").assertExists()
        composeTestRule.onNodeWithText("Delete Schedule").assertExists()
        composeTestRule.onNodeWithText("Theme Mode").assertExists()
        composeTestRule.onNodeWithText("Theme Light color").assertExists()
        composeTestRule.onNodeWithText("First Day of the Week").assertExists()
    }

    @Test
    fun testThemeChange() {
        val settingsViewModel = SettingsViewModel(composeTestRule.activity.application)
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.setContent {
                val navigate = Navigate(rememberNavController())
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    navigate = navigate,
                    currentTheme = settingsViewModel.theme.collectAsState().value,
                    onThemeChange = { settingsViewModel.setTheme(it) },
                    onPrimaryColorChange = { settingsViewModel.setPrimaryColor(it) }
                )
            }
        }

        // Open the theme settings dialog
        composeTestRule.onNodeWithText("Theme Mode").performClick()

        // Select Light theme
        composeTestRule.onNodeWithText("Light").performClick()
        composeTestRule.onNodeWithText("Close").assertExists().performClick()

        // Verify the theme is changed to Light
        assert(settingsViewModel.theme.value == ThemeType.LIGHT)

        // Open the theme settings dialog again
        composeTestRule.onNodeWithText("Theme Mode").performClick()

        // Select Dark theme
        composeTestRule.onNodeWithText("Dark").performClick()
        composeTestRule.onNodeWithText("Close").assertExists().performClick()

        // Verify the theme is changed to Dark
        assert(settingsViewModel.theme.value == ThemeType.DARK)

        // Open the theme settings dialog again
        composeTestRule.onNodeWithText("Theme Mode").performClick()

        // Select System Default theme
        composeTestRule.onNodeWithText("System Default").performClick()
        composeTestRule.onNodeWithText("Close").assertExists().performClick()

        // Verify the theme is changed to System Default
        assert(settingsViewModel.theme.value == ThemeType.SYSTEM)
    }

    @Test
    fun testSetReminder() {
        val settingsViewModel = SettingsViewModel(composeTestRule.activity.application)

        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.setContent {
                val navigate = Navigate(rememberNavController())
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    navigate = navigate,
                    currentTheme = settingsViewModel.theme.collectAsState().value,
                    onThemeChange = { settingsViewModel.setTheme(it) },
                    onPrimaryColorChange = { settingsViewModel.setPrimaryColor(it) }
                )
            }
        }

        // Open the Reminder Setup Dialog
        composeTestRule.onNodeWithText("Workout Reminders").performClick()

        // Ensure the dialog is displayed
        composeTestRule.onNodeWithText("Pick Time").assertExists()

        // Pick a time (this requires UI interaction, so we'll just assert the dialog appears)
        composeTestRule.onNodeWithText("Pick Time").performClick()

        // Ensure the time picker dialog is displayed (you might need to adjust this part depending on your implementation)
        composeTestRule.waitForIdle()

        // Close the dialog
        composeTestRule.onNodeWithText("Save").performClick()

        // Verify the main screen is displayed again
        composeTestRule.onNodeWithText("Workout Reminders").assertExists()
    }

    @Test
    fun testDeleteSchedule() {
        val settingsViewModel = SettingsViewModel(composeTestRule.activity.application)

        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.setContent {
                val navigate = Navigate(rememberNavController())
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    navigate = navigate,
                    currentTheme = settingsViewModel.theme.collectAsState().value,
                    onThemeChange = { settingsViewModel.setTheme(it) },
                    onPrimaryColorChange = { settingsViewModel.setPrimaryColor(it) }
                )
            }
        }

        // Open the Delete Confirmation Dialog
        composeTestRule.onNodeWithText("Delete Schedule").performClick()

        // Confirm deletion
        composeTestRule.onNodeWithText("Delete").performClick()

        // Verify the dialog is dismissed
        composeTestRule.onNodeWithText("Delete Schedule").assertExists()
    }
}
