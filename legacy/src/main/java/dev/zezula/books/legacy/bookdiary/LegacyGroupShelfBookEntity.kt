package dev.zezula.books.legacy.bookdiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookGroup")
data class LegacyGroupShelfBookEntity(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val groupId: Int?,
    val bookId: Int?,
)
