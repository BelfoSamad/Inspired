package com.samadtch.inspired

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    //Loading
    private val _loaded = MutableStateFlow(false) //TODO: Make true again
    private val loaded = _loaded.asStateFlow()
    //Authorization Code
    private val _receivedCode = MutableStateFlow<String?>(null)
    private val receivedCode = _receivedCode.asStateFlow()

    fun updatedAppLoaded(loaded: Boolean) {
        viewModelScope.launch { _loaded.emit(loaded) }
    }

    fun updateAuthorizationCode(code: String?) {
        viewModelScope.launch { _receivedCode.emit(code) }
    }
}