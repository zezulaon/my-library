package dev.zezula.books.util

import android.content.Context
import android.content.ContextWrapper
import dev.zezula.books.ui.MyLibraryMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

fun Context.findMyLibraryMainActivity(): MyLibraryMainActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is MyLibraryMainActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("MyLibraryMainActivity wasn't found - is this being called inside the correct activity context?")
}

/**
 * Returns true if [index] is the last index in the list.
 */
fun <T> List<T>.isLastIndex(index: Int) = index == this.lastIndex

/**
 * Removes HTML tags from the string.
 */
// TODO: Instead of removing the tags, the app should handle/display formatted HTML text
// https://stackoverflow.com/questions/63199823/avoid-malicious-code-to-be-sent-to-firebasedatabase-via-chat-on-android
suspend fun String.removeHtmlTags(): String {
    return coroutineScope {
        withContext(context = Dispatchers.IO) {
            var result = this@removeHtmlTags
            if (result.isNotEmpty()) {
                result = result.replace("<(.*?)>".toRegex(), " ") // Removes all items in brackets
                result = result.replace("<(.*?)\n".toRegex(), " ") // Must be underneath
                result = result.replaceFirst("(.*?)>".toRegex(), " ") // Removes any connected item to the last bracket
                result = result.replace("&nbsp;".toRegex(), " ")
                result = result.replace("&amp;".toRegex(), " ")
            }
            result
        }
    }
}
