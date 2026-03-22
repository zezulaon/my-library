package dev.zezula.books.tests.fake

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import dev.zezula.books.scanner.IsbnScannerController
import kotlinx.coroutines.channels.Channel

class FakeIsbnScannerController : IsbnScannerController {

    private val scanEvents = Channel<String>(capacity = Channel.UNLIMITED)

    override suspend fun start(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onIsbnScanned: (String) -> Unit,
    ) {
        onIsbnScanned(scanEvents.receive())
    }

    override fun stop() = Unit

    fun emitScan(isbn: String) {
        check(scanEvents.trySend(isbn).isSuccess) {
            "Failed to enqueue fake scan for ISBN: $isbn"
        }
    }

    fun reset() {
        while (scanEvents.tryReceive().isSuccess) {
            // drain pending events between tests
        }
    }
}
