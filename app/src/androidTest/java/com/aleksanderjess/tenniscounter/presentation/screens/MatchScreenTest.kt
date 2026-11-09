package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.navigation.compose.rememberNavController
import com.aleksanderjess.tenniscounter.presentation.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class MatchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testMatchScreenDisplaysScore() {
        showMatch()

        composeTestRule.onNodeWithTag("player1_score_tile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player2_score_tile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertTextEquals("0")
        composeTestRule.onNodeWithTag("player2_points", useUnmergedTree = true).assertTextEquals("0")
        composeTestRule.onNodeWithTag("player1_serving_dot", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("player2_serving_dot", useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithTag("undo_button").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Cofnij").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gracz 1", useUnmergedTree = true).assertIsDisplayed()
        val buttonRole = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button)
        composeTestRule.onNode(buttonRole and
            androidx.compose.ui.test.hasTestTag("player1_score_tile")).assertIsDisplayed()
        composeTestRule.onNode(buttonRole and
            androidx.compose.ui.test.hasTestTag("player2_score_tile")).assertIsDisplayed()
        composeTestRule.onAllNodesWithText("+ punkt", useUnmergedTree = true)
            .assertCountEquals(2)
        composeTestRule.onNodeWithTag("match_stats").assertTextEquals("Gemy 0–0 · Sety 0–0")
    }

    @Test
    fun testTapTileProgressesThroughTennisScores() {
        showMatch()

        composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertTextEquals("15")
        composeTestRule.onNodeWithTag("undo_button").assertIsEnabled()

        composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertTextEquals("30")

        composeTestRule.onNodeWithTag("player2_score_tile").performClick()
        composeTestRule.onNodeWithTag("player2_points", useUnmergedTree = true).assertTextEquals("15")
    }

    @Test
    fun testDeuceAndAdvantage() {
        showMatch()

        repeat(3) {
            composeTestRule.onNodeWithTag("player1_score_tile").performClick()
            composeTestRule.onNodeWithTag("player2_score_tile").performClick()
        }
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertTextEquals("40")
        composeTestRule.onNodeWithTag("player2_points", useUnmergedTree = true).assertTextEquals("40")
        composeTestRule.onNodeWithText("Równowaga").assertIsDisplayed()
        composeTestRule.onNodeWithTag("match_stats").assertTextEquals("Gemy 0–0 · Sety 0–0")

        composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertTextEquals("AD")
        composeTestRule.onNodeWithTag("player2_points", useUnmergedTree = true).assertTextEquals("40")

        composeTestRule.onNodeWithTag("player2_score_tile").performClick()
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertTextEquals("40")
        composeTestRule.onNodeWithTag("player2_points", useUnmergedTree = true).assertTextEquals("40")
    }

    @Test
    fun testUndoRestoresGameTransition() {
        showMatch()

        repeat(4) {
            composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        }
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertTextEquals("0")
        composeTestRule.onNodeWithTag("match_stats").assertTextEquals("Gemy 1–0 · Sety 0–0")
        composeTestRule.onNodeWithTag("player2_serving_dot", useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithTag("undo_button").performClick()

        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true).assertTextEquals("40")
        composeTestRule.onNodeWithTag("player2_points", useUnmergedTree = true).assertTextEquals("0")
        composeTestRule.onNodeWithTag("match_stats").assertTextEquals("Gemy 0–0 · Sety 0–0")
        composeTestRule.onNodeWithTag("player1_serving_dot", useUnmergedTree = true).assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testScoreAndUndoSurviveRestorationAcrossGameTransition() {
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent {
            AppTheme {
                MatchScreen(navController = rememberNavController(), setsToWin = 3)
            }
        }

        repeat(4) {
            composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        }
        composeTestRule.onNodeWithTag("match_stats").assertTextEquals("Gemy 1–0 · Sety 0–0")

        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true)
            .assertTextEquals("0")
        composeTestRule.onNodeWithTag("match_stats").assertTextEquals("Gemy 1–0 · Sety 0–0")
        composeTestRule.onNodeWithTag("undo_button").assertIsEnabled()

        composeTestRule.onNodeWithTag("undo_button").performClick()
        composeTestRule.onNodeWithTag("player1_points", useUnmergedTree = true)
            .assertTextEquals("40")
        composeTestRule.onNodeWithTag("match_stats").assertTextEquals("Gemy 0–0 · Sety 0–0")
    }

    private fun showMatch() {
        composeTestRule.setContent {
            AppTheme {
                MatchScreen(navController = rememberNavController(), setsToWin = 3)
            }
        }
    }
}
