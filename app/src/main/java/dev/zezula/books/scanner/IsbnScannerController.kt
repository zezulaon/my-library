package dev.zezula.books.scanner

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

interface IsbnScannerController {
    suspend fun start(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onIsbnScanned: (String) -> Unit,
    )

    fun stop()
}
