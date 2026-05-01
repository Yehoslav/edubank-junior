package md.ipltbriceni.edubankjunior

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import md.ipltbriceni.edubankjunior.data.model.AuthViewModel
import md.ipltbriceni.edubankjunior.data.model.QuizViewModel
import md.ipltbriceni.edubankjunior.ui.theme.EduBankJuniorTheme
import md.ipltbriceni.edubankjunior.data.model.TicketsViewModel
import md.ipltbriceni.edubankjunior.data.repository.QuizRepository
import md.ipltbriceni.edubankjunior.ui.screens.AuthScreen
import md.ipltbriceni.edubankjunior.ui.screens.DashboardScreen
import md.ipltbriceni.edubankjunior.ui.screens.QuizScreen
import md.ipltbriceni.edubankjunior.ui.screens.TicketListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Firebase.firestore
        val auth = Firebase.auth

        enableEdgeToEdge()
        setContent {
            val repository = remember { QuizRepository(auth, db) }

            EduBankJuniorTheme {
                val navController = rememberNavController()
                val ticketsViewModel = remember { TicketsViewModel(repository) }

                NavHost(
                    navController = navController,
                    startDestination = if (auth.currentUser == null) "auth" else "dashboard"
                ) {
                    composable("auth") {
                        val authViewModel = remember { AuthViewModel(repository) }
                        AuthScreen(authViewModel, onAuthSuccess = {
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        })
                    }

                    composable("dashboard/tickets") {
                        TicketListScreen(
                            ticketsViewModel,
                            onTicketClick = { ticketId ->
                                navController.navigate("quiz/$ticketId")
                            },
                            onNavigateBack = {
                                navController.navigate("dashboard") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = ticketsViewModel,
                            onAllTicketsClick = { navController.navigate("dashboard/tickets") },
                            onTicketSelected = { ticketId -> navController.navigate("quiz/$ticketId") },
                            onLogout = {
                                repository.signOut()
                                navController.navigate("auth") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
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
                        QuizScreen(ticketId = ticketId, quizViewModel, onNavigateBack = {
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