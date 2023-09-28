package dev.zezula.books.data.model.reference

data class Reference(
    val id: String,
    val bookId: String,
    val value: String? = null,
    val dateUpdated: String,
)
