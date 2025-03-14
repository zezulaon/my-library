package dev.zezula.books.data.model.book
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.ForeignKey.Companion.CASCADE
//import androidx.room.PrimaryKey
//
///**
// * Represents a record of a book that has been added to the user's library collection in the database.
// *
// * Each instance of this class signifies that a particular book, identified by [bookId], has been added to the user's
// * library collection. The [bookId] is a foreign key that references the primary key in the [BookEntity] table.
// *
// * The [LibraryBookEntity] serves as a way to track which books from the broader [BookEntity] table have been
// * selected by the user. (It is essential for functionalities like displaying the user's personal book collection).
// *
// */
//@Entity(
//    tableName = "library_books",
//    foreignKeys = [
//        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = CASCADE),
//    ],
//)
//data class LibraryBookEntity(
//    @PrimaryKey
//    val bookId: String,
//)
