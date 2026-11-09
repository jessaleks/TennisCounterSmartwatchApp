package com.aleksanderjess.tenniscounter.lib

const val GAME_STATE_FIELD_COUNT = 13

private fun GameState.toIntList(): List<Int> = listOf(
    player1Points,
    player2Points,
    player1Games,
    player2Games,
    player1Sets,
    player2Sets,
    player1TiebreakPoints,
    player2TiebreakPoints,
    if (isTiebreak) 1 else 0,
    servingPlayer,
    tiebreakFirstServer,
    if (isMatchOver) 1 else 0,
    setsToWin,
)

private fun List<Int>.toGameState(offset: Int): GameState = GameState(
    player1Points = this[offset],
    player2Points = this[offset + 1],
    player1Games = this[offset + 2],
    player2Games = this[offset + 3],
    player1Sets = this[offset + 4],
    player2Sets = this[offset + 5],
    player1TiebreakPoints = this[offset + 6],
    player2TiebreakPoints = this[offset + 7],
    isTiebreak = this[offset + 8] != 0,
    servingPlayer = this[offset + 9],
    tiebreakFirstServer = this[offset + 10],
    isMatchOver = this[offset + 11] != 0,
    setsToWin = this[offset + 12],
)

private fun List<Int>.isValidGameState(offset: Int): Boolean {
    for (i in 0..7) {
        if (this[offset + i] < 0) return false
    }
    if (this[offset + 8] != 0 && this[offset + 8] != 1) return false
    if (this[offset + 9] != 1 && this[offset + 9] != 2) return false
    if (this[offset + 10] != 1 && this[offset + 10] != 2) return false
    if (this[offset + 11] != 0 && this[offset + 11] != 1) return false
    if (this[offset + 12] !in 1..3) return false
    return true
}

fun encodeSession(session: MatchSession): List<Int> = buildList {
    add(session.previousStates.size)
    session.previousStates.forEach { addAll(it.toIntList()) }
    addAll(session.current.toIntList())
}

fun decodeSession(values: List<Int>): MatchSession? {
    if (values.isEmpty()) return null
    val count = values[0]
    if (count < 0) return null
    val expectedSize = 1L + count.toLong() * GAME_STATE_FIELD_COUNT + GAME_STATE_FIELD_COUNT
    if (expectedSize > Int.MAX_VALUE || values.size.toLong() != expectedSize) return null
    for (i in 0..count) {
        if (!values.isValidGameState(1 + i * GAME_STATE_FIELD_COUNT)) return null
    }
    val previous = (0 until count).map { i ->
        values.toGameState(1 + i * GAME_STATE_FIELD_COUNT)
    }
    return MatchSession(
        current = values.toGameState(1 + count * GAME_STATE_FIELD_COUNT),
        previousStates = previous,
    )
}
