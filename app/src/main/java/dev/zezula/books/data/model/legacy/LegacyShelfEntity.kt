package dev.zezula.books.data.model.legacy

import androidx.room.Entity
import androidx.room.PrimaryKey

// Schema:
//SqliteColumn(name=_id, affinity=INTEGER, isNullable=true, inPrimaryKey=true)
//SqliteColumn(name=gbId, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=title, affinity=TEXT, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=description, affinity=TEXT, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=access, affinity=TEXT, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=volumeCount, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=volumesLastUpdated, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=created, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=updated, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=selfLink, affinity=TEXT, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=lastBooksRefetched, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
//SqliteColumn(name=isDeleteAfterRefreshFlag, affinity=INTEGER, isNullable=true, inPrimaryKey=false)

@Entity(tableName = "bookshelves")
data class LegacyShelfEntity(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val gbId: Int?,
    val title: String?,
    val description: String?,
    val access: String?,
    val volumeCount: Int?,
    val volumesLastUpdated: Int?,
    val created: Int?,
    val updated: Int?,
    val selfLink: String?,
    val lastBooksRefetched: Int?,
    val isDeleteAfterRefreshFlag: Int?,
)
