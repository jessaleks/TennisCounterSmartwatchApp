package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.collection.intListOf
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Slider
import androidx.wear.compose.material3.Text
import com.aleksanderjess.tenniscounter.annotations.SmallRoundWearPreview
import com.aleksanderjess.tenniscounter.annotations.SquareWearPreview
import com.aleksanderjess.tenniscounter.presentation.Screen

@Composable
fun SetWizardScreen(navController: NavHostController) {
    val setValues = intListOf(1, 3, 5)
    var setIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Sets: ${setValues[setIndex]}",
            modifier = Modifier.padding(5.dp),
            style = MaterialTheme.typography.titleLarge
        )
        Slider(
            value = setIndex.toFloat(),
            onValueChange = { setIndex = it.toInt() }, // Update the index
            valueRange = 0f..2f,
            steps = 1,
            modifier = Modifier.padding(10.dp),

            )


        Button(

            onClick = {
                navController.navigate(Screen.MatchScreen.route)
            }) {
            Text("Start Match")
        }
    }
}

@SquareWearPreview
@SmallRoundWearPreview
@Composable
fun SetWizardScreenPreview() {
    return (SetWizardScreen(rememberNavController()))
}