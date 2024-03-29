package dev.zezula.books.ui.screen.scanner

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
fun IsbnScannerComponent(
    onIsbnScanned: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val showScanner = remember { mutableStateOf(true) }

    if (showScanner.value) {
        LaunchedEffect(previewView) {
            context.startImageAnalysis(
                lifeCycleOwner = lifecycleOwner,
                previewView = previewView,
                onIsbnScanned = onIsbnScanned,
            )
        }
    }

    if (showScanner.value) {
        Box(
            modifier = modifier
                .fillMaxSize(),
        ) {
            AndroidView(modifier = modifier.fillMaxSize(), factory = { previewView })
        }
    }
}

suspend fun Context.startImageAnalysis(
    lifeCycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onIsbnScanned: (String) -> Unit,
) {
    val cameraProvider = this.getCameraProvider()

    val preview = Preview.Builder()
        .build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

    val imageAnalyzer = ImageAnalysis.Builder().build().also {
        it.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            IsbnAnalyzer(
                onIsbnScanned = { isbn ->
                    cameraProvider.unbindAll()
                    onIsbnScanned(isbn)
                },
            ),
        )
    }

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifeCycleOwner, cameraSelector, preview, imageAnalyzer)
    } catch (e: java.lang.Exception) {
        Timber.e(e, "Failed to bind camera provider.")
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider {
    return suspendCancellableCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            {
                try {
                    continuation.resume(future.get())
                } catch (e: ExecutionException) {
                    Timber.e(e, "Failed to init camera")
                    continuation.resumeWithException(e)
                } catch (e: InterruptedException) {
                    Timber.e(e, "Failed to init camera")
                    continuation.resumeWithException(e)
                }
            },
            ContextCompat.getMainExecutor(this),
        )
    }
}

private class IsbnAnalyzer(val onIsbnScanned: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val isbnFound = AtomicBoolean(false)

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_EAN_13,
        )
        .build()

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        Timber.d("analyze()")

        val image = imageProxy.image
        if (image != null) {
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(inputImage).addOnCompleteListener { task ->

                val isbnList: List<String> = task.result
                    .filterNotNull()
                    .filter { it.valueType == Barcode.TYPE_ISBN && it.format == Barcode.FORMAT_EAN_13 }
                    .mapNotNull { it.rawValue }

                imageProxy.close()

                if (isbnList.isNotEmpty() && isbnFound.get().not()) {
                    isbnFound.set(true)
                    val barcode = isbnList.first()
                    Timber.d("ISBN found: $barcode")
                    onIsbnScanned(barcode)
                }
            }
        }
    }
}
