package dev.zezula.books.core.utils.test

import dev.zezula.books.core.utils.TestTree
import timber.log.Timber
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TimberLogRule() : TestWatcher() {

    private val testTree = TestTree()

    override fun starting(description: Description) {
        Timber.plant(testTree)
    }

    override fun finished(description: Description) {
        Timber.uproot(testTree)
    }
}