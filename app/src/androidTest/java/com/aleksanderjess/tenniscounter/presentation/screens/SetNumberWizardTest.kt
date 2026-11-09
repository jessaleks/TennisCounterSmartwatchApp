package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.aleksanderjess.tenniscounter.presentation.Screen
import com.aleksanderjess.tenniscounter.presentation.WearTennisApp
import com.aleksanderjess.tenniscounter.presentation.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SetWizardScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testStepperDefaultsToOneSetAndAllControlsVisible() {
        launchApp()

        composeTestRule.onNodeWithText("Sety do wygrania").assertIsDisplayed()
        composeTestRule.onNodeWithTag("sets_to_win_value").assertTextEquals("1")
        composeTestRule.onNodeWithTag("decrease_sets_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("decrease_sets_button").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("increase_sets_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("increase_sets_button").assertIsEnabled()
        composeTestRule.onNodeWithText("Jeden set").assertIsDisplayed()
        composeTestRule.onNodeWithTag("start_match_button").assertIsDisplayed()
    }

    @Test
    fun testStepperBoundsAndLabels() {
        launchApp()

        composeTestRule.onNodeWithTag("increase_sets_button").performClick()
        composeTestRule.onNodeWithTag("sets_to_win_value").assertTextEquals("2")
        composeTestRule.onNodeWithText("Do 2 wygranych setów").assertIsDisplayed()
        composeTestRule.onNodeWithTag("start_match_button").assertIsDisplayed()

        composeTestRule.onNodeWithTag("increase_sets_button").performClick()
        composeTestRule.onNodeWithTag("sets_to_win_value").assertTextEquals("3")
        composeTestRule.onNodeWithText("Do 3 wygranych setów").assertIsDisplayed()
        composeTestRule.onNodeWithTag("increase_sets_button").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("start_match_button").assertIsDisplayed()

        composeTestRule.onNodeWithTag("decrease_sets_button").performClick()
        composeTestRule.onNodeWithTag("sets_to_win_value").assertTextEquals("2")
        composeTestRule.onNodeWithTag("decrease_sets_button").performClick()
        composeTestRule.onNodeWithTag("sets_to_win_value").assertTextEquals("1")
        composeTestRule.onNodeWithTag("decrease_sets_button").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("start_match_button").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testSelectionSurvivesRestoration() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent {
            AppTheme {
                SetWizardScreen(navController = rememberNavController())
            }
        }

        composeTestRule.onNodeWithTag("increase_sets_button").performClick()
        composeTestRule.onNodeWithTag("increase_sets_button").performClick()

        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithTag("sets_to_win_value").assertTextEquals("3")
    }

    @Test
    fun testDefaultSelectionNavigatesWithOneSet() {
        val navController = launchApp()

        composeTestRule.onNodeWithTag("start_match_button").performClick()

        composeTestRule.onNodeWithTag("player1_score_tile").assertIsDisplayed()
        composeTestRule.runOnIdle {
            val entry = navController.currentBackStackEntry
            assertEquals(Screen.MatchScreen.route, entry?.destination?.route)
            assertEquals("1", entry?.arguments?.getString("setsToWin"))
        }
    }

    @Test
    fun testBestOfThreeSelectionNavigatesWithTwoSetsToWin() {
        val navController = launchApp()

        composeTestRule.onNodeWithTag("increase_sets_button").performClick()
        composeTestRule.onNodeWithTag("start_match_button").performClick()

        composeTestRule.runOnIdle {
            val entry = navController.currentBackStackEntry
            assertEquals(Screen.MatchScreen.route, entry?.destination?.route)
            assertEquals("2", entry?.arguments?.getString("setsToWin"))
        }
    }

    @Test
    fun testBestOfFiveSelectionNavigatesWithThreeSetsToWin() {
        val navController = launchApp()

        composeTestRule.onNodeWithTag("increase_sets_button").performClick()
        composeTestRule.onNodeWithTag("increase_sets_button").performClick()
        composeTestRule.onNodeWithTag("start_match_button").performClick()

        composeTestRule.runOnIdle {
            val entry = navController.currentBackStackEntry
            assertEquals(Screen.MatchScreen.route, entry?.destination?.route)
            assertEquals("3", entry?.arguments?.getString("setsToWin"))
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
