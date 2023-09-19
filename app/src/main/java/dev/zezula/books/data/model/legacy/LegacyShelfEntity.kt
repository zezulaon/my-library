package dev.zezula.books.data.model.legacy

import androidx.room.Entity
import androidx.room.PrimaryKey

// Schema:
//SqliteColumn(name=_id, affinity=INTEGER, isNullable=true, inPrimaryKey=true)
//SqliteColumn(name=name, affinity=TEXT, isNullable=true, inPrimaryKey=false)

@Entity(tableName = "groups")
data class LegacyShelfEntity(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val name: String?,
)
