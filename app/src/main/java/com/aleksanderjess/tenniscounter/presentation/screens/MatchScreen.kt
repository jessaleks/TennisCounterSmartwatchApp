package com.aleksanderjess.tenniscounter.presentation.screens

import GameState
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.aleksanderjess.tenniscounter.annotations.SmallRoundWearPreview
import com.aleksanderjess.tenniscounter.annotations.SquareWearPreview

import decreasePoint
import getScore
import scorePoint

@Composable
fun MatchScreen(navController: NavHostController) {
    var gameState by remember { mutableStateOf(GameState()) }

    // if the screen is round, set the variable padding to 15.dp, otherwise set it to 5.dp
    var padding = 5.dp
    if (Configuration().isScreenRound) {
        padding = 15.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            getScore(gameState),
            modifier = Modifier.padding(10.dp),
            style = MaterialTheme.typography.bodySmall
        )

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
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
    return MatchScreen(rememberNavController());
}