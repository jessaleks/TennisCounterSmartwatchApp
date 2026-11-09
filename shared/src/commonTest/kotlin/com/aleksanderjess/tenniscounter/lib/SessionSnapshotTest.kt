package com.aleksanderjess.tenniscounter.lib

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionSnapshotTest {

    private fun winGame(session: MatchSession, player: Int): MatchSession {
        var s = session
        repeat(4) { s = awardPoint(s, player) }
        return s
    }

    @Test
    fun roundtripInitialSession() {
        val session = MatchSession(GameState(setsToWin = 2))
        val decoded = decodeSession(encodeSession(session))
        assertEquals(session, decoded)
    }

    @Test
    fun encodedLayoutMatchesFlatFormat() {
        var session = MatchSession(GameState(setsToWin = 3))
        session = awardPoint(session, 1)
        val encoded = encodeSession(session)
        assertEquals(1 + GAME_STATE_FIELD_COUNT + GAME_STATE_FIELD_COUNT, encoded.size)
        assertEquals(1, encoded[0])
        assertEquals(1, encoded[encoded.size - GAME_STATE_FIELD_COUNT])
    }

    @Test
    fun roundtripAcrossGameSetTiebreakAndMatch() {
        var session = MatchSession(GameState(setsToWin = 1))
        repeat(6) {
            session = winGame(session, 1)
            session = winGame(session, 2)
        }
        assertTrue(session.current.isTiebreak)
        repeat(7) { session = awardPoint(session, 1) }
        assertTrue(session.current.isMatchOver)

        val decoded = decodeSession(encodeSession(session))
        assertEquals(session, decoded)
        assertEquals(session.previousStates.size, decoded?.previousStates?.size)
    }

    @Test
    fun undoAfterRestoreRestoresFullHistory() {
        var session = MatchSession(GameState())
        repeat(5) { session = awardPoint(session, 1) }
        val decoded = decodeSession(encodeSession(session))
        assertNotNull(decoded)
        val undone = undoPoint(decoded)
        assertEquals(session.current.player1Points - 1, undone.current.player1Points)
        assertEquals(session.previousStates.size - 1, undone.previousStates.size)
    }

    @Test
    fun rejectsEmptyAndTruncatedAndTrailing() {
        assertNull(decodeSession(emptyList()))
        val valid = encodeSession(MatchSession(GameState()))
        assertNull(decodeSession(valid.dropLast(1)))
        assertNull(decodeSession(valid + 7))
    }

    @Test
    fun rejectsNegativeAndOverflowingHistoryCount() {
        assertNull(decodeSession(listOf(-1) + encodeSession(MatchSession(GameState())).drop(1)))
        val hugeCount = Int.MAX_VALUE / GAME_STATE_FIELD_COUNT + 1
        assertNull(decodeSession(List(1 + GAME_STATE_FIELD_COUNT + GAME_STATE_FIELD_COUNT) { if (it == 0) hugeCount else 0 }))
    }

    @Test
    fun rejectsInvalidFlagsServerCountersAndSetsToWin() {
        val valid = encodeSession(MatchSession(GameState())).toMutableList()
        val base = 1

        fun mutated(index: Int, value: Int): List<Int> =
            valid.toMutableList().also { it[base + index] = value }

        assertNull(decodeSession(mutated(8, 2)))
        assertNull(decodeSession(mutated(11, 5)))
        assertNull(decodeSession(mutated(9, 0)))
        assertNull(decodeSession(mutated(9, 3)))
        assertNull(decodeSession(mutated(10, 0)))
        assertNull(decodeSession(mutated(0, -1)))
        assertNull(decodeSession(mutated(4, -2)))
        assertNull(decodeSession(mutated(12, 0)))
        assertNull(decodeSession(mutated(12, 4)))

        assertNotNull(decodeSession(valid))
    }
}
