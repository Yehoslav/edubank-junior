package md.ipltbriceni.edubankjunior.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import md.ipltbriceni.edubankjunior.data.repository.QuizRepository

class TicketsViewModel(private val repository: QuizRepository) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    // This channel handles one-time events like showing a Snackbar
    private val _errorEvents = Channel<String>()
    val errorEvents = _errorEvents.receiveAsFlow()

    // We use a StateFlow to keep the UI in sync with the data
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    
    val tickets = combine(_tickets, repository.getUserProgress()) { tickets, progress ->
        tickets.map { it to progress[it.id.toString()] }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadTickets()
    }

    fun loadTickets() {
        isLoading = true
        repository.getTickets(
            onSuccess = {
                _tickets.value = it
                isLoading = false
            },
            onFailure = {
                isLoading = false
                viewModelScope.launch {
                    _errorEvents.send("Nu am putut încărca misiunile.")
                }
            }
        )
    }
}
