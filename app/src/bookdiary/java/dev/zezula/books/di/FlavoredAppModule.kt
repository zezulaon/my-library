package dev.zezula.books.di

import dev.zezula.books.legacy.bookdiary.di.flavoredBookdiaryAppModule
import org.koin.dsl.module

val flavoredAppModule = module {
    includes(flavoredBookdiaryAppModule)
}
