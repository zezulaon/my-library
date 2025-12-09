package dev.zezula.books.testtag

class HomeTestTag {
    companion object {
        const val ROOT = "home/Root"

        const val CONTAINER_TOOLBAR = "home/container/Toolbar"

        const val BTN_ADD_BOOK = "home/btn/AddBook"
        const val BTN_ADD_BOOK_MANUALLY = "home/btn/AddBookManually"
    }
}

class BookEditorTestTag {
    companion object {
        const val ROOT = "bookEditor/Root"

        const val INPUT_TITLE = "bookEditor/input/Title"
        const val INPUT_AUTHOR = "bookEditor/input/Author"
        const val INPUT_PUBLISHER = "bookEditor/input/Publisher"
        const val INPUT_YEAR = "bookEditor/input/Year"
        const val INPUT_PAGES = "bookEditor/input/Pages"
        const val INPUT_ISBN = "bookEditor/input/Isbn"
        const val INPUT_DESC = "bookEditor/input/Desc"

        const val BTN_SAVE = "bookEditor/btn/Save"
    }
}

class BookDetailTestTag {
    companion object {
        const val ROOT = "bookDetail/Root"

        const val BTN_DELETE_BOOK = "bookDetail/btn/DeleteBook"
        const val BTN_EDIT_BOOK = "bookDetail/btn/EditBook"
    }
}
