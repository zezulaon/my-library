package dev.zezula.books.tests

import android.Manifest
import androidx.test.platform.app.InstrumentationRegistry

fun grantCameraPermission() {
    InstrumentationRegistry.getInstrumentation().uiAutomation.grantRuntimePermission(
        InstrumentationRegistry.getInstrumentation().targetContext.packageName,
        Manifest.permission.CAMERA,
    )
}
