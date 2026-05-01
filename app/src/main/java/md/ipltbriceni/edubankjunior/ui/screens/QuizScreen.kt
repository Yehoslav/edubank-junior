package md.ipltbriceni.edubankjunior.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import md.ipltbriceni.edubankjunior.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import md.ipltbriceni.edubankjunior.ui.theme.EduBankJuniorTheme
import md.ipltbriceni.edubankjunior.data.model.Question
import md.ipltbriceni.edubankjunior.data.model.QuizUiState
import md.ipltbriceni.edubankjunior.data.model.QuizViewModel

@Composable
fun QuizScreen(
    ticketId: Int,
    viewModel: QuizViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    QuizScreenContent(
        ticketId = ticketId,
        uiState = uiState,
        isMissionComplete = viewModel.isMissionComplete,
        score = viewModel.score,
        currentQuestionIndex = viewModel.currentQuestionIndex,
        selectedOptionIndex = viewModel.selectedOptionIndex,
        showFeedback = viewModel.showFeedback,
        snackbarHostState = snackbarHostState,
        onAnswerClick = { index, questions -> viewModel.checkAnswer(index, questions) },
        onFinishQuiz = { tId, s, total -> viewModel.finishQuiz(tId, s, total, onComplete = {}) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizScreenContent(
    ticketId: Int,
    uiState: QuizUiState,
    isMissionComplete: Boolean,
    score: Int,
    currentQuestionIndex: Int,
    selectedOptionIndex: Int?,
    showFeedback: Boolean,
    snackbarHostState: SnackbarHostState,
    onAnswerClick: (Int, List<Question>) -> Unit,
    onFinishQuiz: (Int, Int, Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            QuizTopAppBar(ticketId = ticketId, onNavigateBack = onNavigateBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                is QuizUiState.Error -> Text(uiState.message, color = MaterialTheme.colorScheme.error)
                is QuizUiState.Loading -> CircularProgressIndicator()
                is QuizUiState.Success -> {
                    if (isMissionComplete) {
                        LaunchedEffect(Unit) {
                            onFinishQuiz(ticketId, score, uiState.questions.size)
                        }
                        MissionSummary(
                            score = score,
                            total = uiState.questions.size,
                            onExit = onNavigateBack
                        )
                    } else {
                        QuizContent(
                            questions = uiState.questions,
                            currentQuestionIndex = currentQuestionIndex,
                            selectedOptionIndex = selectedOptionIndex,
                            showFeedback = showFeedback,
                            onAnswerClick = { index -> onAnswerClick(index, uiState.questions) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizTopAppBar(ticketId: Int, onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text("Biletul $ticketId", style = MaterialTheme.typography.titleLarge)
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Înapoi",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    )
}

@Composable
private fun MissionSummary(score: Int, total: Int, onExit: () -> Unit) {
    Column(
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
        
        Button(
            onClick = onExit,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Înapoi la Dashboard")
        }
    }
}

@Composable
private fun QuizContent(
    questions: List<Question>,
    currentQuestionIndex: Int,
    selectedOptionIndex: Int?,
    showFeedback: Boolean,
    onAnswerClick: (Int) -> Unit
) {
    val currentQuestion = questions[currentQuestionIndex]

    QuestionSurface(text = currentQuestion.text)

    Spacer(modifier = Modifier.height(48.dp))

    currentQuestion.options.forEachIndexed { index, answer ->
        val isCorrect = index == currentQuestion.correctIndex
        val isSelected = index == selectedOptionIndex
        
        val (containerColor, contentColor) = getAnswerButtonColors(
            isSelected = isSelected,
            isCorrect = isCorrect,
            showFeedback = showFeedback
        )

        AnswerButton(
            text = answer,
            containerColor = containerColor,
            contentColor = contentColor,
            onClick = { onAnswerClick(index) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QuestionSurface(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.background,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun getAnswerButtonColors(
    isSelected: Boolean,
    isCorrect: Boolean,
    showFeedback: Boolean
): Pair<Color, Color> {
    val colors = MaterialTheme.colorScheme
    return when {
        !showFeedback -> colors.surface to colors.onSurface
        isCorrect -> colors.tertiaryContainer to colors.onTertiaryContainer
        isSelected -> colors.errorContainer to colors.onErrorContainer
        else -> colors.surfaceVariant to colors.onSurfaceVariant
    }
}

@Composable
private fun AnswerButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = null,
                    indication = ripple(color = MaterialTheme.colorScheme.primaryContainer)
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// --- PREVIEWS ---

data class QuizPreviewState(
    val name: String,
    val uiState: QuizUiState,
    val isMissionComplete: Boolean = false,
    val score: Int = 0,
    val selectedOptionIndex: Int? = null,
    val showFeedback: Boolean = false,
    val darkTheme: Boolean = false
)

private val sampleQuestions = listOf(
    Question(
        text = "Ce este un buget?",
        options = listOf("Un plan de cheltuieli", "O listă de dorințe", "O bancă", "Un card"),
        correctIndex = 0
    )
)

class LightQuizPreviewParameterProvider : PreviewParameterProvider<QuizPreviewState> {
    override val values = sequenceOf(
        QuizPreviewState(
            name = "Light Mode",
            uiState = QuizUiState.Success(sampleQuestions),
            darkTheme = false
        ),
        QuizPreviewState(
            name = "Correct Answer",
            uiState = QuizUiState.Success(sampleQuestions),
            selectedOptionIndex = 0,
            showFeedback = true
        ),
        QuizPreviewState(
            name = "Wrong Answer",
            uiState = QuizUiState.Success(sampleQuestions),
            selectedOptionIndex = 1,
            showFeedback = true
        ),
    )
}

class DarkQuizPreviewParameterProvider : PreviewParameterProvider<QuizPreviewState> {
    override val values = sequenceOf(
        QuizPreviewState(
            name = "Light Mode",
            uiState = QuizUiState.Success(sampleQuestions),
            darkTheme = true
        ),
        QuizPreviewState(
            name = "Correct Answer",
            uiState = QuizUiState.Success(sampleQuestions),
            selectedOptionIndex = 0,
            darkTheme = true,
            showFeedback = true
        ),
        QuizPreviewState(
            name = "Wrong Answer",
            uiState = QuizUiState.Success(sampleQuestions),
            selectedOptionIndex = 1,
            darkTheme = true,
            showFeedback = true
        ),
    )
}

class MissionCompletePreviewParameterProvider : PreviewParameterProvider<QuizPreviewState> {
    override val values = sequenceOf(
        QuizPreviewState(
            name = "Mission Complete Light",
            uiState = QuizUiState.Success(sampleQuestions),
            isMissionComplete = true,
            score = 1
        ),
        QuizPreviewState(
            name = "Mission Complete Dark",
            uiState = QuizUiState.Success(sampleQuestions),
            isMissionComplete = true,
            darkTheme = true,
            score = 1
        )
    )
}

@Composable
fun QuizScreenPreviewFactory(
    previewState: QuizPreviewState
) {
    EduBankJuniorTheme(darkTheme = previewState.darkTheme) {
        QuizScreenContent(
            ticketId = 1,
            uiState = previewState.uiState,
            isMissionComplete = previewState.isMissionComplete,
            score = previewState.score,
            currentQuestionIndex = 0,
            selectedOptionIndex = previewState.selectedOptionIndex,
            showFeedback = previewState.showFeedback,
            snackbarHostState = remember { SnackbarHostState() },
            onAnswerClick = { _, _ -> },
            onFinishQuiz = { _, _, _ -> },
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name="Light Quiz Interactions")
@Composable
fun LightQuizScreenPreview(
    @PreviewParameter(LightQuizPreviewParameterProvider::class) previewState: QuizPreviewState
) {
    QuizScreenPreviewFactory(previewState)
}

@Preview(showBackground = true, name="Dark Quiz Interactions")
@Composable
fun DarkQuizScreenPreview(
    @PreviewParameter(DarkQuizPreviewParameterProvider::class) previewState: QuizPreviewState
) {
    QuizScreenPreviewFactory(previewState)
}

@Preview(showBackground = true, name = "Mission Complete")
@Composable
fun QuizScreenPreview(
    @PreviewParameter(MissionCompletePreviewParameterProvider::class) previewState: QuizPreviewState
) {
    QuizScreenPreviewFactory(previewState)
}
