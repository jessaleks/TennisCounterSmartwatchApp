package com.aleksanderjess.tenniscounter.presentation.screens

import GameState
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material3.Button


import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.TimeText
import com.aleksanderjess.tenniscounter.annotations.SmallRoundWearPreview
import com.aleksanderjess.tenniscounter.annotations.SquareWearPreview
import decreasePoint
import getScore
import scorePoint

@Composable
fun MatchScreen(navController: NavHostController, setsToWin: Int = 2) {
    var gameState by remember { mutableStateOf(GameState()) }
    var padding: Dp = 0.dp

    if (gameState.isMatchOver) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Match Over", modifier = Modifier.padding(10.dp))

            Button(onClick = {
                gameState = GameState()
            }) {
                Text("New Match")
            }
        }
    }
    padding = if (Configuration().isScreenRound) {
        16.dp
    } else {
        5.dp
    }
    TimeText(
        timeTextStyle = MaterialTheme.typography.bodySmall,
        contentPadding = PaddingValues(2.dp)
    ) {
        time()
    }
    // if the screen is round, set the variable padding to 15.dp, otherwise set it to 5.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.Center
    ) {
        Text(getScore(gameState), modifier = Modifier.padding(padding))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { gameState = scorePoint(gameState, 1) }) {
                    Text("P1+")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { gameState = decreasePoint(gameState, 1) }) {
                    Text("P1-")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { gameState = scorePoint(gameState, 2) }) {
                    Text("P2+")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { gameState = decreasePoint(gameState, 2) }) {
                    Text("P2-")
                }
            }
        }
    }
}

@SquareWearPreview
@SmallRoundWearPreview
@Composable
fun MatchScreenPreview() {
    return MatchScreen(rememberNavController(), 3)
}