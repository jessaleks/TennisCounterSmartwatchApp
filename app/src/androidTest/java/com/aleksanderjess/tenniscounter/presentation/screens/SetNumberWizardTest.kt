package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
    fun testSetWizardScreenDisplaysSetsToWin() {
        composeTestRule.setContent {
            SetWizardScreen(navController = rememberNavController())
        }

        composeTestRule.onNodeWithText("Sets to win: 1").assertIsDisplayed()
    }

    @Test
    fun testSetWizardScreenButtonClick() {
        val navController = TestNavHostController(composeTestRule.activity)
        composeTestRule.runOnUiThread {
            navController.navigatorProvider.addNavigator(ComposeNavigator())
        }
        composeTestRule.setContent {
            AppTheme {
                WearTennisApp(navController)
            }
        }

        composeTestRule.onNodeWithText("Start Match").performClick()

        composeTestRule.onNodeWithText("P1+").assertIsDisplayed()
        composeTestRule.runOnIdle {
            val entry = navController.currentBackStackEntry
            assertEquals(Screen.MatchScreen.route, entry?.destination?.route)
            assertEquals("1", entry?.arguments?.getString("setsToWin"))
        }
    }
}
