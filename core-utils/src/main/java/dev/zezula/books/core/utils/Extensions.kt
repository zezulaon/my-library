package dev.zezula.books.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

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

fun String.toIsbnDigits(): String {
    return replace("-", "").replace(" ", "")
}

fun String.isIsbn(): Boolean {
    // TODO: Naive implementation, should be improved based on:
    //  https://en.wikipedia.org/wiki/International_Standard_Book_Number#ISBN-10_check_digit_calculation
    val isbn = this.toIsbnDigits()
    if (isbn.length != 10 && isbn.length != 13) return false
    val numberPart = isbn.substring(0, isbn.length - 1)

    // return true if all parts are digits
    return numberPart.all { it.isDigit() }
}
