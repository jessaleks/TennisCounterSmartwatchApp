package com.aleksanderjess.tenniscounter.lib

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MatchControllerTest {

    @Test
    fun scoreLabelsFollowTennisProgression() {
        val c = MatchController(1)
        assertEquals("0", c.player1PointsLabel)
        c.awardPoint(1)
        assertEquals("15", c.player1PointsLabel)
        c.awardPoint(1)
        assertEquals("30", c.player1PointsLabel)
        c.awardPoint(1)
        assertEquals("40", c.player1PointsLabel)
        assertEquals("0", c.player2PointsLabel)
    }

    @Test
    fun deuceAndAdvantageLabels() {
        val c = MatchController(1)
        repeat(3) { c.awardPoint(1); c.awardPoint(2) }
        assertTrue(c.isDeuce)
        assertEquals("40", c.player1PointsLabel)
        assertEquals("40", c.player2PointsLabel)
        c.awardPoint(1)
        assertEquals("AD", c.player1PointsLabel)
        assertEquals("40", c.player2PointsLabel)
    }

    @Test
    fun winningMatchReportsWinner() {
        val c = MatchController(1)
        assertEquals(0, c.winningPlayer)
        repeat(6) {
            repeat(4) { c.awardPoint(1) }
            repeat(4) { c.awardPoint(2) }
        }
        assertTrue(c.isTiebreak)
        repeat(7) { c.awardPoint(1) }
        assertTrue(c.isMatchOver)
        assertEquals(1, c.winningPlayer)
        assertEquals(1, c.player1Sets)
    }

    @Test
    fun scoringAfterMatchOverIsNoOp() {
        val c = MatchController(1)
        repeat(6) { repeat(4) { c.awardPoint(1) } }
        assertTrue(c.isMatchOver)
        val snapshot = c.exportSnapshot()
        c.awardPoint(1)
        c.awardPoint(2)
        assertEquals(snapshot, c.exportSnapshot())
    }

    @Test
    fun undoRestoresAndCanUndoTracksHistory() {
        val c = MatchController(1)
        assertFalse(c.canUndo)
        c.awardPoint(2)
        assertTrue(c.canUndo)
        c.undoPoint()
        assertFalse(c.canUndo)
        assertEquals("0", c.player2PointsLabel)
    }

    @Test
    fun exportRestoreRoundtripPreservesUndo() {
        val c = MatchController(2)
        repeat(5) { c.awardPoint(1) }
        c.awardPoint(2)
        val restored = MatchController.restoreSnapshot(c.exportSnapshot())
        assertNotNull(restored)
        assertEquals(c.exportSnapshot(), restored.exportSnapshot())
        restored.undoPoint()
        assertEquals("0", restored.player2PointsLabel)
        assertEquals("15", restored.player1PointsLabel)
    }

    @Test
    fun rejectsInvalidConstructorSetsToWin() {
        assertFailsWith<IllegalArgumentException> { MatchController(0) }
        assertFailsWith<IllegalArgumentException> { MatchController(4) }
    }

    @Test
    fun rejectsInvalidPlayerAndSnapshot() {
        val c = MatchController(1)
        assertFailsWith<IllegalArgumentException> { c.awardPoint(0) }
        assertFailsWith<IllegalArgumentException> { c.awardPoint(3) }
        assertNull(MatchController.restoreSnapshot(""))
        assertNull(MatchController.restoreSnapshot("not,a,number"))
        assertNull(MatchController.restoreSnapshot("1,2,3"))
        assertNull(MatchController.restoreSnapshot("0," + List(12) { "0" }.joinToString(",")))
    }
}
