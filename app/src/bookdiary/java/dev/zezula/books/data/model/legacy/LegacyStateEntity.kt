package dev.zezula.books.data.model.legacy

import androidx.room.Entity
import androidx.room.PrimaryKey

// Schema:
// SqliteColumn(name=_id, affinity=INTEGER, isNullable=true, inPrimaryKey=true)
// SqliteColumn(name=bookId, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=favorite, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=haveRead, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=readingNow, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=iOwn, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=toBuy, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=toRead, affinity=INTEGER, isNullable=true, inPrimaryKey=false)

@Entity(tableName = "states")
data class LegacyStateEntity(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val bookId: Int?,
    val favorite: Int?,
    val haveRead: Int?,
    val readingNow: Int?,
    val iOwn: Int?,
    val toBuy: Int?,
    val toRead: Int?,
)
