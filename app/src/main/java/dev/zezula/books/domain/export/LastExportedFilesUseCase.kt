package dev.zezula.books.domain.export

import java.io.File

class LastExportedFilesUseCase(
    private val getExportDirUseCase: GetExportDirUseCase,
) {

    operator fun invoke(): List<File> {
        val exportDir = getExportDirUseCase() ?: return emptyList()
        return exportDir.listFiles()
            ?.filter { it.isFile }
            ?.sortedByDescending { it.lastModified() }
            ?.take(3)
            ?: emptyList()
    }
}
