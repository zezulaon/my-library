package dev.zezula.books.scanner

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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

class CameraXIsbnScannerController : IsbnScannerController {

    private var cameraProvider: ProcessCameraProvider? = null

    override suspend fun start(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onIsbnScanned: (String) -> Unit,
    ) {
        val provider = context.getCameraProvider()
        cameraProvider = provider

        val preview = Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val imageAnalyzer = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    MlKitIsbnAnalyzer(
                        onIsbnScanned = { isbn ->
                            provider.unbindAll()
                            onIsbnScanned(isbn)
                        },
                    ),
                )
            }

        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer,
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to bind camera provider.")
        }
    }

    override fun stop() {
        cameraProvider?.unbindAll()
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider {
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

private class MlKitIsbnAnalyzer(
    private val onIsbnScanned: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val isbnFound = AtomicBoolean(false)

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13)
        .build()

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees,
        )

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val isbn = barcodes
                    .firstOrNull {
                        it.valueType == Barcode.TYPE_ISBN &&
                            it.format == Barcode.FORMAT_EAN_13
                    }
                    ?.rawValue

                if (isbn != null && isbnFound.compareAndSet(false, true)) {
                    Timber.d("ISBN found: $isbn")
                    onIsbnScanned(isbn)
                }
            }
            .addOnFailureListener { error ->
                Timber.e(error, "Failed to process barcode frame.")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
