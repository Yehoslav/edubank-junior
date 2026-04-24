package md.ipltbriceni.edubankjunior.data.repository

import md.ipltbriceni.edubankjunior.data.model.Question
import md.ipltbriceni.edubankjunior.data.model.Ticket

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

import androidx.compose.runtime.staticCompositionLocalOf

class QuizRepository {
    private val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    fun getTickets(onSuccess: (List<Ticket>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tickets")
            .orderBy("id")
            .get()
            .addOnSuccessListener { result ->
                val tickets = result.toObjects(Ticket::class.java)
                onSuccess(tickets)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getQuestionsForTicket(ticketId: Int): Flow<Result<List<Question>>> = callbackFlow {
        val subscription = db.collection("tickets").document(ticketId.toString())
            .collection("questions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val questions = snapshot?.mapNotNull { doc ->
                    doc.toObject(Question::class.java)
                } ?: emptyList()

                trySend(Result.success(questions))
            }
        
        // Clean up the listener when the flow is closed
        awaitClose { subscription.remove() }
    }
}


// We use staticCompositionLocalOf because the Repository 
// instance won't change during the app's lifetime.
val LocalQuizRepository = staticCompositionLocalOf<QuizRepository> {
    error("No QuizRepository provided! Check your MainActivity setContent.")
}