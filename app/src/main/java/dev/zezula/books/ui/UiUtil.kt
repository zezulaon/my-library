package dev.zezula.books.ui

import kotlinx.coroutines.flow.SharingStarted

val whileSubscribedInActivity = SharingStarted.WhileSubscribed(5000)
