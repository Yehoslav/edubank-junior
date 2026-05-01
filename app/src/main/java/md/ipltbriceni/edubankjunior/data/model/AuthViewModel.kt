package md.ipltbriceni.edubankjunior.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import md.ipltbriceni.edubankjunior.data.repository.QuizRepository

class AuthViewModel(private val repository: QuizRepository) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun performAuth(isLogin: Boolean, email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isEmpty() || pass.isEmpty()) {
            error = "Te rugăm să completezi toate câmpurile."
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                if (isLogin) {
                    repository.signIn(email, pass)
                } else {
                    repository.signUp(email, pass)
                }
                onSuccess()
            } catch (e: Exception) {
                // Firebase provides friendly error messages we can pass to the UI
                error = e.localizedMessage ?: "A apărut o eroare la conectare."
            } finally {
                isLoading = false
            }
        }
    }
}
