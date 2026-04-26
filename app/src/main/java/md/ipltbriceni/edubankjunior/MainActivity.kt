package md.ipltbriceni.edubankjunior

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import md.ipltbriceni.edubankjunior.data.model.QuizViewModel
import md.ipltbriceni.edubankjunior.ui.theme.EduBankJuniorTheme
import md.ipltbriceni.edubankjunior.data.model.TicketsViewModel
import md.ipltbriceni.edubankjunior.data.repository.LocalQuizRepository
import md.ipltbriceni.edubankjunior.data.repository.QuizRepository
import md.ipltbriceni.edubankjunior.ui.screens.QuizScreen
import md.ipltbriceni.edubankjunior.ui.screens.TicketListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = remember { QuizRepository() }

            EduBankJuniorTheme(dynamicColor = false) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "dashboard"
                ) {
                    composable("dashboard") {
                        val ticketsViewModel = remember {TicketsViewModel(repository)}
                        TicketListScreen(
                            ticketsViewModel,
                            onTicketClick = { ticketId ->
                                navController.navigate("quiz/$ticketId")

                            }
                        )
                    }

                    composable(
                        route = "quiz/{ticketId}",
                        arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val ticketId = backStackEntry.arguments?.getInt("ticketId") ?: 0
                        val quizViewModel = remember { QuizViewModel(repository) }
                        quizViewModel.loadQuestions(ticketId)
                        QuizScreen(ticketId = ticketId, quizViewModel, {
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}