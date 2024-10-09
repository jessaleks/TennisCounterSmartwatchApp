package com.aleksanderjess.tenniscounter.presentation.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun PlayerButtonWithSwipe(
    playerName: String,
    modifier: Modifier = Modifier,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    currentSets: Int,
    totalSets: Int
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display set wins as boxes above the button
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(totalSets) { index ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp)
                        .background(
                            color = if (index < currentSets) Color.Green else Color.Gray,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // The button area for swipe gesture detection
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colors.primary, shape = RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                    detectVerticalDragGestures(

                        onVerticalDrag = { change, dragAmount ->
                            if (dragAmount < -10) {
                                // Swiped up
                                onSwipeUp()
                            } else if (dragAmount > 10) {
                                // Swiped down
                                onSwipeDown()
                            }
                            change.consume()
                        }
                    )
                }
        ) {
            Text(
                text = playerName,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND)
@Composable
fun PreviewSwipeableScoreButtons() {
    return PlayerButtonWithSwipe(
        "1",
        onSwipeUp = { -> it.inc() },
        onSwipeDown = TODO(),
        currentSets = TODO(),
        totalSets = TODO(),
    )
}