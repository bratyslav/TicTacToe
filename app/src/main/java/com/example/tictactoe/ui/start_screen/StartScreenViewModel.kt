package com.example.tictactoe.ui.start_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tictactoe.ui.Navigator
import com.example.tictactoe.ui.TictactoeDestinations
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StartScreenUiState(
    val isLogoVisible: Boolean = false
)

class StartScreenViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(StartScreenUiState())
    val uiState: StateFlow<StartScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.run {
            launch {
                delay(LOGO_DELAY_TIME)
                _uiState.update { it.copy(isLogoVisible = true) }
                delay(LOGO_APPEARANCE_TIME + LOGO_EXISTING_TIME)
                _uiState.update { it.copy(isLogoVisible = false) }
                delay(LOGO_DISAPPEARANCE_TIME)
                Navigator.navigateTo(TictactoeDestinations.CONNECT_BY_ID_ROUTE)
            }
        }
    }

    companion object {
        // used to save ui state between configuration changes
        fun provideFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StartScreenViewModel() as T
                }
            }

        const val LOGO_DELAY_TIME = 100L
        const val LOGO_APPEARANCE_TIME = 1500L
        const val LOGO_EXISTING_TIME = 500L
        const val LOGO_DISAPPEARANCE_TIME = 500L
    }

}