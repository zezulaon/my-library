package dev.zezula.books.util

/**
 * List of articles in different languages. Used for sorting - if a book title contains an article, it should be
 * ignored (removed) when sorting.
 */
private val articles = listOf(
    "The", "A", "An", // English
    "Le", "La", "Les", "Un", "Une", // French
    "Der", "Die", "Das", "Ein", "Eine", // German
    "De", "Het", "Een", // Dutch
    "Il", "Lo", "La", "I", "Gli", "Le", "Un", "Una", // Italian
    "El", "La", "Los", "Las", "Un", "Una", // Spanish
    "O", "A", "Os", "As", "Um", "Uma", // Portuguese
    "Den", "Det", "Ett", "En", // Swedish
    "Den", "Det", "Ei", "Et", // Norwegian
)

/**
 * Returns title without article. (If there are more articles, removes the first one).
 * Example: "The Lord of the Rings" -> "Lord of the Rings".
 *
 * @return a book title without article.
 */
fun String.toSortingTitle(): String {
    val title = this.trim()
    articles.forEach { article ->
        val prefix = "$article "
        if (title.startsWith(prefix, ignoreCase = true)) {
            return title.removePrefix(prefix).removePrefix(prefix.lowercase())
        }
    }
    return title
}

/**
 * Returns author's last name for sorting purposes. (If there are more authors, returns the last name of the first
 * author)
 *
 * @return book's author's last name.
 */
fun String.toSortingAuthor(): String {
    var author = this.trim()
    val names = author.split(", ", ",")
    if (names.size > 1) {
        author = names.first()
    }
    return author.trim().split(" ").last()
}