package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.navigation.compose.rememberNavController
import com.aleksanderjess.tenniscounter.presentation.theme.AppTheme
import kotlin.math.hypot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RoundFitTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun setupFitsRoundAtFont10() = runSetup(1.0f)

    @Test
    fun setupFitsRoundAtFont13() = runSetup(1.3f)

    private fun runSetup(fontScale: Float) {
        setContentFontScaled(fontScale) {
            SetWizardScreen(navController = rememberNavController())
        }
        assertAllTextFitsRound("setup fontScale=$fontScale")

        composeTestRule.onNodeWithTag("increase_sets_button").performClick()
        composeTestRule.onNodeWithTag("increase_sets_button").performClick()
        assertAllTextFitsRound("setup=3 fontScale=$fontScale")
    }

    @Test
    fun matchDeuceAndAdvantageFitRoundAtFont10() = runMatch(1.0f)

    @Test
    fun matchDeuceAndAdvantageFitRoundAtFont13() = runMatch(1.3f)

    private fun runMatch(fontScale: Float) {
        setContentFontScaled(fontScale) {
            MatchScreen(navController = rememberNavController(), setsToWin = 3)
        }
        assertAllTextFitsRound("match0 fontScale=$fontScale")

        repeat(3) {
            composeTestRule.onNodeWithTag("player1_score_tile").performClick()
            composeTestRule.onNodeWithTag("player2_score_tile").performClick()
        }
        assertAllTextFitsRound("deuce fontScale=$fontScale")

        composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        assertAllTextFitsRound("advantage fontScale=$fontScale")
    }

    @Test
    fun winnerFitsRoundAtFont10() = runWinner(1.0f)

    @Test
    fun winnerFitsRoundAtFont13() = runWinner(1.3f)

    private fun runWinner(fontScale: Float) {
        setContentFontScaled(fontScale) {
            MatchScreen(navController = rememberNavController(), setsToWin = 1)
        }
        repeat(24) {
            composeTestRule.onNodeWithTag("player1_score_tile").performClick()
        }
        assertAllTextFitsRound("winner fontScale=$fontScale")
    }

    @Test
    fun scoreTileBorderFollowsServing() {
        setContentFontScaled(1.0f) {
            MatchScreen(navController = rememberNavController(), setsToWin = 3)
        }
        composeTestRule.runOnIdle {
            assertEquals(
                com.aleksanderjess.tenniscounter.presentation.theme.TennisYellow,
                scoreTileBorderColor(true),
            )
            assertEquals(
                com.aleksanderjess.tenniscounter.presentation.theme.ScoreBorderGray,
                scoreTileBorderColor(false),
            )
        }
    }

    private fun setContentFontScaled(fontScale: Float, content: @Composable () -> Unit) {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalDensity provides Density(LocalDensity.current.density, fontScale)
            ) {
                AppTheme {
                    content()
                }
            }
        }
    }

    private fun assertAllTextFitsRound(label: String) {
        val root = composeTestRule.onAllNodes(androidx.compose.ui.test.isRoot()).fetchSemanticsNodes().first()
        val cx = root.size.width / 2f
        val cy = root.size.height / 2f
        val radius = minOf(cx, cy)

        val textNodes = composeTestRule.onAllNodes(
            SemanticsMatcher("has text layout") {
                it.config.getOrNull(SemanticsActions.GetTextLayoutResult) != null
            },
            useUnmergedTree = true,
        ).fetchSemanticsNodes()

        assertTrue("$label: no text nodes found", textNodes.isNotEmpty())
        for (node in textNodes) {
            assertNoOverflow(label, node)
            assertInsideCircle(label, node, cx, cy, radius)
        }
    }

    private fun assertNoOverflow(label: String, node: SemanticsNode) {
        val action = node.config.getOrNull(SemanticsActions.GetTextLayoutResult) ?: return
        val results = mutableListOf<TextLayoutResult>()
        action.action?.invoke(results)
        val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString() ?: node.id.toString()
        for (layout in results) {
            assertFalse(
                "$label: visual overflow in text '$text'",
                layout.hasVisualOverflow,
            )
        }
    }

    private fun assertInsideCircle(
        label: String,
        node: SemanticsNode,
        cx: Float,
        cy: Float,
        radius: Float,
    ) {
        val b = node.boundsInRoot
        val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString() ?: node.id.toString()
        for ((x, y) in listOf(
            b.left to b.top, b.right to b.top, b.left to b.bottom, b.right to b.bottom,
        )) {
            val distance = hypot(x - cx, y - cy)
            assertTrue(
                "$label: text '$text' corner at distance $distance exceeds radius $radius",
                distance <= radius + 1f,
            )
        }
    }

    private fun SemanticsNodeInteraction.nothing() = this
}
