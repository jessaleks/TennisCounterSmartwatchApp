package com.aleksanderjess.tenniscounter.lib


import GameState
import decreasePoint
import getScore
import getScoreName
import isDeuce
import isTiebreakCondition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import playerHasAdvantage
import playerHasWonGame
import playerHasWonSet
import playerHasWonTiebreak
import scorePoint

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
}