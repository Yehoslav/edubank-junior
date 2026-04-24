package md.ipltbriceni.edubankjunior.data.model

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
)