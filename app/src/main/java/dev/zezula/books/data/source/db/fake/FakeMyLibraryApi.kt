package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.myLibrary.MyLibraryBook
import dev.zezula.books.data.source.network.MyLibraryApi

class FakeMyLibraryApi : MyLibraryApi {
    override suspend fun suggestions(title: String, author: String, isbn: String?): List<MyLibraryBook>? {
        TODO("Not yet implemented")
    }
}
