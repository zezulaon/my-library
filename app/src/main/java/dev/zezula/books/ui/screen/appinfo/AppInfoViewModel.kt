package dev.zezula.books.ui.screen.appinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zezula.books.domain.export.ExportLibraryUseCase
import dev.zezula.books.domain.export.GetExportDirUseCase
import dev.zezula.books.domain.export.LastExportedFilesUseCase
import dev.zezula.books.ui.whileSubscribedInActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppInfoViewModel(
    private val exportLibraryUseCase: ExportLibraryUseCase,
    private val getExportDirUseCase: GetExportDirUseCase,
    private val lastExportedFilesUseCase: LastExportedFilesUseCase,
) : ViewModel() {

    private val exportInProgress = MutableStateFlow(false)
    private val exportDir = MutableStateFlow<String?>(null)
    private val lastExportedFileNames = MutableStateFlow<List<String>>(emptyList())

    val uiState = combine(
        exportInProgress,
        exportDir,
        lastExportedFileNames,
    ) { exportInProgress, exportDir, lastExportedFiles ->
        AppInfoUiState(
            exportInProgress = exportInProgress,
            exportDir = exportDir,
            lastExportedFiles = lastExportedFiles,
        )
    }
        .onStart {
            exportDir.value = getExportDirUseCase()?.absolutePath
            refreshLastExportedFiles()
        }
        .stateIn(
            scope = viewModelScope,
            started = whileSubscribedInActivity,
            initialValue = AppInfoUiState(),
        )

    fun onExportClicked() {
        viewModelScope.launch {
            exportInProgress.value = true
            exportLibraryUseCase()
            exportInProgress.value = false
            refreshLastExportedFiles()
        }
    }

    private fun refreshLastExportedFiles() {
        lastExportedFileNames.value = lastExportedFilesUseCase()
            .map { it.name }
    }
}
