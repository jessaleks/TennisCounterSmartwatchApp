import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.aleksanderjess.tenniscounter.presentation.components.RuleSelectionScreen
//import com.aleksanderjess.tenniscounter.presentation.components.button.SwipeScoreButtons
import com.aleksanderjess.tenniscounter.presentation.lib.GameState
import com.aleksanderjess.tenniscounter.presentation.lib.getScoreName
import com.aleksanderjess.tenniscounter.presentation.lib.isDeuce
import com.aleksanderjess.tenniscounter.presentation.lib.playerHasAdvantage
import com.aleksanderjess.tenniscounter.presentation.theme.TennisCounterTheme

@Composable
fun TennisApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "ruleSelection") {
        composable("ruleSelection") {
            RuleSelectionScreen(navController)
        }
        composable("tennisCounter/{setCount}") { backStackEntry ->
            val setCount = backStackEntry.arguments?.getString("setCount")?.toInt() ?: 3
            TennisCounterApp(setCount = setCount)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TennisCounterTheme {
                TennisApp()
            }
        }
    }
}

@Composable
fun TennisCounterApp(setCount: Int) {
    var gameState by remember { mutableStateOf(GameState()) }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // Display game score
            Text(text = getScore(gameState, setCount), style = MaterialTheme.typography.body2)

//            SwipeScoreButtons(
//                0,
//                0,
//                onPlayer1ScoreChange = { it: Int -> it.inv() },
//
//                onPlayer2ScoreChange = { it: Int -> it.inv() }
//            )


            // Reset game button
            LargeButton(
                onClick = { gameState = GameState() },
                text = "Reset"
            )
        }
    }
}

fun getScore(state: GameState, setCount: Int): String {
    // Check if someone has won the match based on the selected setCount
    if (state.player1Sets == setCount) return "Player 1 wins the match"
    if (state.player2Sets == setCount) return "Player 2 wins the match"

    // Check if tiebreak is ongoing
    if (state.isTiebreak) {
        return "Tiebreak: ${state.player1TiebreakPoints} - ${state.player2TiebreakPoints}, Serving: Player ${state.servingPlayer}"
    }

    // Check if it’s deuce
    if (isDeuce(state)) return "Deuce, Serving: Player ${state.servingPlayer}"

    // Check for advantage
    if (playerHasAdvantage(state)) return "${if (state.player1Points > state.player2Points) "Advantage Player 1" else "Advantage Player 2"}, Serving: Player ${state.servingPlayer}"

    // Normal game score
    val gameScore = "${getScoreName(state.player1Points)} - ${getScoreName(state.player2Points)}"
    val setScore =
        "${state.player1Games} - ${state.player2Games} (Sets: ${state.player1Sets} - ${state.player2Sets})"
    return "$setScore,\n Current Game: $gameScore,\n Serving: Player ${state.servingPlayer}\n"
}

@Preview(
    device = WearDevices.SMALL_ROUND
)
@Composable()
fun TennisCounterAppPreview() {
    TennisCounterApp(3)
}