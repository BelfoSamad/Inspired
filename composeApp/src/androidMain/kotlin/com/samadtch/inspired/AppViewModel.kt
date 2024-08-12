package com.samadtch.inspired

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samadtch.inspired.data.repositories.ConfigRepository
import com.samadtch.inspired.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /* **************************************************************************
     * ************************************* Declarations
     */
    private val _initUiState = MutableStateFlow(AppUiState())
    val initUiState: StateFlow<AppUiState> = _initUiState.asStateFlow()

    /* **************************************************************************
     * ************************************* Init
     */
    init {
        viewModelScope.launch {
            _initUiState.emit(AppUiState(links = configRepository.getAppDetails()))
        }
    }

    fun logout() = viewModelScope.launch {
        userRepository.logout()
    }

}