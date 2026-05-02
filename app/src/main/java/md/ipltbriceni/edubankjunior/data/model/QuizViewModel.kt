package md.ipltbriceni.edubankjunior.data.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
    val correctAnswerText: String
        get() = options.getOrElse(correctIndex) { "" }

    // shuffled va crea o nouă listă, așa că `correctAnswerText` nu va fi afectat
    val shuffledOptions: List<String> by lazy { options.shuffled() }
}

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _errorEvents = Channel<String>()
    val errorEvents = _errorEvents.receiveAsFlow()

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
        if (showFeedback) return // Ignoră apăsările multiple în timp ce se afișează răspunsurile

        val currentQuestion = questions[currentQuestionIndex]
        val selectedAnswer = currentQuestion.shuffledOptions[selectedIndex]
        selectedOptionIndex = selectedIndex
        showFeedback = true

        if (selectedAnswer == currentQuestion.correctAnswerText) {
            score++
        }

        // Așteaptă pentru a afișa răspunsul ne/corect și treci la următoarea întrebare
        viewModelScope.launch {
            delay(1000) // Timpul de fișare a răspunsurilor marcate ne/corect
            
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

    /** Se execută după ce utilizatorul a răspuns la ultima întrebare de pe bilet */
    fun finishQuiz(ticketId: Int, correct: Int, total: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            try {
                repository.saveTicketScore(ticketId, correct, total)
                onComplete()
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Failed to save score", e)
                _errorEvents.trySend("Nu s-a putut salva scorul. Verifică conexiunea.")
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