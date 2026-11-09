package com.aleksanderjess.tenniscounter.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
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

        composeTestRule.onNodeWithText("Sety do wygrania").assertIsDisplayed()
        composeTestRule.onNodeWithTag("start_match_button").assertExists()
        composeTestRule.runOnIdle {
            assertEquals(Screen.SetWizard.route, navController.currentDestination?.route)
        }
    }

    @Test
    fun testNavigationToMatchScreen() {
        val navController = launchApp()

        composeTestRule.onNodeWithTag("start_match_button").performClick()

        composeTestRule.onNodeWithTag("player1_score_tile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player2_score_tile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("undo_button").assertIsDisplayed()
        composeTestRule.runOnIdle {
            val entry = navController.currentBackStackEntry
            assertEquals(Screen.MatchScreen.route, entry?.destination?.route)
            assertEquals("1", entry?.arguments?.getString("setsToWin"))
        }
    }

    @Test
    fun testNewMatchFromWinnerScreenResetsToSetup() {
        val navController = launchApp()

        composeTestRule.onNodeWithTag("start_match_button").performClick()
        repeat(24) {
            composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        }

        composeTestRule.onNodeWithText("Wygrywa gracz 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sety 1–0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("new_match_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("undo_button").assertIsDisplayed()

        composeTestRule.onNodeWithTag("new_match_button").performClick()

        composeTestRule.onNodeWithText("Sety do wygrania").assertIsDisplayed()
        composeTestRule.runOnIdle {
            assertEquals(Screen.SetWizard.route, navController.currentDestination?.route)
            org.junit.Assert.assertNull(navController.previousBackStackEntry)
        }
    }

    @Test
    fun testUndoOnWinnerScreenReturnsToMatch() {
        launchApp()

        composeTestRule.onNodeWithTag("start_match_button").performClick()
        repeat(24) {
            composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        }
        composeTestRule.onNodeWithText("Wygrywa gracz 1").assertIsDisplayed()

        composeTestRule.onNodeWithTag("undo_button").performClick()

        composeTestRule.onNodeWithTag("player1_score_tile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertIsDisplayed()
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
