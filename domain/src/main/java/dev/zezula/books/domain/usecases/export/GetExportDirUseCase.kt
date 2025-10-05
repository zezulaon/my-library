package dev.zezula.books.domain.usecases.export

import android.content.Context
import java.io.File

class GetExportDirUseCase(
    private val applicationContext: Context,
) {

    operator fun invoke(): File? {
        val exportDir = applicationContext.getExternalFilesDir("exports")
        if (exportDir != null && !exportDir.exists()) {
            exportDir.mkdirs()
        }
        return exportDir
    }
}
