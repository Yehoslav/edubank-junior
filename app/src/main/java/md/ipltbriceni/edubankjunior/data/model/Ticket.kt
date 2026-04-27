package md.ipltbriceni.edubankjunior.data.model

data class Ticket(
    val id: Int = 0,
    val title: String = ""
)

data class TicketProgress(
    val correct: Int = 0,
    val total: Int = 0
)
