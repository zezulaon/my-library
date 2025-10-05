package dev.zezula.books.legacy.gb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_volume_shelf")
data class LegacyGroupShelfBookEntity(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val shelfId: Int?,
    val volumeId: Int?,
    val isDeleteAfterRefreshFlag: Int?,
)
