package com.samadtch.inspired.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samadtch.inspired.common.LOADING_STATE
import com.samadtch.inspired.common.Result
import com.samadtch.inspired.common.SUCCESS_STATE
import com.samadtch.inspired.common.exceptions.AuthException
import com.samadtch.inspired.common.exceptions.AuthException.Companion.AUTH_TOKEN_SERVER_ERROR_OTHER
import com.samadtch.inspired.common.exceptions.DataException
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_AUTH
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_OTHER
import com.samadtch.inspired.data.repositories.AssetsRepository
import com.samadtch.inspired.data.repositories.FoldersRepository
import com.samadtch.inspired.data.repositories.UserRepository
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.domain.models.Folder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val foldersRepository: FoldersRepository,
    private val assetsRepository: AssetsRepository
) : ViewModel() {

    /* **************************************************************************
     * ************************************* Declarations
     */
    private val _homeUiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    //Action States
    private val _deleteFolderState = MutableStateFlow<Pair<Int?, String>?>(null)
    val deleteFolderState: StateFlow<Pair<Int?, String>?> = _deleteFolderState.asStateFlow()
    private val _saveFolderState = MutableStateFlow<Pair<Int?, Folder>?>(null)
    val saveFolderState: StateFlow<Pair<Int?, Folder>?> = _saveFolderState.asStateFlow()
    private val _createAssetState = MutableStateFlow<Pair<Int?, Asset>?>(null)
    val createAssetState: StateFlow<Pair<Int?, Asset>?> = _createAssetState.asStateFlow()
    private val _deleteAssetState = MutableStateFlow<Pair<Int?, String>?>(null)
    val deleteAssetState: StateFlow<Pair<Int?, String>?> = _deleteAssetState.asStateFlow()

    /* **************************************************************************
     * ************************************* Init
     */
    init {
        fetchAllItems()
    }

    /* **************************************************************************
     * ************************************* Functions
     */
    private fun fetchAllItems() {
        viewModelScope.launch {
            userRepository.performActionWithFreshToken { token ->
                foldersRepository.getAllItems(token).collect {
                    _homeUiState.emit(
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
                    )
                }
            }
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            _deleteFolderState.emit(LOADING_STATE to folderId)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    foldersRepository.deleteFolder(it, folderId)
                }
                _deleteFolderState.emit(SUCCESS_STATE to folderId)
            } catch (e: AuthException) {
                //only if error is from server, keep logged-in and try again
                if (e.code == AUTH_TOKEN_SERVER_ERROR_OTHER)
                    _deleteFolderState.emit(AUTH_TOKEN_SERVER_ERROR_OTHER to folderId)
                //else logout
                else _deleteFolderState.emit(API_ERROR_AUTH to folderId)
            } catch (e: DataException) {
                if (e.code in DataException.handleableErrors) _deleteFolderState.emit(e.code to folderId)
                else _deleteFolderState.emit(API_ERROR_OTHER to folderId)
            }
        }
    }

    fun saveFolder(folder: Folder) {
        viewModelScope.launch {
            _saveFolderState.emit(LOADING_STATE to folder)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    _saveFolderState.emit(SUCCESS_STATE to foldersRepository.saveFolder(it, folder))
                }
            } catch (e: AuthException) {
                //only if error is from server, keep logged-in and try again
                if (e.code == AUTH_TOKEN_SERVER_ERROR_OTHER)
                    _saveFolderState.emit(AUTH_TOKEN_SERVER_ERROR_OTHER to folder)
                //else logout
                else _saveFolderState.emit(API_ERROR_AUTH to folder)
            } catch (e: DataException) {
                if (e.code in DataException.handleableErrors) _saveFolderState.emit(e.code to folder)
                else _saveFolderState.emit(API_ERROR_OTHER to folder)
            }
        }
    }

    fun createAsset(asset: Asset, assetFile: AssetFile) {
        viewModelScope.launch {
            _createAssetState.emit(LOADING_STATE to asset)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    _createAssetState.emit(
                        SUCCESS_STATE to
                                assetsRepository.createAsset(it, asset, assetFile)
                    )
                }
            } catch (e: AuthException) {
                //only if error is from server, keep logged-in and try again
                if (e.code == AUTH_TOKEN_SERVER_ERROR_OTHER)
                    _createAssetState.emit(AUTH_TOKEN_SERVER_ERROR_OTHER to asset)
                //else logout
                else _createAssetState.emit(API_ERROR_AUTH to asset)
            } catch (e: DataException) {
                if (e.code in DataException.handleableErrors) _createAssetState.emit(e.code to asset)
                else _createAssetState.emit(API_ERROR_OTHER to asset)
            }
        }
    }

    fun deleteAsset(assetId: String) {
        viewModelScope.launch {
            _deleteAssetState.emit(LOADING_STATE to assetId)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    assetsRepository.deleteAsset(it, assetId)
                }
                _deleteAssetState.emit(SUCCESS_STATE to assetId)
            } catch (e: AuthException) {
                //only if error is from server, keep logged-in and try again
                if (e.code == AUTH_TOKEN_SERVER_ERROR_OTHER)
                    _deleteAssetState.emit(AUTH_TOKEN_SERVER_ERROR_OTHER to assetId)
                //else logout
                else _deleteAssetState.emit(API_ERROR_AUTH to assetId)
            } catch (e: DataException) {
                if (e.code in DataException.handleableErrors) _deleteAssetState.emit(e.code to assetId)
                else _deleteAssetState.emit(API_ERROR_OTHER to assetId)
            }
        }
    }

    // reset all states since only one is active at a time
    fun resetStates() {
        viewModelScope.launch {
            _saveFolderState.emit(null)
            _deleteFolderState.emit(null)
            _createAssetState.emit(null)
            _deleteAssetState.emit(null)
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