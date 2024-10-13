package com.aleksanderjess.tenniscounter.presentation

import WearTennisApp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aleksanderjess.tenniscounter.presentation.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationToSetWizardScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            AppTheme {
                WearTennisApp(navController)
            }
        }

        // Verify that SetWizardScreen is displayed
        composeTestRule.onNodeWithText("Set Wizard Screen Text").assertExists()
    }

    @Test
    fun testNavigationToMatchScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            AppTheme {
                WearTennisApp(navController)
            }
        }

        // Perform navigation to MatchScreen
        composeTestRule.onNodeWithText("Navigate to Match Screen").performClick()

        // Verify that MatchScreen is displayed
        composeTestRule.onNodeWithText("Match Screen Text").assertExists()
    }
}