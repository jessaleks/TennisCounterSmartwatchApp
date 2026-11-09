package com.aleksanderjess.tenniscounter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sqrt

@Composable
fun RoundSafeColumn(
    rowHeights: List<Dp>,
    gap: Dp = 2.dp,
    content: @Composable (index: Int, rowModifier: Modifier) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val radius = minOf(maxWidth, maxHeight) / 2
        val stackHeight = rowHeights.fold(0.dp) { acc, h -> acc + h } + gap * (rowHeights.size - 1)
        val centerY = maxHeight / 2
        var rowTop = centerY - stackHeight / 2
        rowHeights.forEachIndexed { index, rowHeight ->
            val rowBottom = rowTop + rowHeight
            val farthest = maxOf(
                abs((rowTop - centerY).value),
                abs((rowBottom - centerY).value),
            )
            val chord = 2 * sqrt((radius.value * radius.value - farthest * farthest).toDouble().coerceAtLeast(0.0))
            val safeWidth = (chord.dp - 12.dp).coerceIn(0.dp, maxWidth)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = rowTop)
                    .width(safeWidth)
                    .height(rowHeight)
            ) {
                content(index, Modifier.fillMaxSize())
            }
            rowTop = rowBottom + gap
        }
    }
}

@Composable
fun FitText(
    text: String,
    color: Color,
    maxFontSize: TextUnit,
    minFontSize: TextUnit,
    maxLines: Int,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign = TextAlign.Center,
    testTag: String? = null,
) {
    BasicText(
        text = text,
        style = TextStyle(
            color = color,
            fontWeight = fontWeight,
            textAlign = textAlign,
        ),
        maxLines = maxLines,
        autoSize = TextAutoSize.StepBased(
            minFontSize = minFontSize,
            maxFontSize = maxFontSize,
        ),
        modifier = if (testTag != null) modifier.testTag(testTag) else modifier,
    )
}
