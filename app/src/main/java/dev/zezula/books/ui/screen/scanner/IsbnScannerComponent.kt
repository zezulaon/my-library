package dev.zezula.books.ui.screen.scanner

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import dev.zezula.books.scanner.IsbnScannerController
import dev.zezula.books.testtag.ScanBarcodeTestTag
import org.koin.compose.koinInject

@Composable
fun IsbnScannerComponent(
    onIsbnScanned: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scannerController = koinInject<IsbnScannerController>()
    val currentOnIsbnScanned = rememberUpdatedState(onIsbnScanned)

    val previewView = remember(context) { PreviewView(context) }

    LaunchedEffect(scannerController, context, lifecycleOwner, previewView) {
        scannerController.start(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            onIsbnScanned = { isbn -> currentOnIsbnScanned.value(isbn) },
        )
    }

    DisposableEffect(scannerController) {
        onDispose {
            scannerController.stop()
        }
    }

    Box(
        modifier = modifier
            .testTag(ScanBarcodeTestTag.CONTAINER_SCANNER)
            .fillMaxSize(),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView },
        )
    }
}
