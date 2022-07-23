package com.example.tictactoe.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object Navigator {

    private val _sharedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun navigateTo(route: String) {
        _sharedFlow.tryEmit(route)
    }

}