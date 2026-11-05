package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class SetWizardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSetWizardScreenDisplaysSetsToWin() {
        composeTestRule.setContent {
            SetWizardScreen(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Sets to win: 1").assertExists()
    }

    @Test
    fun testSetWizardScreenButtonClick() {
        composeTestRule.setContent {
            SetWizardScreen(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Start Match").performClick()
        // Add assertions to verify navigation if needed
    }
}