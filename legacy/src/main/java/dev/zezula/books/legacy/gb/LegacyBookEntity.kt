package dev.zezula.books.legacy.gb

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.zezula.books.core.model.Book
import dev.zezula.books.data.database.entities.BookEntity
import kotlinx.datetime.Clock
import java.time.LocalDateTime
import java.util.Calendar

// Schema:
// TableInfo{name='volumes', columns={_id=Column{name='_id', type='INTEGER', affinity='3', notNull=false, primaryKeyPosition=1, defaultValue='undefined'}, gbId=Column{name='gbId', type='INTEGER', affinity='3', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, title=Column{name='title', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, subtitle=Column{name='subtitle', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, description=Column{name='description', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, categories=Column{name='categories', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, authors=Column{name='authors', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, isbn10=Column{name='isbn10', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, isbn13=Column{name='isbn13', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, pageCount=Column{name='pageCount', type='INTEGER', affinity='3', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, publishedDate=Column{name='publishedDate', type='INTEGER', affinity='3', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, publisher=Column{name='publisher', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, avarageRating=Column{name='avarageRating', type='REAL', affinity='4', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, ratingsCount=Column{name='ratingsCount', type='INTEGER', affinity='3', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, smallThumbnail=Column{name='smallThumbnail', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, thumbnail=Column{name='thumbnail', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, selfLink=Column{name='selfLink', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, infoLink=Column{name='infoLink', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, previewLink=Column{name='previewLink', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, webReaderLink=Column{name='webReaderLink', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, accessViewStatus=Column{name='accessViewStatus', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, linkDownloadEpub=Column{name='linkDownloadEpub', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, linkDownloadPdf=Column{name='linkDownloadPdf', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}, isDeleteAfterRefreshFlag=Column{name='isDeleteAfterRefreshFlag', type='INTEGER', affinity='3', notNull=false, primaryKeyPosition=0, defaultValue='undefined'}}, foreignKeys=[], indices=[]}

@Entity(tableName = "volumes")
data class LegacyBookEntity(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val gbId: Int?,
    val title: String?,
    val subtitle: String?,
    val description: String?,
    val categories: String?,
    val authors: String?,
    val isbn10: String?,
    val isbn13: String?,
    val pageCount: Int?,
    val publishedDate: Long?,
    val publisher: String?,
    val avarageRating: Double?,
    val ratingsCount: Int?,
    val smallThumbnail: String?,
    val thumbnail: String?,
    val selfLink: String?,
    val infoLink: String?,
    val previewLink: String?,
    val webReaderLink: String?,
    val accessViewStatus: String?,
    val linkDownloadEpub: String?,
    val linkDownloadPdf: String?,
    val isDeleteAfterRefreshFlag: Int?,
)

fun LegacyBookEntity.toBookEntity(bookId: String): BookEntity {
    val yearPublished = if (publishedDate != null) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = publishedDate
        cal.get(Calendar.YEAR)
    } else {
        null
    }

    return BookEntity(
        id = Book.Id(bookId),
        title = title,
        author = authors,
        description = description,
        isbn = isbn13 ?: isbn10,
        publisher = publisher,
        yearPublished = yearPublished,
        pageCount = null,
        thumbnailLink = null,
        dateAdded = LocalDateTime.now().toString(),
        userRating = null,
        subject = null,
        binding = null,
        isInLibrary = true,
        isPendingSync = true,
        lastModifiedTimestamp = Clock.System.now().toString(),
    )
}
