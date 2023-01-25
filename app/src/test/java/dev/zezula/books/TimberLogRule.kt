package dev.zezula.books

import dev.zezula.books.util.TestTree
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import timber.log.Timber

class TimberLogRule() : TestWatcher() {

    private val testTree = TestTree()

    override fun starting(description: Description) {
        Timber.plant(testTree)
    }

    override fun finished(description: Description) {
        Timber.uproot(testTree)
    }
}
