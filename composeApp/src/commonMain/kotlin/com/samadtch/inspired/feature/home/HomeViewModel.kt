package com.samadtch.inspired.feature.home

import com.samadtch.inspired.common.LOADING_STATE
import com.samadtch.inspired.common.Result
import com.samadtch.inspired.common.SUCCESS_STATE
import com.samadtch.inspired.common.exceptions.AuthException
import com.samadtch.inspired.common.exceptions.DataException
import com.samadtch.inspired.data.repositories.FoldersRepository
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.Folder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class HomeViewModel(
    private val foldersRepository: FoldersRepository
) : ViewModel() {

    /* **************************************************************************
     * ************************************* Declarations
     */
    val homeUiState: StateFlow<HomeUiState> = foldersRepository.getAllItems().map {
        when (it) {
            Result.Loading -> HomeUiState.Loading
            is Result.Error -> {
                //TODO: Handle Re-Authentication
                HomeUiState.Error(
                    type = if (it.exception is DataException) "DATA" else "AUTH",
                    code = if (it.exception is DataException) it.exception.code
                    else (it.exception as AuthException).code
                )
            }

            is Result.Success -> HomeUiState.Success(it.data.first, it.data.second)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading,
    )

    //Action States
    private val _deleteFolderState = MutableStateFlow<Int?>(null)
    val deleteFolderState: StateFlow<Int?> = _deleteFolderState.asStateFlow()
    private val _saveFolderState = MutableStateFlow<Int?>(null)
    val saveFolderState: StateFlow<Int?> = _saveFolderState.asStateFlow()
    private val _saveAssetState = MutableStateFlow<Int?>(null)
    val saveAssetState: StateFlow<Int?> = _saveAssetState.asStateFlow()

    /* **************************************************************************
     * ************************************* Functions
     */
    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            _deleteFolderState.emit(LOADING_STATE)//Loading State
            try {
                foldersRepository.deleteFolder(folderId)
                _deleteFolderState.emit(SUCCESS_STATE)
            } //TODO: Properly handle returned errors
            catch (e: AuthException) {
                println("CREATE FOLDER - AUTH ERROR: " + e.message)
                _deleteFolderState.emit(e.code)
            } catch (e: DataException) {
                println("CREATE FOLDER - DATA ERROR: " + e.message)
                _deleteFolderState.emit(e.code)
            }
        }
    }

    fun saveFolder(folder: Folder, parentId: String?) {
        viewModelScope.launch {
            _saveFolderState.emit(LOADING_STATE)//Loading State
            try {
                foldersRepository.saveFolder(folder, parentId)
                _saveFolderState.emit(SUCCESS_STATE)
            } //TODO: Properly handle returned errors
            catch (e: AuthException) {
                println("SAVE FOLDER - AUTH ERROR: " + e.message)
                _saveFolderState.emit(e.code)
            } catch (e: DataException) {
                println("SAVE FOLDER - DATA ERROR: " + e.message)
                _saveFolderState.emit(e.code)
            }
        }
    }

    /* **************************************************************************
     * ************************************* UI States
     */
    sealed interface HomeUiState {
        data object Loading : HomeUiState
        data class Success(
            val folders: List<Folder>,
            val assets: List<Asset>
        ) : HomeUiState

        data class Error(val type: String, val code: Int) : HomeUiState
    }
}