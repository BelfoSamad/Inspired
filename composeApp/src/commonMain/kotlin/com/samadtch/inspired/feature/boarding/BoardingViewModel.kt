package com.samadtch.inspired.feature.boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samadtch.inspired.common.LOADING_STATE
import com.samadtch.inspired.common.SUCCESS_STATE
import com.samadtch.inspired.common.exceptions.AuthException
import com.samadtch.inspired.data.repositories.ConfigRepository
import com.samadtch.inspired.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BoardingViewModel(
    private val configRepository: ConfigRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /* **************************************************************************
     * ************************************* Declarations
     */
    val uiState = combine(
        configRepository.isFirstOpen(),
        configRepository.isLoggedIn()
    ) { firstOpen, loggedIn ->
        BoardingUiState(
            links = configRepository.getAppDetails(),
            isFirstOpen = firstOpen,
            isLoggedIn = loggedIn
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    //Login State
    private val _loginState = MutableStateFlow<Int?>(null)
    val loginState: StateFlow<Int?> = _loginState.asStateFlow()
    private val _logoutState = MutableStateFlow<Unit?>(null)
    val logOutState: StateFlow<Unit?> = _logoutState.asStateFlow()

    /* **************************************************************************
     * ************************************* Functions
     */
    fun setFirstTimeOpened() = viewModelScope.launch { configRepository.setFirstTimeOpened() }

    fun authenticate(codeVerifier: String, code: String) {
        viewModelScope.launch {
            _loginState.emit(LOADING_STATE)//Loading State
            try {
                userRepository.authenticate(codeVerifier, code)
                _loginState.emit(SUCCESS_STATE)
                _logoutState.emit(null)
            } catch (e: AuthException) {
                _loginState.emit(e.code)
            }
        }
    }

    fun logout() = viewModelScope.launch {
        userRepository.logout()
        _logoutState.emit(Unit)
        _loginState.emit(null)
    }

    /* **************************************************************************
     * ************************************* UI States
     */
    data class BoardingUiState(
        val links: Map<String, String> = mapOf(),
        val isFirstOpen: Boolean,
        val isLoggedIn: Boolean
    )
}