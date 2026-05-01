package md.ipltbriceni.edubankjunior.data.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import md.ipltbriceni.edubankjunior.data.repository.QuizRepository

import com.google.firebase.firestore.PropertyName

data class Question(
    @get:PropertyName("q")
    @set:PropertyName("q")
    var text: String = "",

    @get:PropertyName("a")
    @set:PropertyName("a")
    var options: List<String> = emptyList(),

    @get:PropertyName("correct")
    @set:PropertyName("correct")
    var correctIndex: Int = 0
) {
    // Computed property to find the correct answer text before shuffling
    val correctAnswerText: String
        get() = options.getOrElse(correctIndex) { "" }

    // Shuffled version of options for UI display
    val shuffledOptions: List<String> by lazy { options.shuffled() }
}

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Simple variables for tracking the quiz
    var currentQuestionIndex by mutableIntStateOf(0)
        private set
    
    var score by mutableIntStateOf(0)
        private set

    var isMissionComplete by mutableStateOf(false)
        private set

    var selectedOptionIndex by mutableStateOf<Int?>(null)
        private set

    var showFeedback by mutableStateOf(false)
        private set

    fun checkAnswer(selectedIndex: Int, questions: List<Question>) {
        if (showFeedback) return // Prevent double-clicks during the flash

        val currentQuestion = questions[currentQuestionIndex]
        val selectedAnswer = currentQuestion.shuffledOptions[selectedIndex]
        selectedOptionIndex = selectedIndex
        showFeedback = true

        if (selectedAnswer == currentQuestion.correctAnswerText) {
            score++
        }

        // 2. Wait 1 second, then move to the next question
        viewModelScope.launch {
            delay(1000) // The "flash" duration
            
            showFeedback = false
            selectedOptionIndex = null

            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
            } else {
                isMissionComplete = true
            }
        }
    }
    
    fun loadQuestions(ticketId: Int) {
        viewModelScope.launch {
            repository.getQuestionsForTicket(ticketId).collect { result ->
                result.fold(
                    onSuccess = { _uiState.value = QuizUiState.Success(it) },
                    onFailure = { _uiState.value = QuizUiState.Error(it.message ?: "Unknown Error") }
                )
            }
        }
    }

    var isSaving by mutableStateOf(false)
        private set

    /**
     * Called when the student finishes the last question.
     */
    fun finishQuiz(ticketId: Int, correct: Int, total: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            try {
                repository.saveTicketScore(ticketId, correct, total)
                onComplete() // Navigate back to Dashboard
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Failed to save score", e)
                // Optionally show a Toast error here
            } finally {
                isSaving = false
            }
        }
    }
}

sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Success(val questions: List<Question>) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}