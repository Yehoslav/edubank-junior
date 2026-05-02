package md.ipltbriceni.edubankjunior.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import md.ipltbriceni.edubankjunior.data.repository.QuizRepository

data class Ticket(
    val id: Int = 0,
    val title: String = ""
)

data class TicketProgress(
    val correct: Int = 0,
    val total: Int = 0
)

class TicketsViewModel(private val repository: QuizRepository) : ViewModel() {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    var isLoading by mutableStateOf(false)
        private set

    // This channel handles one-time events like showing a Snackbar
    private val _errorEvents = Channel<String>()
    val errorEvents = _errorEvents.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tickets = refreshTrigger.flatMapLatest {
        combine(
            repository.getTickets(), // Aici sunt colectate rezultatele trimise de către trySend, vezi comentariul din getTicket
            repository.getUserProgress()
        ) { ticketsResult, progress ->
            isLoading = false
            ticketsResult.fold(
                onSuccess = { ticketList ->
                    ticketList.map { it to progress[it.id.toString()] }
                },
                onFailure = {
                    _errorEvents.trySend("Nu am putut încărca misiunile.")
                    emptyList()
                }
            )
        }.onStart { isLoading = true }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Triggers a manual refresh of the tickets and progress data.
     */
    fun loadTickets() {
        refreshTrigger.tryEmit(Unit)
    }

    val currentUserEmail: String?
        get() = repository.currentUserEmail
}
