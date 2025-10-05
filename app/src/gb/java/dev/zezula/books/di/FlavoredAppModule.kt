package dev.zezula.books.di

import dev.zezula.books.legacy.gb.di.flavoredGbAppModule
import org.koin.dsl.module

val flavoredAppModule = module {
    includes(flavoredGbAppModule)
}
