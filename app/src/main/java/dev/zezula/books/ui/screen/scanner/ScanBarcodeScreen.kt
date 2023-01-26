package dev.zezula.books.ui.screen.scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.zezula.books.R
import dev.zezula.books.ui.theme.MyLibraryTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanBarcodeRoute(
    onNavigateBack: () -> Unit,
    onBarcodeScanned: (String) -> Unit,
) {
    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA,
    )

    ScanBarcodeScreen(
        isCameraPermissionGranted = cameraPermissionState.status.isGranted,
        onRequestCameraPermissionClick = { cameraPermissionState.launchPermissionRequest() },
        onBarcodeScanned = onBarcodeScanned,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanBarcodeScreen(
    isCameraPermissionGranted: Boolean,
    onBarcodeScanned: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onRequestCameraPermissionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scanner_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_navigate_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            if (isCameraPermissionGranted) {
                BarcodeScannerComponent(onBarcodeScanned)
            } else {
                RequestCameraPermission(onRequestCameraPermissionClick)
            }
        }
    }
}

@Composable
private fun RequestCameraPermission(onRequestCameraPermissionClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.scanner_perm_required))
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(onClick = onRequestCameraPermissionClick) {
            Text(stringResource(R.string.scanner_btn_allow_camera))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyLibraryTheme {
        ScanBarcodeScreen(
            onBarcodeScanned = {},
            onNavigateBack = {},
            onRequestCameraPermissionClick = {},
            isCameraPermissionGranted = false,
        )
    }
}
