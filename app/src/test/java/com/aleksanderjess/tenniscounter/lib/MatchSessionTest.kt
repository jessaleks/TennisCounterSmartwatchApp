package com.aleksanderjess.tenniscounter.lib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MatchSessionTest {

    private fun winGame(session: MatchSession, player: Int): MatchSession {
        var s = session
        repeat(4) { s = awardPoint(s, player) }
        return s
    }

    private fun reachTiebreak(session: MatchSession): MatchSession {
        var s = session
        repeat(6) {
            s = winGame(s, 1)
            s = winGame(s, 2)
        }
        return s
    }

    @Test
    fun awardPointAppendsSnapshotAndScores() {
        val session = MatchSession(GameState())
        val after = awardPoint(session, 1)
        assertEquals(1, after.current.player1Points)
        assertEquals(1, after.previousStates.size)
        assertEquals(GameState(), after.previousStates.last())
    }

    @Test
    fun awardRejectsInvalidPlayer() {
        val session = MatchSession(GameState())
        assertThrows<IllegalArgumentException> { awardPoint(session, 0) }
        assertThrows<IllegalArgumentException> { awardPoint(session, 3) }
    }

    @Test
    fun undoRestoresNormalPoint() {
        var s = MatchSession(GameState(player1Points = 2, player2Points = 1))
        val before = s.current.copy()
        s = awardPoint(s, 1)
        val undone = undoPoint(s)
        assertEquals(before, undone.current)
        assertTrue(undone.previousStates.isEmpty())
    }

    @Test
    fun undoRestoresGameWinIncludingServer() {
        var s = MatchSession(GameState(player1Points = 3, player2Points = 2, servingPlayer = 1))
        val before = s.current.copy()
        s = awardPoint(s, 1)
        assertEquals(1, s.current.player1Games)
        val undone = undoPoint(s)
        assertEquals(before, undone.current)
    }

    @Test
    fun undoRestoresSetWin() {
        var s = MatchSession(
            GameState(setsToWin = 3, player1Games = 5, player2Games = 3, player1Points = 3),
        )
        val before = s.current.copy()
        s = awardPoint(s, 1)
        assertEquals(1, s.current.player1Sets)
        val undone = undoPoint(s)
        assertEquals(before, undone.current)
    }

    @Test
    fun undoRestoresTiebreakEntry() {
        var s = MatchSession(
            GameState(player1Games = 5, player2Games = 6, player1Points = 3, player2Points = 2, servingPlayer = 1),
        )
        val before = s.current.copy()
        s = awardPoint(s, 1)
        assertTrue(s.current.isTiebreak)
        val undone = undoPoint(s)
        assertEquals(before, undone.current)
        assertFalse(undone.current.isTiebreak)
    }

    @Test
    fun undoRestoresTiebreakWin() {
        var s = MatchSession(
            GameState(
                isTiebreak = true, player1Games = 6, player2Games = 6,
                player1TiebreakPoints = 6, player2TiebreakPoints = 5,
            ),
        )
        val before = s.current.copy()
        s = awardPoint(s, 1)
        assertFalse(s.current.isTiebreak)
        assertEquals(1, s.current.player1Sets)
        val undone = undoPoint(s)
        assertEquals(before, undone.current)
        assertTrue(undone.current.isTiebreak)
    }

    @Test
    fun undoRestoresMatchWin() {
        var s = MatchSession(
            GameState(setsToWin = 1, player1Games = 5, player2Games = 3, player1Points = 3),
        )
        val before = s.current.copy()
        s = awardPoint(s, 1)
        assertTrue(s.current.isMatchOver)
        val undone = undoPoint(s)
        assertEquals(before, undone.current)
        assertFalse(undone.current.isMatchOver)
    }

    @Test
    fun undoOnEmptyHistoryIsNoOp() {
        val s = MatchSession(GameState())
        assertEquals(s, undoPoint(s))
    }

    @Test
    fun noScoringAfterMatchEnd() {
        var s = MatchSession(GameState(setsToWin = 1, player1Games = 5, player1Points = 3))
        s = awardPoint(s, 1)
        assertTrue(s.current.isMatchOver)
        val historySize = s.previousStates.size
        val after = awardPoint(s, 2)
        assertEquals(s, after)
        assertEquals(historySize, after.previousStates.size)
    }

    @Test
    fun fullMatchUndoChainRestoresExactStates() {
        var s = MatchSession(GameState(setsToWin = 1))
        val snapshots = mutableListOf(s.current.copy())
        s = reachTiebreak(s)
        snapshots += s.current.copy()
        repeat(7) { s = awardPoint(s, 1) }
        assertTrue(s.current.isMatchOver)
        repeat(7) { s = undoPoint(s) }
        assertEquals(snapshots.last(), s.current)
    }
}
