package com.aleksanderjess.tenniscounter.lib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GameStateTest {

    @Test
    fun testGetScoreName() {
        assertEquals("0", getScoreName(0))
        assertEquals("15", getScoreName(1))
        assertEquals("30", getScoreName(2))
        assertEquals("40", getScoreName(3))
        assertEquals("40", getScoreName(4)) // Points beyond 3 should still return "40"
    }

    @Test
    fun testIsDeuce() {
        val state = GameState(player1Points = 3, player2Points = 3)
        assertTrue(isDeuce(state))

        val stateNotDeuce = GameState(player1Points = 3, player2Points = 2)
        assertFalse(isDeuce(stateNotDeuce))
    }

    @Test
    fun testPlayerHasAdvantage() {
        val state = GameState(player1Points = 4, player2Points = 3)
        assertTrue(playerHasAdvantage(state))

        val stateNoAdvantage = GameState(player1Points = 3, player2Points = 3)
        assertFalse(playerHasAdvantage(stateNoAdvantage))
    }

    @Test
    fun testPlayerHasWonGame() {
        val state = GameState(player1Points = 4, player2Points = 2)
        assertTrue(playerHasWonGame(state))

        val stateNotWon = GameState(player1Points = 3, player2Points = 3)
        assertFalse(playerHasWonGame(stateNotWon))
    }

    @Test
    fun testPlayerHasWonSet() {
        assertTrue(playerHasWonSet(6, 4))
        assertFalse(playerHasWonSet(5, 5))
    }

    @Test
    fun testIsTiebreakCondition() {
        val state = GameState(player1Games = 6, player2Games = 6)
        assertTrue(isTiebreakCondition(state))

        val stateNotTiebreak = GameState(player1Games = 5, player2Games = 6)
        assertFalse(isTiebreakCondition(stateNotTiebreak))
    }

    @Test
    fun testPlayerHasWonTiebreak() {
        val state = GameState(player1TiebreakPoints = 7, player2TiebreakPoints = 5)
        assertTrue(playerHasWonTiebreak(state))

        val stateNotWonTiebreak = GameState(player1TiebreakPoints = 6, player2TiebreakPoints = 6)
        assertFalse(playerHasWonTiebreak(stateNotWonTiebreak))
    }

    @Test
    fun testScorePoint() {
        val state = GameState()
        val newState = scorePoint(state, 1)
        assertEquals(1, newState.player1Points)
    }

    @Test
    fun testDecreasePoint() {
        val state = GameState(player1Points = 1)
        val newState = decreasePoint(state, 1)
        assertEquals(0, newState.player1Points)
    }

    @Test
    fun testGetScore() {
        val state = GameState(player1Points = 3, player2Points = 3)
        assertEquals("Deuce, Serving: Player 1", getScore(state))
    }

    @Test
    fun testScorePointSetsMatchOverWhenSetsToWinReached() {
        val oneSetToWin = GameState(
            setsToWin = 1,
            player1Games = 5,
            player2Games = 4,
            player1Points = 3,
            player2Points = 2,
        )

        val afterWinningSet = scorePoint(oneSetToWin, 1)

        assertEquals(1, afterWinningSet.player1Sets)
        assertTrue(afterWinningSet.isMatchOver)
    }

    @Test
    fun testScorePointDoesNotEndMatchBeforeSetsToWin() {
        val state = GameState(
            setsToWin = 3,
            player1Sets = 1,
            player1Games = 5,
            player2Games = 4,
            player1Points = 3,
            player2Points = 2,
        )

        val afterWinningSet = scorePoint(state, 1)

        assertEquals(2, afterWinningSet.player1Sets)
        assertFalse(afterWinningSet.isMatchOver)
    }

    @Nested
    inner class EdgeCases {

        @Test
        fun playerHasAdvantageRequiresPointsBeyondForty() {
            assertFalse(playerHasAdvantage(GameState(player1Points = 3, player2Points = 2)))
            assertTrue(playerHasAdvantage(GameState(player1Points = 5, player2Points = 4)))
            assertTrue(playerHasAdvantage(GameState(player1Points = 4, player2Points = 5)))
        }

        @Test
        fun playerHasWonGameNeedsTwoPointMargin() {
            assertFalse(playerHasWonGame(GameState(player1Points = 4, player2Points = 3)))
            assertTrue(playerHasWonGame(GameState(player1Points = 5, player2Points = 3)))
        }

        @Test
        fun playerHasWonSetNeedsSixGamesWithTwoGameLead() {
            assertFalse(playerHasWonSet(6, 5))
            assertFalse(playerHasWonSet(5, 4))
            assertTrue(playerHasWonSet(6, 4))
            assertTrue(playerHasWonSet(7, 5))
        }

        @Test
        fun playerHasWonTiebreakNeedsSevenPointsAndTwoPointMargin() {
            assertFalse(playerHasWonTiebreak(GameState(player1TiebreakPoints = 7, player2TiebreakPoints = 6)))
            assertTrue(playerHasWonTiebreak(GameState(player1TiebreakPoints = 8, player2TiebreakPoints = 6)))
            assertTrue(playerHasWonTiebreak(GameState(player1TiebreakPoints = 10, player2TiebreakPoints = 8)))
        }

        @Test
        fun scorePointDoesNotMutateOriginalState() {
            val original = GameState(player1Points = 2)
            val updated = scorePoint(original, 1)

            assertNotSame(original, updated)
            assertEquals(2, original.player1Points)
            assertEquals(3, updated.player1Points)
        }

        @Test
        fun winningGameResetsPointsAndIncrementsGames() {
            val beforeGameWin = GameState(player1Points = 3, player2Points = 2, player1Games = 2, player2Games = 1)

            val afterGameWin = scorePoint(beforeGameWin, 1)

            assertEquals(0, afterGameWin.player1Points)
            assertEquals(0, afterGameWin.player2Points)
            assertEquals(3, afterGameWin.player1Games)
            assertEquals(1, afterGameWin.player2Games)
            assertFalse(afterGameWin.isTiebreak)
        }

        @Test
        fun reachingSixAllStartsTiebreakInsteadOfAwardingSet() {
            val oneGameFromTiebreak = GameState(
                player1Games = 5,
                player2Games = 6,
                player1Points = 3,
                player2Points = 2,
                player1Sets = 0,
                player2Sets = 0,
            )

            val afterPoint = scorePoint(oneGameFromTiebreak, 1)

            assertEquals(6, afterPoint.player1Games)
            assertEquals(6, afterPoint.player2Games)
            assertTrue(afterPoint.isTiebreak)
            assertEquals(0, afterPoint.player1Sets)
            assertEquals(0, afterPoint.player2Sets)
            assertEquals(0, afterPoint.player1Points)
            assertEquals(0, afterPoint.player2Points)
        }

        @Test
        fun winningTiebreakAwardsSetAndClearsTiebreakState() {
            val tiebreak = GameState(
                isTiebreak = true,
                player1Games = 6,
                player2Games = 6,
                player1TiebreakPoints = 6,
                player2TiebreakPoints = 5,
                player1Sets = 0,
                player2Sets = 0,
                setsToWin = 3,
            )

            val afterWin = scorePoint(tiebreak, 1)

            assertFalse(afterWin.isTiebreak)
            assertEquals(0, afterWin.player1TiebreakPoints)
            assertEquals(0, afterWin.player2TiebreakPoints)
            assertEquals(0, afterWin.player1Games)
            assertEquals(0, afterWin.player2Games)
            assertEquals(1, afterWin.player1Sets)
            assertFalse(afterWin.isMatchOver)
        }

        @Test
        fun winningTiebreakCanEndMatchWhenSetsToWinIsOne() {
            val decidingTiebreak = GameState(
                setsToWin = 1,
                isTiebreak = true,
                player1Games = 6,
                player2Games = 6,
                player1TiebreakPoints = 6,
                player2TiebreakPoints = 4,
            )

            val afterWin = scorePoint(decidingTiebreak, 1)

            assertEquals(1, afterWin.player1Sets)
            assertTrue(afterWin.isMatchOver)
        }

        @Test
        fun decreasePointDoesNotGoBelowZeroInRegularGame() {
            val atZero = GameState(player1Points = 0, player2Points = 2)

            val afterDecrease = decreasePoint(atZero, 1)

            assertEquals(0, afterDecrease.player1Points)
            assertEquals(2, afterDecrease.player2Points)
        }

        @Test
        fun decreasePointInTiebreakOnlyAffectsScoringPlayer() {
            val tiebreak = GameState(
                isTiebreak = true,
                player1TiebreakPoints = 2,
                player2TiebreakPoints = 3,
            )

            val afterDecrease = decreasePoint(tiebreak, 1)

            assertEquals(1, afterDecrease.player1TiebreakPoints)
            assertEquals(3, afterDecrease.player2TiebreakPoints)
        }

        @Test
        fun getScoreReportsTiebreakAdvantageAndMatchWinner() {
            val tiebreakScore = GameState(
                isTiebreak = true,
                player1TiebreakPoints = 4,
                player2TiebreakPoints = 3,
                servingPlayer = 2,
            )
            assertEquals(
                "Tiebreak: 4 - 3, Serving: Player 2",
                getScore(tiebreakScore),
            )

            val advantage = GameState(player1Points = 5, player2Points = 4, servingPlayer = 1)
            assertEquals(
                "Advantage Player 1, Serving: Player 1",
                getScore(advantage),
            )

            val matchWon = GameState(player2Sets = 2, setsToWin = 2)
            assertEquals("Player 2 wins the match", getScore(matchWon))
            assertTrue(matchWon.isMatchOver)
        }

        @Test
        fun switchServerTogglesServingPlayer() {
            val servingPlayer1 = GameState(servingPlayer = 1)
            val servingPlayer2 = switchServer(servingPlayer1.copy())

            assertEquals(2, servingPlayer2.servingPlayer)
            assertEquals(1, switchServer(servingPlayer2.copy()).servingPlayer)
        }

        @Test
        fun winningGameSwitchesServerExceptWhenEnteringTiebreak() {
            val beforeWin = GameState(
                servingPlayer = 1,
                player1Games = 2,
                player2Games = 1,
                player1Points = 3,
                player2Points = 1,
            )
            val afterRegularGameWin = scorePoint(beforeWin, 1)
            assertEquals(2, afterRegularGameWin.servingPlayer)

            val beforeTiebreak = GameState(
                servingPlayer = 1,
                player1Games = 5,
                player2Games = 6,
                player1Points = 3,
                player2Points = 2,
            )
            val afterSixAll = scorePoint(beforeTiebreak, 1)
            assertTrue(afterSixAll.isTiebreak)
            assertEquals(2, afterSixAll.servingPlayer)
        }
    }
}

class PointLabelAndServeTest {

    private fun playPoints(state: GameState, player: Int, count: Int): GameState {
        var s = state
        repeat(count) { s = scorePoint(s, player) }
        return s
    }

    private fun winGame(state: GameState, player: Int): GameState = playPoints(state, player, 4)

    private fun reachTiebreak(firstServer: Int = 1): GameState {
        var s = GameState(servingPlayer = firstServer)
        repeat(6) {
            s = winGame(s, 1)
            s = winGame(s, 2)
        }
        return s
    }

    @Test
    fun pointLabelsAreTennisScores() {
        var s = GameState()
        val expected = listOf("0", "15", "30", "40")
        expected.forEachIndexed { i, label ->
            assertEquals(label, getPointLabel(s, 1))
            assertEquals(label, getPointLabel(s, 2))
            if (i < expected.size - 1) {
                s = scorePoint(s, 1)
                s = scorePoint(s, 2)
            }
        }
    }

    @Test
    fun deuceShowsFortyForBothPlayers() {
        val s33 = GameState(player1Points = 3, player2Points = 3)
        assertEquals("40", getPointLabel(s33, 1))
        assertEquals("40", getPointLabel(s33, 2))
        val s55 = GameState(player1Points = 5, player2Points = 5)
        assertEquals("40", getPointLabel(s55, 1))
        assertEquals("40", getPointLabel(s55, 2))
    }

    @Test
    fun advantageShowsAdForLeaderAndFortyForOther() {
        val s43 = GameState(player1Points = 4, player2Points = 3)
        assertEquals("AD", getPointLabel(s43, 1))
        assertEquals("40", getPointLabel(s43, 2))
        val s34 = GameState(player1Points = 3, player2Points = 4)
        assertEquals("40", getPointLabel(s34, 1))
        assertEquals("AD", getPointLabel(s34, 2))
    }

    @Test
    fun tiebreakLabelsAreNumeric() {
        val s = GameState(isTiebreak = true, player1TiebreakPoints = 8, player2TiebreakPoints = 7)
        assertEquals("8", getPointLabel(s, 1))
        assertEquals("7", getPointLabel(s, 2))
    }

    @Test
    fun pointProgressionResetsAfterGame() {
        var s = GameState()
        s = scorePoint(s, 1); assertEquals("15", getPointLabel(s, 1))
        s = scorePoint(s, 1); assertEquals("30", getPointLabel(s, 1))
        s = scorePoint(s, 1); assertEquals("40", getPointLabel(s, 1))
        s = scorePoint(s, 1)
        assertEquals(1, s.player1Games)
        assertEquals("0", getPointLabel(s, 1))
        assertEquals("0", getPointLabel(s, 2))
    }

    @Test
    fun repeatedDeuceAdvantageCancellation() {
        var s = GameState(player1Points = 3, player2Points = 3)
        s = scorePoint(s, 1)
        assertEquals("AD", getPointLabel(s, 1)); assertEquals("40", getPointLabel(s, 2))
        s = scorePoint(s, 2)
        assertEquals("40", getPointLabel(s, 1)); assertEquals("40", getPointLabel(s, 2))
        s = scorePoint(s, 2)
        assertEquals("40", getPointLabel(s, 1)); assertEquals("AD", getPointLabel(s, 2))
        s = scorePoint(s, 1)
        assertEquals("40", getPointLabel(s, 1)); assertEquals("40", getPointLabel(s, 2))
    }

    @Test
    fun sixFourAndSevenFiveWinSets() {
        var s = GameState(player1Games = 5, player2Games = 4)
        s = winGame(s, 1)
        assertEquals(1, s.player1Sets)
        assertEquals(0, s.player1Games)

        var s2 = GameState(player1Games = 6, player2Games = 5)
        s2 = winGame(s2, 1)
        assertEquals(1, s2.player1Sets)
        assertFalse(s2.isTiebreak)
    }

    @Test
    fun sixAllEntersTiebreak() {
        val s = reachTiebreak()
        assertTrue(s.isTiebreak)
        assertEquals(6, s.player1Games)
        assertEquals(6, s.player2Games)
    }

    @Test
    fun tiebreakNotWonAtSevenSixWonAtEightSix() {
        var s = GameState(
            isTiebreak = true, player1Games = 6, player2Games = 6,
            player1TiebreakPoints = 6, player2TiebreakPoints = 6,
        )
        s = scorePoint(s, 1)
        assertTrue(s.isTiebreak)
        s = scorePoint(s, 1)
        assertFalse(s.isTiebreak)
        assertEquals(1, s.player1Sets)
    }

    @Test
    fun tiebreakServiceSequenceOneThenTwoForBothInitialServers() {
        for (first in 1..2) {
            var s = reachTiebreak(first)
            val firstTbServer = s.servingPlayer
            assertEquals(first, firstTbServer)
            val second = 3 - firstTbServer
            val expected = listOf(
                firstTbServer,
                second, second,
                firstTbServer, firstTbServer,
                second, second,
                firstTbServer, firstTbServer,
                second, second,
            )
            for ((i, server) in expected.withIndex()) {
                assertEquals(
                    server, s.servingPlayer,
                    "first=$first pointIndex=$i",
                )
                val scorer = if (i % 2 == 0) 1 else 2
                s = scorePoint(s, scorer)
            }
        }
    }

    @Test
    fun nextSetServerIsOppositeTiebreakFirstServer() {
        val scenarios = listOf(
            listOf(7, 0), listOf(7, 5), listOf(8, 6), listOf(9, 7),
        )
        for (first in 1..2) {
            for ((p1Pts, p2Pts) in scenarios) {
                val before = GameState(
                    isTiebreak = true,
                    player1Games = 6, player2Games = 6,
                    player1TiebreakPoints = p1Pts - 1,
                    player2TiebreakPoints = p2Pts,
                    tiebreakFirstServer = 3 - first,
                    servingPlayer = 1,
                )
                val after = scorePoint(before, 1)
                assertFalse(after.isTiebreak)
                assertEquals(first, after.servingPlayer, "tiebreak first server was ${3 - first}")
            }
        }
    }

    @Test
    fun tiebreakEntrySetsFirstServerAfterSwitch() {
        var s = GameState(servingPlayer = 1, player1Games = 5, player2Games = 6, player1Points = 3, player2Points = 0)
        s = scorePoint(s, 1)
        assertTrue(s.isTiebreak)
        assertEquals(2, s.servingPlayer)
        assertEquals(2, s.tiebreakFirstServer)
    }

    @Test
    fun scoringAfterMatchOverIsRefused() {
        val s = GameState(setsToWin = 1, player1Sets = 1, isMatchOver = true)
        val after = scorePoint(s, 2)
        assertEquals(0, after.player2Points)
        assertEquals(0, after.player2TiebreakPoints)
    }
}
