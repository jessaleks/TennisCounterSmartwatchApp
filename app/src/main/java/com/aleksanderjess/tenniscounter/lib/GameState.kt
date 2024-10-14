import kotlin.math.abs

data class GameState(
    var player1Points: Int = 0,
    var player2Points: Int = 0,
    var player1Games: Int = 0,
    var player2Games: Int = 0,
    var player1Sets: Int = 0,
    var player2Sets: Int = 0,
    var player1TiebreakPoints: Int = 0,
    var player2TiebreakPoints: Int = 0,
    var isTiebreak: Boolean = false,
    var servingPlayer: Int = 1, // 1 for Player 1, 2 for Player 2
    var isMatchOver: Boolean = false,
    var setsToWin: Int = 3
)

val scoreNames = listOf("0", "15", "30", "40")

fun getScoreName(points: Int): String = scoreNames[minOf(points, 3)]

fun isDeuce(state: GameState): Boolean =
    state.player1Points >= 3 && state.player1Points == state.player2Points

fun playerHasAdvantage(state: GameState): Boolean =
    abs(state.player1Points - state.player2Points) == 1 && (state.player1Points > 3 || state.player2Points > 3)

fun playerHasWonGame(state: GameState): Boolean =
    (state.player1Points >= 4 || state.player2Points >= 4) && abs(state.player1Points - state.player2Points) >= 2

fun playerHasWonSet(playerGames: Int, opponentGames: Int): Boolean =
    playerGames >= 6 && (playerGames - opponentGames) >= 2

fun isTiebreakCondition(state: GameState): Boolean =
    state.player1Games == 6 && state.player2Games == 6

fun playerHasWonTiebreak(state: GameState): Boolean =
    (state.player1TiebreakPoints >= 7 || state.player2TiebreakPoints >= 7) && Math.abs(state.player1TiebreakPoints - state.player2TiebreakPoints) >= 2

fun switchServer(state: GameState): GameState {
    state.servingPlayer = if (state.servingPlayer == 1) 2 else 1
    return state
}

fun scorePoint(state: GameState, player: Int): GameState {
    val newState = state.copy()

    if (newState.isTiebreak) {
        if (player == 1) newState.player1TiebreakPoints++ else newState.player2TiebreakPoints++

        if (playerHasWonTiebreak(newState)) {
            if (newState.player1TiebreakPoints > newState.player2TiebreakPoints) newState.player1Sets++ else newState.player2Sets++
            newState.player1Games = 0
            newState.player2Games = 0
            newState.player1TiebreakPoints = 0
            newState.player2TiebreakPoints = 0
            newState.isTiebreak = false
        } else {
            val totalTiebreakPoints =
                newState.player1TiebreakPoints + newState.player2TiebreakPoints
            if (totalTiebreakPoints % 2 == 1) switchServer(newState)
        }
    } else {
        if (player == 1) newState.player1Points++ else newState.player2Points++

        if (playerHasWonGame(newState)) {
            if (newState.player1Points > newState.player2Points) newState.player1Games++ else newState.player2Games++
            newState.player1Points = 0
            newState.player2Points = 0

            if (isTiebreakCondition(newState)) {
                newState.isTiebreak = true
                switchServer(newState)
            } else {
                if (playerHasWonSet(newState.player1Games, newState.player2Games)) {
                    newState.player1Sets++
                    newState.player1Games = 0
                    newState.player2Games = 0
                } else if (playerHasWonSet(newState.player2Games, newState.player1Games)) {
                    newState.player2Sets++
                    newState.player1Games = 0
                    newState.player2Games = 0
                }
                switchServer(newState)
            }
        }
    }

    return newState
}

fun decreasePoint(state: GameState, player: Int): GameState {
    val newState = state.copy()

    // Handle tiebreak logic separately
    if (newState.isTiebreak) {
        if (player == 1 && newState.player1TiebreakPoints > 0) {
            newState.player1TiebreakPoints--
        } else if (player == 2 && newState.player2TiebreakPoints > 0) {
            newState.player2TiebreakPoints--
        }
    } else {
        // Regular game point decrement
        if (player == 1 && newState.player1Points > 0) {
            newState.player1Points--
        } else if (player == 2 && newState.player2Points > 0) {
            newState.player2Points--
        }
    }

    return newState
}

fun getScore(state: GameState): String {
    if (state.player1Sets == state.setsToWin) return "Player 1 wins the match"
    if (state.player2Sets == state.setsToWin) return "Player 2 wins the match"

    if (state.isTiebreak) return "Tiebreak: ${state.player1TiebreakPoints} - ${state.player2TiebreakPoints}, Serving: Player ${state.servingPlayer}"
    if (isDeuce(state)) return "Deuce, Serving: Player ${state.servingPlayer}"
    if (playerHasAdvantage(state)) return "Advantage ${if (state.player1Points > state.player2Points) "Player 1" else "Player 2"}, Serving: Player ${state.servingPlayer}"

    val gameScore = "${getScoreName(state.player1Points)} - ${getScoreName(state.player2Points)}"
    val setScore =
        "${state.player1Games} - ${state.player2Games} (Sets: ${state.player1Sets} - ${state.player2Sets})"
    return "$setScore, Current Game: $gameScore, Serving: Player ${state.servingPlayer}"
}