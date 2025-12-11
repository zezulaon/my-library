package dev.zezula.books.testtag

class HomeTestTag {
    companion object {
        const val ROOT = "home/Root"

        const val CONTAINER_TOOLBAR = "home/container/Toolbar"
        const val CONTAINER_NAV_DRAWER = "home/container/NavDrawer"

        const val BTN_OPEN_NAV_DRAWER = "home/btn/OpenNavDrawer"
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

class ManageShelvesTestTag {
    companion object {
        const val ROOT = "manageShelves/Root"

        const val CONTAINER_SHELF_ITEM = "manageShelves/container/ShelfItem"

        const val BTN_EXPAND_SHELF = "manageShelves/btn/ExpandShelf"
        const val INPUT_SHELF_NAME = "manageShelves/input/ShelfName"
    }
}

class NotesTestTag {
    companion object {
        const val CONTAINER_NOTE_ITEM = "notes/container/NoteItem"
        const val BTN_EXPAND_NOTE = "notes/btn/ExpandShelf"
    }
}
