package com.samadtch.inspired.feature.boarding

import com.samadtch.inspired.common.LOADING_STATE
import com.samadtch.inspired.common.SUCCESS_STATE
import com.samadtch.inspired.common.exceptions.AuthException
import com.samadtch.inspired.common.exceptions.DataException
import com.samadtch.inspired.data.repositories.ConfigRepository
import com.samadtch.inspired.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

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

    /* **************************************************************************
     * ************************************* Functions
     */
    fun setFirstTimeOpened() = viewModelScope.launch { configRepository.setFirstTimeOpened() }

    fun authenticate(codeVerifier: String, code: String) {
        viewModelScope.launch {
            println("AUTH IN")
            _loginState.emit(LOADING_STATE)//Loading State
            try {
                userRepository.authenticate(codeVerifier, code)
                _loginState.emit(SUCCESS_STATE)
            } //TODO: Properly handle returned errors
            catch (e: AuthException) {
                println("CREATE FOLDER - AUTH ERROR: " + e.message)
                _loginState.emit(e.code)
            } catch (e: DataException) {
                println("CREATE FOLDER - DATA ERROR: " + e.message)
                _loginState.emit(e.code)
            }
        }
    }

    /* **************************************************************************
     * ************************************* UI States
     */
    data class BoardingUiState(val isFirstOpen: Boolean, val isLoggedIn: Boolean)
}