package md.ipltbriceni.edubankjunior

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
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

        // Firebase se inițiază în fișierul ./EduBankApplication.kt
        val repository = QuizRepository(Firebase.auth, Firebase.firestore)

        enableEdgeToEdge()
        setContent {
            EduBankJuniorTheme {
                val navController = rememberNavController()
                val ticketsViewModel = remember { TicketsViewModel(repository) }
                val snackbarHostState = remember { SnackbarHostState() }
                val navigateToTicket = { ticketId: Int ->
                    navController.navigate("quiz/$ticketId")
                }

                NavHost(
                    navController = navController,
                    startDestination = if (repository.currentUser == null) "auth" else "dashboard"
                ) {
                    composable("auth") {
                        AuthScreen(
                            viewModel = remember { AuthViewModel(repository) },
                            snackbarHostState = snackbarHostState,
                            onAuthSuccess = {
                                navController.navigate("dashboard") {
                                    // Elimină din istoricul de navigare fereastra de autentificare
                                    // astfel când utilizatorul va naviga înapoi din `Dashboard` aplicația
                                    // nu se va muta pe fereastra de navigare, ci se va închide
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = ticketsViewModel,
                            snackbarHostState = snackbarHostState,
                            onTicketClick = navigateToTicket,
                            onAllTicketsClick = { navController.navigate("dashboard/tickets") },
                            onLogout = {
                                repository.signOut()
                                navController.navigate("auth") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("dashboard/tickets") {
                        TicketListScreen(
                            viewModel = ticketsViewModel,
                            snackbarHostState = snackbarHostState,
                            onTicketClick = navigateToTicket,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "quiz/{ticketId}",
                        arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val ticketId = backStackEntry.arguments?.getInt("ticketId") ?: 0
                        val quizViewModel = remember { QuizViewModel(repository) }
                        quizViewModel.loadQuestions(ticketId)
                        QuizScreen(ticketId, quizViewModel, onNavigateBack = {
                            navController.popBackStack()
                        })
                    }
                }
            }
        }
    }
}
