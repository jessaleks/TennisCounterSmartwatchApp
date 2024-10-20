package com.aleksanderjess.tenniscounter.presentation.screens

import GameState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aleksanderjess.tenniscounter.annotations.SmallRoundWearPreview
import com.aleksanderjess.tenniscounter.annotations.SquareWearPreview
import com.aleksanderjess.tenniscounter.presentation.Screen
import decreasePoint
import getScore
import scorePoint

@Composable
fun MatchScreen(navController: NavController) {
    var gameState by remember { mutableStateOf(GameState()) }

    if (gameState.isMatchOver) {
        // Display match winner
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = getScore(gameState), style = MaterialTheme.typography.titleLarge
            )
            Button(
                onClick = { navController.navigate(Screen.SetWizard) },

                ) {
                Text(
                    text = "Start New Match", style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row {
            Button(
                onClick = { gameState = scorePoint(gameState, 1) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("P1+")
            }
            Button(
                onClick = { gameState = decreasePoint(gameState, 1) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("P1-")
            }
        }


        // Match Score Table
        ScoreTable(gameState)


        Row {
            Button(
                onClick = { gameState = scorePoint(gameState, 2) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("P2+")
            }
            Button(
                onClick = { gameState = scorePoint(gameState, 2) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("P2-")
            }
        }
    }
}

@Composable
fun ScoreTable(gameState: GameState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row for Player 1's scores
        PlayerScoreRow(
            player = "Player 1",
            currentPoints = gameState.player1Points,
            games = listOf(gameState.player1Games), // List of set games
            serving = gameState.servingPlayer == 1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Row for Player 2's scores
        PlayerScoreRow(
            player = "Player 2",
            currentPoints = gameState.player2Points,
            games = listOf(gameState.player2Games), // List of set games
            serving = gameState.servingPlayer == 2
        )
    }
}

@Composable
fun PlayerScoreRow(
    player: String, currentPoints: Int, games: List<Int>, // List of games per set
    serving: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Player name with serving indicator
        Text(
            text = "$player ${if (serving) "🎾" else ""}",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        // Current game score
        Text(
            text = "Points: $currentPoints",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        // Display set scores (games won in each set)
        games.forEachIndexed { index, gameScore ->
            Text(
                text = "Set ${index + 1}: $gameScore",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


@SquareWearPreview
@SmallRoundWearPreview
@Composable
fun MatchScreenPreview() {
    return MatchScreen(rememberNavController())
}