import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aleksanderjess.tenniscounter.presentation.Screen
import com.aleksanderjess.tenniscounter.presentation.screens.MatchHistoryScreen
import com.aleksanderjess.tenniscounter.presentation.screens.MatchScreen
import com.aleksanderjess.tenniscounter.presentation.screens.SetWizardScreen
import com.aleksanderjess.tenniscounter.presentation.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                WearTennisApp(navController)
            }
        }
    }
}

@Composable
fun WearTennisApp(navController: NavHostController) {
    AppTheme {
        NavHost(navController = navController, startDestination = Screen.MatchHistory.route) {
            composable(Screen.MatchHistory.route) {
                MatchHistoryScreen(navController)
            }
            composable(Screen.SetWizard.route) { SetWizardScreen(navController) }
            composable(Screen.MatchScreen.route) { MatchScreen(navController) }
        }
    }
}