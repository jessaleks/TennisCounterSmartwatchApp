package com.aleksanderjess.tenniscounter.presentation.lib

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
    var servingPlayer: Int = 1 // 1 for Player 1, 2 for Player 2
)

val scoreNames = listOf("0", "15", "30", "40")

fun getScoreName(points: Int): String {
    return scoreNames[minOf(points, 3)]
}

fun isDeuce(state: GameState): Boolean {
    return state.player1Points >= 3 && state.player1Points == state.player2Points
}

fun playerHasAdvantage(state: GameState): Boolean {
    return kotlin.math.abs(state.player1Points - state.player2Points) == 1 && (state.player1Points > 3 || state.player2Points > 3)
}

fun playerHasWonGame(state: GameState): Boolean {
    return (state.player1Points >= 4 || state.player2Points >= 4) && kotlin.math.abs(state.player1Points - state.player2Points) >= 2
}

fun playerHasWonSet(playerGames: Int, opponentGames: Int): Boolean {
    return playerGames >= 6 && (playerGames - opponentGames) >= 2
}

fun isTiebreakCondition(state: GameState): Boolean {
    return state.player1Games == 6 && state.player2Games == 6
}

fun playerHasWonTiebreak(state: GameState): Boolean {
    return (state.player1TiebreakPoints >= 7 || state.player2TiebreakPoints >= 7) &&
            kotlin.math.abs(state.player1TiebreakPoints - state.player2TiebreakPoints) >= 2
}

fun switchServer(state: GameState): GameState {
    return state.copy(servingPlayer = if (state.servingPlayer == 1) 2 else 1)
}

fun decreasePoint(state: GameState, player: Int): GameState {
    val newState = state.copy()

    if (newState.isTiebreak) {
        // Tiebreak logic
        if (player == 1 && newState.player1TiebreakPoints > 0) {
            newState.player1TiebreakPoints--
        } else if (player == 2 && newState.player2TiebreakPoints > 0) {
            newState.player2TiebreakPoints--
        }
    } else {
        // Normal game logic
        if (player == 1 && newState.player1Points > 0) {
            newState.player1Points--
        } else if (player == 2 && newState.player2Points > 0) {
            newState.player2Points--
        }
    }

    return newState
}

fun scorePoint(state: GameState, player: Int): GameState {
    val newState = state.copy()

    // Tiebreak logic
    if (newState.isTiebreak) {
        if (player == 1) {
            newState.player1TiebreakPoints++
        } else {
            newState.player2TiebreakPoints++
        }

        // Check if someone has won the tiebreak and thus the set
        if (playerHasWonTiebreak(newState)) {
            if (newState.player1TiebreakPoints > newState.player2TiebreakPoints) {
                newState.player1Sets++
            } else {
                newState.player2Sets++
            }
            // Reset games, points, and exit tiebreak mode
            newState.player1Games = 0
            newState.player2Games = 0
            newState.player1TiebreakPoints = 0
            newState.player2TiebreakPoints = 0
            newState.isTiebreak = false
        } else {
            // Switch server after every odd point
            val totalTiebreakPoints = newState.player1TiebreakPoints + newState.player2TiebreakPoints
            if (totalTiebreakPoints % 2 == 1) {
                return switchServer(newState)
            }
        }
    } else {
        // Normal game logic
        if (player == 1) {
            newState.player1Points++
        } else {
            newState.player2Points++
        }

        // Check if someone has won the game
        if (playerHasWonGame(newState)) {
            if (newState.player1Points > newState.player2Points) {
                newState.player1Games++
            } else {
                newState.player2Games++
            }

            // Reset points after game is won
            newState.player1Points = 0
            newState.player2Points = 0

            // Check for tiebreak condition
            if (isTiebreakCondition(newState)) {
                newState.isTiebreak = true
                return switchServer(newState) // Switch server for tiebreak
            } else {
                // Check if someone has won the set
                if (playerHasWonSet(newState.player1Games, newState.player2Games)) {
                    newState.player1Sets++
                    newState.player1Games = 0
                    newState.player2Games = 0
                } else if (playerHasWonSet(newState.player2Games, newState.player1Games)) {
                    newState.player2Sets++
                    newState.player1Games = 0
                    newState.player2Games = 0
                }
                return switchServer(newState)
            }
        }
    }

    return newState
}

fun getScore(state: GameState): String {
    // Check if someone has won the match
    if (state.player1Sets == 3) return "Player 1 wins the match"
    if (state.player2Sets == 3) return "Player 2 wins the match"

    // Check if tiebreak is ongoing
    if (state.isTiebreak) {
        return "Tiebreak: ${state.player1TiebreakPoints} - ${state.player2TiebreakPoints}, Serving: Player ${state.servingPlayer}"
    }

    // Check if it’s deuce
    if (isDeuce(state)) return "Deuce, Serving: Player ${state.servingPlayer}"

    // Check for advantage
    if (playerHasAdvantage(state)) return "${if (state.player1Points > state.player2Points) "Advantage Player 1" else "Advantage Player 2"}, Serving: Player ${state.servingPlayer}"

    // Normal game score
    val gameScore = "${getScoreName(state.player1Points)} - ${getScoreName(state.player2Points)}"
    val setScore = "${state.player1Games} - ${state.player2Games} (Sets: ${state.player1Sets} - ${state.player2Sets})"
    return "$setScore, \n Current Game: $gameScore, \n Serving: Player ${state.servingPlayer}"
}