package dev.zezula.books.data.model.legacy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookGroup")
data class LegacyGroupShelfBookEntity(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val groupId: Int?,
    val bookId: Int?,
)
