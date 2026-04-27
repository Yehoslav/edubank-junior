package md.ipltbriceni.edubankjunior.data.repository

import md.ipltbriceni.edubankjunior.data.model.Question
import md.ipltbriceni.edubankjunior.data.model.Ticket

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

import androidx.compose.runtime.staticCompositionLocalOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuizRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
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

    // --- Authentication Logic ---

    suspend fun signUp(email: String, pass: String) {
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        val uid = result.user?.uid ?: return
        val initialProfile = mapOf(
            "email" to email,
            "totalScore" to 0,
            "progress" to emptyMap<String, Int>(),
            "role" to "student"
        )
        db.collection("users").document(uid).set(initialProfile).await()
    }

    suspend fun signIn(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
    }

    fun signOut() {
        auth.signOut()
    }

}