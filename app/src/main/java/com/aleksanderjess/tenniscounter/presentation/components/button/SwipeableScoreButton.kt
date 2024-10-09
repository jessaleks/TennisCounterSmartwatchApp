package com.aleksanderjess.tenniscounter.presentation.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
    totalSets: Int,
    isServing: Boolean,

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
                        .size(30.dp)
                        .padding(2.dp)
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
                text = if (isServing) {
                    "🟢$playerName"
                } else {
                    playerName
                },
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
        onSwipeUp = {},
        onSwipeDown = { },
        currentSets = 1,
        totalSets = 3,
        isServing = false,

        )
}