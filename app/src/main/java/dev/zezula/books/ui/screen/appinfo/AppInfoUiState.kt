package dev.zezula.books.ui.screen.appinfo

data class AppInfoUiState(
    val exportInProgress: Boolean = false,
    val exportDir: String? = null,
    val lastExportedFiles: List<String> = emptyList(),
)
