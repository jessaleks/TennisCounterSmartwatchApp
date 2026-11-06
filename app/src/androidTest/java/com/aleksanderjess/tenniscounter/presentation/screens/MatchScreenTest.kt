package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class MatchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testMatchScreenDisplaysScore() {
        showMatch()

        composeTestRule.onNodeWithText("P1+").assertIsDisplayed()
        composeTestRule.onNodeWithText("P1-").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player1_name").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player1_name").assertTextEquals("Player 1 🎾")
        composeTestRule.onNodeWithTag("player1_points").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player1_points").assertTextEquals("Points: 0")
        composeTestRule.onNodeWithTag("player2_name").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player2_name").assertTextEquals("Player 2 ")
        composeTestRule.onNodeWithTag("player2_points").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player2_points").assertTextEquals("Points: 0")
        composeTestRule.onNodeWithTag("player1_set1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player1_set1").assertTextEquals("Set 1: 0")
        composeTestRule.onNodeWithTag("player2_set1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player2_set1").assertTextEquals("Set 1: 0")
        composeTestRule.onNodeWithText("P2+").assertIsDisplayed()
        composeTestRule.onNodeWithText("P2-").assertIsDisplayed()
    }

    @Test
    fun testPlayer2MinusDecreasesPoint() {
        showMatch()

        composeTestRule.onNodeWithText("P2+").performClick()
        composeTestRule.onNodeWithTag("player2_points").assertTextEquals("Points: 1")

        composeTestRule.onNodeWithText("P2-").performClick()

        composeTestRule.onNodeWithTag("player2_points").assertTextEquals("Points: 0")
        composeTestRule.onNodeWithTag("player1_points").assertTextEquals("Points: 0")
    }

    @Test
    fun testMatchScreenButtonClick() {
        showMatch()

        composeTestRule.onNodeWithText("P1+").performClick()

        composeTestRule.onNodeWithTag("player1_name").assertTextEquals("Player 1 🎾")
        composeTestRule.onNodeWithTag("player1_points").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player1_points").assertTextEquals("Points: 1")
        composeTestRule.onNodeWithTag("player2_points").assertTextEquals("Points: 0")
        composeTestRule.onNodeWithTag("player1_set1").assertTextEquals("Set 1: 0")
        composeTestRule.onNodeWithTag("player2_set1").assertTextEquals("Set 1: 0")
    }

    private fun showMatch() {
        composeTestRule.setContent {
            MatchScreen(navController = rememberNavController(), setsToWin = 3)
        }
    }
}
