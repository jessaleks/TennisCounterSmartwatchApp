import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.aleksanderjess.tenniscounter.presentation.lib.GameState
import com.aleksanderjess.tenniscounter.presentation.lib.decreasePoint
import com.aleksanderjess.tenniscounter.presentation.lib.getScore
import com.aleksanderjess.tenniscounter.presentation.lib.scorePoint
import com.aleksanderjess.tenniscounter.presentation.theme.TennisCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TennisCounterTheme {
                TennisCounterApp()
            }
        }
    }
}

@Composable
fun TennisCounterApp() {
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
            Text(text = getScore(gameState), style = MaterialTheme.typography.body2)

            Spacer(modifier = Modifier.height(24.dp))

            // Player 1 point buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    shape = RoundedCornerShape(5),
                    onClick = { gameState = scorePoint(gameState, 1) }) {
                    Text("P1+")
                }
                Button(
                    shape = RoundedCornerShape(5),
                    onClick = { gameState = scorePoint(gameState, 2) }) {
                    Text("P2+")
                }

            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    shape = RoundedCornerShape(5),
                    onClick = { gameState = decreasePoint(gameState, 1) }) {
                    Text("P1-")
                }

                Button(
                    shape = RoundedCornerShape(5),
                    onClick = { gameState = decreasePoint(gameState, 2) }) {
                    Text("P2-")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset game button
            Button(onClick = { gameState = GameState() }) {
                Text("Reset Game")
            }
        }
    }
}

@Preview()
@Composable()
fun TennisCounterAppPreview() {
    TennisCounterApp()
}