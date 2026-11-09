package com.aleksanderjess.tenniscounter.lib

data class MatchSession(
    val current: GameState,
    val previousStates: List<GameState> = emptyList(),
)

fun awardPoint(session: MatchSession, player: Int): MatchSession {
    require(player == 1 || player == 2) { "player must be 1 or 2, was $player" }
    if (session.current.isMatchOver) return session
    return session.copy(
        previousStates = session.previousStates + session.current.copy(),
        current = scorePoint(session.current, player),
    )
}

fun undoPoint(session: MatchSession): MatchSession {
    val previous = session.previousStates.lastOrNull() ?: return session
    return session.copy(
        current = previous,
        previousStates = session.previousStates.dropLast(1),
    )
}
