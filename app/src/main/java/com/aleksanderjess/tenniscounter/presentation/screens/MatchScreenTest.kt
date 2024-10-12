package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class MatchScreenTest {

    @get:Rule
    private val composeTestRule = createComposeRule()

    @Test
    fun testMatchScreenDisplaysScore() {
        composeTestRule.setContent {
            MatchScreen(navController = rememberNavController(), setsToWin = 3)
        }

        composeTestRule.onNodeWithText("0-0, Serving: Player 1").assertExists()
    }

    @Test
    fun testMatchScreenButtonClick() {
        composeTestRule.setContent {
            MatchScreen(navController = rememberNavController(), setsToWin = 3)
        }

        composeTestRule.onNodeWithText("P1+").performClick()
        composeTestRule.onNodeWithText("15-0, Serving: Player 1").assertExists()
    }
}