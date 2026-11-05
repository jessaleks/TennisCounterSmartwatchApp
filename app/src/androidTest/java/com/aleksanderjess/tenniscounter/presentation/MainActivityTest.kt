package com.aleksanderjess.tenniscounter.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.aleksanderjess.tenniscounter.presentation.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testNavigationToSetWizardScreen() {
        val navController = launchApp()

        composeTestRule.onNodeWithText("Sets to win: 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Match").assertIsDisplayed()
        composeTestRule.runOnIdle {
            assertEquals(Screen.SetWizard.route, navController.currentDestination?.route)
        }
    }

    @Test
    fun testNavigationToMatchScreen() {
        val navController = launchApp()

        composeTestRule.onNodeWithText("Start Match").performClick()

        composeTestRule.onNodeWithText("P1+").assertIsDisplayed()
        composeTestRule.onNodeWithText("Player 1 🎾").assertIsDisplayed()
        composeTestRule.onNodeWithText("P2+").assertExists()
        composeTestRule.runOnIdle {
            val entry = navController.currentBackStackEntry
            assertEquals(Screen.MatchScreen.route, entry?.destination?.route)
            assertEquals("1", entry?.arguments?.getString("setsToWin"))
        }
    }

    private fun launchApp(): TestNavHostController {
        val navController = TestNavHostController(composeTestRule.activity)
        composeTestRule.runOnUiThread {
            navController.navigatorProvider.addNavigator(ComposeNavigator())
        }
        composeTestRule.setContent {
            AppTheme {
                WearTennisApp(navController)
            }
        }
        return navController
    }
}
