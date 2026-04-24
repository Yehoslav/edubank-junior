package md.ipltbriceni.edubankjunior.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import md.ipltbriceni.edubankjunior.R
import md.ipltbriceni.edubankjunior.data.model.Question
import md.ipltbriceni.edubankjunior.data.model.QuizUiState
import md.ipltbriceni.edubankjunior.data.model.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(ticketId: Int, viewModel: QuizViewModel, onNavigateBack: () -> Unit = {}) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Misiunea $ticketId", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Înapoi",
                            tint = MaterialTheme.colorScheme.primary // Your EduOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Întrebarea Misiunii $ticketId",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val uiState by viewModel.uiState.collectAsState()

                when (val state = uiState) {
                    is QuizUiState.Loading -> CircularProgressIndicator()
                    is QuizUiState.Success -> {
                        if (viewModel.isMissionComplete) {
                            MissionSummary(
                                score = viewModel.score,
                                total = state.questions.size,
                                onExit = onNavigateBack
                            )
                        } else {
                            QuizContent(state.questions, viewModel)
                        }
                    }
                    is QuizUiState.Error -> Text(state.message)
                }
            }
        }
    }
}

@Composable
fun MissionSummary(score: Int, total: Int, onExit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Misiune Completă!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Scorul tău: $score / $total",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onExit) {
            Text("Înapoi la Dashboard")
        }
    }
}

@Composable
fun QuizContent(questions: List<Question>, viewModel: QuizViewModel) {

    val currentQuestion: Question = questions[viewModel.currentQuestionIndex]

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface, // White
        tonalElevation = 2.dp
    ) {
        Text(
            text = currentQuestion.text,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }

    Spacer(modifier = Modifier.height(48.dp))

    currentQuestion.options.forEachIndexed { index, answer ->

        val buttonColor = when {
            !viewModel.showFeedback -> MaterialTheme.colorScheme.onSurface
            index == currentQuestion.correctIndex -> Color.Green
            index == viewModel.selectedOptionIndex -> Color.Red
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        }

        AnswerButton(
            text = answer,
            color = buttonColor,
            onClick = {
                viewModel.checkAnswer(index, questions)
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AnswerButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = color
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}