package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.aleksanderjess.tenniscounter.annotations.SmallRoundWearPreview
import com.aleksanderjess.tenniscounter.annotations.SquareWearPreview

@Composable
fun MatchHistoryScreen(navController: NavController) {
    val matchHistory = listOf(
        "Player 1 vs Player 2: 6-3, 6-4",
        "Player 1 vs Player 3: 7-5, 6-7, 7-6",
        "Player 2 vs Player 3: 6-2, 6-3"
    )

    Scaffold(
        timeText = {
            TimeText(
                modifier = Modifier.padding(end = 15.dp)
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(matchHistory.size) { match ->
                    Text(matchHistory[match], modifier = Modifier.padding(8.dp))
                }
            }
            Button(
                onClick = { navController.navigate("set_wizard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Start New Match")
            }
        }

    }
}

@SquareWearPreview
@SmallRoundWearPreview
@Composable
fun MatchHistoryScreenPreview() {
    return MatchHistoryScreen(rememberNavController())
}