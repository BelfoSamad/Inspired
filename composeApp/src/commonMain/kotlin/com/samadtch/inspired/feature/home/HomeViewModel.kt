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
    private val _deleteFolderState = MutableStateFlow<Int?>(null)
    val deleteFolderState: StateFlow<Int?> = _deleteFolderState.asStateFlow()
    private val _saveFolderState = MutableStateFlow<Int?>(null)
    val saveFolderState: StateFlow<Int?> = _saveFolderState.asStateFlow()
    private val _createAssetState = MutableStateFlow<Int?>(null)
    val createAssetState: StateFlow<Int?> = _createAssetState.asStateFlow()
    private val _deleteAssetState = MutableStateFlow<Int?>(null)
    val deleteAssetState: StateFlow<Int?> = _deleteAssetState.asStateFlow()

    /* **************************************************************************
     * ************************************* Init
     */
    init { fetchAllItems() }

    /* **************************************************************************
     * ************************************* Functions
     */
    fun fetchAllItems() {
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
            _deleteFolderState.emit(LOADING_STATE)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    foldersRepository.deleteFolder(it, folderId)
                }
                _deleteFolderState.emit(SUCCESS_STATE)
            } catch (e: AuthException) {
                //only if error is from server, keep logged-in and try again
                if (e.code == AUTH_TOKEN_SERVER_ERROR_OTHER)
                    _deleteFolderState.emit(AUTH_TOKEN_SERVER_ERROR_OTHER)
                //else logout
                else _deleteFolderState.emit(API_ERROR_AUTH)
            } catch (e: DataException) {
                if (e.code in DataException.handleableErrors) _deleteFolderState.emit(e.code)
                else _deleteFolderState.emit(API_ERROR_OTHER)
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
            } catch (e: AuthException) {
                //only if error is from server, keep logged-in and try again
                if (e.code == AUTH_TOKEN_SERVER_ERROR_OTHER)
                    _saveFolderState.emit(AUTH_TOKEN_SERVER_ERROR_OTHER)
                //else logout
                else _saveFolderState.emit(API_ERROR_AUTH)
            } catch (e: DataException) {
                if (e.code in DataException.handleableErrors) _saveFolderState.emit(e.code)
                else _saveFolderState.emit(API_ERROR_OTHER)
            }
        }
    }

    fun createAsset(asset: Asset, assetFile: AssetFile) {
        viewModelScope.launch {
            _createAssetState.emit(LOADING_STATE)//Loading State
            try {
                userRepository.performActionWithFreshToken {
                    assetsRepository.createAsset(it, asset, assetFile)
                }
                _createAssetState.emit(SUCCESS_STATE)
            } catch (e: AuthException) {
                //only if error is from server, keep logged-in and try again
                if (e.code == AUTH_TOKEN_SERVER_ERROR_OTHER)
                    _createAssetState.emit(AUTH_TOKEN_SERVER_ERROR_OTHER)
                //else logout
                else _createAssetState.emit(API_ERROR_AUTH)
            } catch (e: DataException) {
                if (e.code in DataException.handleableErrors) _createAssetState.emit(e.code)
                else _createAssetState.emit(API_ERROR_OTHER)
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
            } catch (e: AuthException) {
                //only if error is from server, keep logged-in and try again
                if (e.code == AUTH_TOKEN_SERVER_ERROR_OTHER)
                    _deleteAssetState.emit(AUTH_TOKEN_SERVER_ERROR_OTHER)
                //else logout
                else _deleteAssetState.emit(API_ERROR_AUTH)
            } catch (e: DataException) {
                if (e.code in DataException.handleableErrors) _deleteAssetState.emit(e.code)
                else _deleteAssetState.emit(API_ERROR_OTHER)
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