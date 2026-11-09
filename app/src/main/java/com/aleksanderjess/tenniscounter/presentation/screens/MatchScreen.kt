package com.aleksanderjess.tenniscounter.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aleksanderjess.tenniscounter.R
import com.aleksanderjess.tenniscounter.annotations.SmallRoundWearPreview
import com.aleksanderjess.tenniscounter.annotations.SquareWearPreview
import com.aleksanderjess.tenniscounter.lib.GameState
import com.aleksanderjess.tenniscounter.lib.MatchSession
import com.aleksanderjess.tenniscounter.lib.awardPoint
import com.aleksanderjess.tenniscounter.lib.getPointLabel
import com.aleksanderjess.tenniscounter.lib.isDeuce
import com.aleksanderjess.tenniscounter.lib.undoPoint
import com.aleksanderjess.tenniscounter.presentation.Screen
import com.aleksanderjess.tenniscounter.presentation.components.FitText
import com.aleksanderjess.tenniscounter.presentation.components.RoundSafeColumn
import com.aleksanderjess.tenniscounter.presentation.theme.NeutralSecondary
import com.aleksanderjess.tenniscounter.presentation.theme.ScoreBorderGray
import com.aleksanderjess.tenniscounter.presentation.theme.TennisYellow
import com.aleksanderjess.tenniscounter.presentation.theme.TileSurface

private const val GAME_STATE_FIELD_COUNT = 13

private fun GameState.toIntList(): List<Int> = listOf(
    player1Points,
    player2Points,
    player1Games,
    player2Games,
    player1Sets,
    player2Sets,
    player1TiebreakPoints,
    player2TiebreakPoints,
    if (isTiebreak) 1 else 0,
    servingPlayer,
    tiebreakFirstServer,
    if (isMatchOver) 1 else 0,
    setsToWin,
)

private fun List<Int>.toGameState(offset: Int): GameState = GameState(
    player1Points = this[offset],
    player2Points = this[offset + 1],
    player1Games = this[offset + 2],
    player2Games = this[offset + 3],
    player1Sets = this[offset + 4],
    player2Sets = this[offset + 5],
    player1TiebreakPoints = this[offset + 6],
    player2TiebreakPoints = this[offset + 7],
    isTiebreak = this[offset + 8] != 0,
    servingPlayer = this[offset + 9],
    tiebreakFirstServer = this[offset + 10],
    isMatchOver = this[offset + 11] != 0,
    setsToWin = this[offset + 12],
)

val MatchSessionSaver: Saver<MatchSession, List<Int>> = Saver(
    save = { session ->
        buildList {
            add(session.previousStates.size)
            session.previousStates.forEach { addAll(it.toIntList()) }
            addAll(session.current.toIntList())
        }
    },
    restore = { list ->
        val count = list[0]
        val previous = (0 until count).map { i ->
            list.toGameState(1 + i * GAME_STATE_FIELD_COUNT)
        }
        MatchSession(
            current = list.toGameState(1 + count * GAME_STATE_FIELD_COUNT),
            previousStates = previous,
        )
    },
)

private val matchRowHeights = listOf(18.dp, 80.dp, 18.dp, 48.dp)
private val resultRowHeights = listOf(56.dp, 20.dp, 48.dp)

internal fun scoreTileBorderColor(serving: Boolean): Color =
    if (serving) TennisYellow else ScoreBorderGray

@Composable
fun MatchScreen(navController: NavController, setsToWin: Int = 3) {
    var session by rememberSaveable(stateSaver = MatchSessionSaver) {
        mutableStateOf(MatchSession(GameState(setsToWin = setsToWin)))
    }
    val haptics = LocalHapticFeedback.current
    val state = session.current

    if (state.isMatchOver) {
        RoundSafeColumn(rowHeights = resultRowHeights, gap = 4.dp) { index, rowModifier ->
            // Display match winner
            WinnerRow(
                index = index,
                rowModifier = rowModifier,
                state = state,
                canUndo = session.previousStates.isNotEmpty(),
                onUndo = { session = undoPoint(session) },
                onNewMatch = {
                    navController.navigate(Screen.SetWizard.route) {
                        popUpTo(0)
                    }
                },
            )
        }
    } else {
        RoundSafeColumn(rowHeights = matchRowHeights, gap = 2.dp) { index, rowModifier ->
            MatchRow(
                index = index,
                rowModifier = rowModifier,
                state = state,
                canUndo = session.previousStates.isNotEmpty(),
                onPoint = { player ->
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    session = awardPoint(session, player)
                },
                onUndo = { session = undoPoint(session) },
            )
        }
    }
}

@Composable
private fun MatchRow(
    index: Int,
    rowModifier: Modifier,
    state: GameState,
    canUndo: Boolean,
    onPoint: (Int) -> Unit,
    onUndo: () -> Unit,
) {
    when (index) {
        0 -> if (state.isTiebreak || isDeuce(state)) {
            FitText(
                text = stringResource(if (state.isTiebreak) R.string.tiebreak else R.string.deuce),
                color = TennisYellow,
                maxFontSize = 12.sp,
                minFontSize = 9.sp,
                maxLines = 1,
                modifier = rowModifier,
            )
        } else {
            Spacer(modifier = rowModifier)
        }
        1 -> Row(
            modifier = rowModifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Row for Player 1's scores
            ScoreTile(
                player = 1,
                label = getPointLabel(state, 1),
                serving = state.servingPlayer == 1,
                onClick = { onPoint(1) },
                modifier = Modifier.weight(1f),
            )
            // Row for Player 2's scores
            // List of set games
            ScoreTile(
                player = 2,
                label = getPointLabel(state, 2),
                serving = state.servingPlayer == 2,
                onClick = { onPoint(2) },
                modifier = Modifier.weight(1f),
            )
        }
        2 -> // List of set games
            FitText(
                text = stringResource(
                    R.string.match_stats,
                    state.player1Games,
                    state.player2Games,
                    state.player1Sets,
                    state.player2Sets,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxFontSize = 12.sp,
                minFontSize = 9.sp,
                maxLines = 1,
                modifier = rowModifier,
                testTag = "match_stats",
            )
        else -> Box(modifier = rowModifier, contentAlignment = Alignment.Center) {
            OutlinedButton(
                onClick = onUndo,
                enabled = canUndo,
                contentPadding = PaddingValues(horizontal = 12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TennisYellow,
                ),
                modifier = Modifier
                    .width(80.dp)
                    .height(48.dp)
                    .testTag("undo_button"),
            ) {
                FitText(
                    text = stringResource(R.string.undo),
                    color = TennisYellow,
                    maxFontSize = 14.sp,
                    minFontSize = 10.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ScoreTile(
    player: Int,
    label: String,
    serving: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tileContentDescription = stringResource(
        if (serving) R.string.award_point_serving else R.string.award_point,
        player,
    )
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(TileSurface)
            .border(1.dp, scoreTileBorderColor(serving), RoundedCornerShape(16.dp))
            .semantics {
                role = Role.Button
                contentDescription = tileContentDescription
            }
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .testTag("player${player}_score_tile"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.height(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // List of games per set
            FitText(
                text = stringResource(R.string.player_label, player),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxFontSize = 11.sp,
                minFontSize = 8.sp,
                maxLines = 1,
            )
            if (serving) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(TennisYellow, CircleShape)
                        .testTag("player${player}_serving_dot")
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            FitText(
                text = label,
                color = Color.White,
                maxFontSize = 34.sp,
                minFontSize = 20.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("player${player}_points"),
            )
        }
        FitText(
            text = stringResource(R.string.add_point_hint),
            color = if (serving) TennisYellow else MaterialTheme.colorScheme.onSurfaceVariant,
            maxFontSize = 11.sp,
            minFontSize = 8.sp,
            maxLines = 1,
            modifier = Modifier.height(14.dp),
        )
    }
}

@Composable
private fun WinnerRow(
    index: Int,
    rowModifier: Modifier,
    state: GameState,
    canUndo: Boolean,
    onUndo: () -> Unit,
    onNewMatch: () -> Unit,
) {
    val winner = if (state.player1Sets > state.player2Sets) 1 else 2
    when (index) {
        0 -> FitText(
            text = stringResource(R.string.match_winner, winner),
            color = Color.White,
            maxFontSize = 22.sp,
            minFontSize = 14.sp,
            maxLines = 2,
            fontWeight = FontWeight.Medium,
            modifier = rowModifier,
        )
        1 -> FitText(
            text = stringResource(R.string.sets_result, state.player1Sets, state.player2Sets),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxFontSize = 14.sp,
            minFontSize = 10.sp,
            maxLines = 1,
            modifier = rowModifier,
        )
        else -> Row(
            modifier = rowModifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
        Button(
            onClick = onNewMatch,
            contentPadding = PaddingValues(horizontal = 8.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("new_match_button"),
        ) {
            FitText(
                text = stringResource(R.string.new_match),
                color = Color.Black,
                maxFontSize = 14.sp,
                minFontSize = 10.sp,
                maxLines = 2,
            )
        }
        OutlinedButton(
            onClick = onUndo,
            enabled = canUndo,
            contentPadding = PaddingValues(horizontal = 8.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("undo_button"),
        ) {
            FitText(
                text = stringResource(R.string.undo),
                color = NeutralSecondary,
                maxFontSize = 14.sp,
                minFontSize = 10.sp,
                maxLines = 1,
            )
        }
    }
    }
}

@SquareWearPreview
@SmallRoundWearPreview
@Composable
fun MatchScreenPreview() {
    return MatchScreen(rememberNavController())
}
