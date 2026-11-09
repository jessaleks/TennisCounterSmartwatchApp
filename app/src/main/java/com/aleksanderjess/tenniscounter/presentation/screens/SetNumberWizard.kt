package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aleksanderjess.tenniscounter.R
import com.aleksanderjess.tenniscounter.annotations.SmallRoundWearPreview
import com.aleksanderjess.tenniscounter.annotations.SquareWearPreview
import com.aleksanderjess.tenniscounter.presentation.Screen
import com.aleksanderjess.tenniscounter.presentation.components.FitText
import com.aleksanderjess.tenniscounter.presentation.components.RoundSafeColumn
import com.aleksanderjess.tenniscounter.presentation.theme.TennisYellow

private val matchFormats = listOf(
    R.string.format_one_set,
    R.string.format_best_of_three,
    R.string.format_best_of_five,
)

private val setupRowHeights = listOf(22.dp, 48.dp, 32.dp, 48.dp)

@Composable
fun SetWizardScreen(navController: NavHostController) {
    var setsToWin by rememberSaveable { mutableIntStateOf(1) }

    RoundSafeColumn(rowHeights = setupRowHeights, gap = 4.dp) { index, rowModifier ->
        when (index) {
            0 -> FitText(
                text = stringResource(R.string.sets_to_win),
                color = Color.White,
                maxFontSize = 16.sp,
                minFontSize = 8.sp,
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                modifier = rowModifier,
            )
            1 -> Row(
                modifier = rowModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                StepperButton(
                    symbol = "−",
                    contentDescription = stringResource(R.string.decrease_sets),
                    enabled = setsToWin > 1,
                    onClick = { setsToWin -= 1 },
                    modifier = Modifier.testTag("decrease_sets_button"),
                )
                FitText(
                    text = "$setsToWin",
                    color = Color.White,
                    maxFontSize = 32.sp,
                    minFontSize = 16.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(28.dp),
                    testTag = "sets_to_win_value",
                )
                // Update the index
                StepperButton(
                    symbol = "+",
                    contentDescription = stringResource(R.string.increase_sets),
                    enabled = setsToWin < 3,
                    onClick = { setsToWin += 1 },
                    modifier = Modifier.testTag("increase_sets_button"),
                )
            }
            2 -> FitText(
                text = stringResource(matchFormats[setsToWin - 1]),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxFontSize = 12.sp,
                minFontSize = 10.sp,
                maxLines = 2,
                modifier = rowModifier,
                testTag = "format_description",
            )
            else -> Button(
                onClick = {
                    navController.navigate(Screen.MatchScreen.createRoute(setsToWin))
                },
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = rowModifier.testTag("start_match_button"),
            ) {
                FitText(
                    text = stringResource(R.string.start_match),
                    color = Color.Black,
                    maxFontSize = 14.sp,
                    minFontSize = 8.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun StepperButton(
    symbol: String,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TennisYellow,
        ),
        modifier = modifier
            .width(48.dp)
            .height(48.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        FitText(
            text = symbol,
            color = TennisYellow,
            maxFontSize = 20.sp,
            minFontSize = 14.sp,
            maxLines = 1,
        )
    }
}

@SquareWearPreview
@SmallRoundWearPreview
@Composable
fun SetWizardScreenPreview() {
    return (SetWizardScreen(rememberNavController()))
}
