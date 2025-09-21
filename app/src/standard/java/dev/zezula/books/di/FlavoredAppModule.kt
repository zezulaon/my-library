package dev.zezula.books.di

import dev.zezula.books.legacy.standard.di.flavoredStandardAppModule
import org.koin.dsl.module

val flavoredAppModule = module {
    includes(flavoredStandardAppModule)
}