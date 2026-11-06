package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aleksanderjess.tenniscounter.annotations.SmallRoundWearPreview
import com.aleksanderjess.tenniscounter.annotations.SquareWearPreview
import com.aleksanderjess.tenniscounter.lib.GameState
import com.aleksanderjess.tenniscounter.lib.decreasePoint
import com.aleksanderjess.tenniscounter.lib.getScore
import com.aleksanderjess.tenniscounter.lib.scorePoint
import com.aleksanderjess.tenniscounter.presentation.Screen

@Composable
fun MatchScreen(navController: NavController, setsToWin: Int = 3) {
    var gameState by remember { mutableStateOf(GameState(setsToWin = setsToWin)) }

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
                onClick = { navController.navigate(Screen.SetWizard.route) },

                ) {
                Text(
                    text = "Start New Match", style = MaterialTheme.typography.titleMedium
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            ScoreButtons(
                plusLabel = "P1+",
                minusLabel = "P1-",
                onPlus = { gameState = scorePoint(gameState, 1) },
                onMinus = { gameState = decreasePoint(gameState, 1) },
            )
            ScoreTable(gameState)
            ScoreButtons(
                plusLabel = "P2+",
                minusLabel = "P2-",
                onPlus = { gameState = scorePoint(gameState, 2) },
                onMinus = { gameState = decreasePoint(gameState, 2) },
            )
        }
    }
}

@Composable
private fun ScoreButtons(
    plusLabel: String,
    minusLabel: String,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        ScoreButton(plusLabel, onPlus)
        ScoreButton(minusLabel, onMinus)
    }
}

@Composable
private fun ScoreButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
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

        Spacer(modifier = Modifier.height(2.dp))

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
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$player ${if (serving) "🎾" else ""}",
                modifier = Modifier.testTag(scoreTag(player, "name")),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            games.forEachIndexed { index, gameScore ->
                Text(
                    text = "Set ${index + 1}: $gameScore",
                    modifier = Modifier.testTag(scoreTag(player, "set${index + 1}")),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
        Text(
            text = "Points: $currentPoints",
            modifier = Modifier.testTag(scoreTag(player, "points")),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
    }
}


@SquareWearPreview
@SmallRoundWearPreview
@Composable
fun MatchScreenPreview() {
    return MatchScreen(rememberNavController())
}

private fun scoreTag(player: String, part: String): String =
    "${player.replace(" ", "").lowercase()}_$part"