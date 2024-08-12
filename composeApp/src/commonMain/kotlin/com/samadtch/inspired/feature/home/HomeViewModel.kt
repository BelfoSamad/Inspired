package com.samadtch.inspired.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samadtch.inspired.common.LOADING_STATE
import com.samadtch.inspired.common.Result
import com.samadtch.inspired.common.SUCCESS_STATE
import com.samadtch.inspired.common.exceptions.AuthException
import com.samadtch.inspired.common.exceptions.DataException
import com.samadtch.inspired.data.repositories.AssetsRepository
import com.samadtch.inspired.data.repositories.FoldersRepository
import com.samadtch.inspired.data.repositories.UserRepository
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val foldersRepository: FoldersRepository,
    private val assetsRepository: AssetsRepository
) : ViewModel() {

    /* **************************************************************************
     * ************************************* Declarations
     */
    val homeUiState: StateFlow<HomeUiState> = getAllItems().map {
        when (it) {
            Result.Loading -> HomeUiState.Loading
            is Result.Error -> {
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
    private val _createAssetState = MutableStateFlow<Int?>(null)
    val createAssetState: StateFlow<Int?> = _createAssetState.asStateFlow()
    private val _deleteAssetState = MutableStateFlow<Int?>(null)
    val deleteAssetState: StateFlow<Int?> = _deleteAssetState.asStateFlow()

    /* **************************************************************************
     * ************************************* Functions
     */
    private fun getAllItems(): Flow<Result<Pair<List<Folder>, List<Asset>>>> = flow {
        userRepository.performActionWithFreshToken { token ->
            foldersRepository.getAllItems(token).collect { emit(it) }
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            _deleteFolderState.emit(LOADING_STATE)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    foldersRepository.deleteFolder(it, folderId)
                }
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
                userRepository.performActionWithFreshToken {
                    foldersRepository.saveFolder(it, folder, parentId)
                }
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

    fun createAsset(asset: Asset) {
        viewModelScope.launch {
            _createAssetState.emit(LOADING_STATE)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    assetsRepository.createAsset(it, asset)
                }
                _createAssetState.emit(SUCCESS_STATE)
            } //TODO: Properly handle returned errors
            catch (e: AuthException) {
                println("SAVE ASSET - AUTH ERROR: " + e.message)
                _saveFolderState.emit(e.code)
            } catch (e: DataException) {
                println("SAVE ASSET - DATA ERROR: " + e.message)
                _saveFolderState.emit(e.code)
            }
        }
    }

    fun deleteAsset(assetId: String) {
        viewModelScope.launch {
            _deleteAssetState.emit(LOADING_STATE)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    assetsRepository.deleteAsset(it, assetId)
                }
                _deleteAssetState.emit(SUCCESS_STATE)
            } //TODO: Properly handle returned errors
            catch (e: AuthException) {
                println("SAVE ASSET - AUTH ERROR: " + e.message)
                _saveFolderState.emit(e.code)
            } catch (e: DataException) {
                println("SAVE ASSET - DATA ERROR: " + e.message)
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