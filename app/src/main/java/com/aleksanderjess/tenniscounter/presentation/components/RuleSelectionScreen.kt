package com.aleksanderjess.tenniscounter.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text

@Composable
fun RuleSelectionScreen(navController: NavController) {
    var selectedSetCount by remember { mutableIntStateOf(3) }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Select Number of Sets to Win", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(16.dp))

            // Radio buttons for set selection (best of 3 or best of 5)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selectedSetCount == 3, onClick = { selectedSetCount = 3 })
                Text(text = "Best of 3 sets")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selectedSetCount == 5, onClick = { selectedSetCount = 5 })
                Text(text = "Best of 5 sets")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start game button
            Button(onClick = {
                navController.navigate("tennisCounter/$selectedSetCount")
            }) {
                Text("Start Game")
            }
        }
    }
}

//@Preview
//@Composable
//fun RuleSelectionScreenPreview() {
//    return RuleSelectionScreen(
//        navController = NavController();
//    )
//}