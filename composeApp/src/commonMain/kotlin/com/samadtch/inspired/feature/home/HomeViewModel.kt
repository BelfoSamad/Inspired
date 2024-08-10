package com.samadtch.inspired.feature.home

import com.samadtch.inspired.common.Result
import com.samadtch.inspired.common.exceptions.AuthException
import com.samadtch.inspired.common.exceptions.DataException
import com.samadtch.inspired.data.repositories.FoldersRepository
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.Folder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    /* **************************************************************************
     * ************************************* Functions
     */

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