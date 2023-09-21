package dev.zezula.books.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatDate(date: String): String {
    return LocalDateTime.parse(date).format(DateTimeFormatter.ofPattern("d/MM/uuuu"))
}
