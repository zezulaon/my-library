package dev.zezula.books.util

import java.time.LocalDateTime

fun currentDateInIso(): String {
    return LocalDateTime.now().toString()
}