package dev.zezula.books

import android.content.Context
import android.content.ContextWrapper
import dev.zezula.books.ui.MyLibraryMainActivity

fun Context.findMyLibraryMainActivity(): MyLibraryMainActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is MyLibraryMainActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("MyLibraryMainActivity wasn't found - is this being called inside the correct activity context?")
}
