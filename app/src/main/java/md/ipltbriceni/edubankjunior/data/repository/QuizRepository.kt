package md.ipltbriceni.edubankjunior.data.repository

import md.ipltbriceni.edubankjunior.data.model.Question
import md.ipltbriceni.edubankjunior.data.model.Ticket
import md.ipltbriceni.edubankjunior.data.model.TicketProgress

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

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
        awaitClose { subscription.remove() }
    }

    // --- Autenticare ---
    suspend fun signUp(email: String, pass: String) {
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        val uid = result.user?.uid ?: return
        val initialProfile = mapOf(
            "email" to email,
            "totalScore" to 0,
            "progress" to emptyMap<String, TicketProgress>(),
        )
        db.collection("users").document(uid).set(initialProfile).await()
    }

    suspend fun signIn(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
    }

    fun signOut() {
        auth.signOut()
    }

    // --- Scor ---
    fun getUserProgress(): Flow<Map<String, TicketProgress>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: run {
            trySend(emptyMap())
            close()
            return@callbackFlow
        }

        val subscription = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val progressMap = snapshot?.get("progress") as? Map<*, *>
                val result = progressMap?.entries?.mapNotNull { entry ->
                    val key = entry.key as? String
                    val value = entry.value as? Map<*, *>
                    if (key != null && value != null) {
                        key to TicketProgress(
                            correct = (value["correct"] as? Long)?.toInt() ?: 0,
                            total = (value["total"] as? Long)?.toInt() ?: 0
                        )
                    } else {
                        null
                    }
                }?.toMap() ?: emptyMap()
                trySend(result)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun saveTicketScore(ticketId: Int, correct: Int, total: Int) {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val updates = mapOf(
            "progress.$ticketId.correct" to correct,
            "progress.$ticketId.total" to total,
            "progress.$ticketId.timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("users").document(uid)
            .update(updates)
            .await()
    }
}