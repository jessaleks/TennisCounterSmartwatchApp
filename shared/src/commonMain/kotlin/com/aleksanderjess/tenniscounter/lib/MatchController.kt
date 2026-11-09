package com.aleksanderjess.tenniscounter.lib

class MatchController(setsToWin: Int) {
    init {
        require(setsToWin in 1..3) { "setsToWin must be 1..3, was $setsToWin" }
    }

    private var session = MatchSession(GameState(setsToWin = setsToWin))

    val player1PointsLabel: String get() = getPointLabel(session.current, 1)
    val player2PointsLabel: String get() = getPointLabel(session.current, 2)
    val player1Games: Int get() = session.current.player1Games
    val player2Games: Int get() = session.current.player2Games
    val player1Sets: Int get() = session.current.player1Sets
    val player2Sets: Int get() = session.current.player2Sets
    val servingPlayer: Int get() = session.current.servingPlayer
    val setsToWin: Int get() = session.current.setsToWin
    val isTiebreak: Boolean get() = session.current.isTiebreak
    val isDeuce: Boolean get() = isDeuce(session.current)
    val isMatchOver: Boolean get() = session.current.isMatchOver
    val canUndo: Boolean get() = session.previousStates.isNotEmpty()

    val winningPlayer: Int
        get() = if (!session.current.isMatchOver) {
            0
        } else if (session.current.player1Sets > session.current.player2Sets) {
            1
        } else {
            2
        }

    fun awardPoint(player: Int) {
        session = awardPoint(session, player)
    }

    fun undoPoint() {
        session = undoPoint(session)
    }

    fun exportSnapshot(): String = encodeSession(session).joinToString(",")

    companion object {
        fun restoreSnapshot(snapshot: String): MatchController? {
            val values = snapshot.split(",").map { it.trim().toIntOrNull() ?: return null }
            val session = decodeSession(values) ?: return null
            return MatchController(session.current.setsToWin).also { it.session = session }
        }
    }
}
