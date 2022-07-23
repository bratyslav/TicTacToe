package com.example.tictactoe.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tictactoe.data.game.GameRepository
import com.example.tictactoe.data.game.GameUI
import com.example.tictactoe.model.GameBoard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val gameRoomId: String,
    val gameBoard: List<Int>,
    val isUserTurn: Boolean,
    val isWon: Boolean = false,
    val isLost: Boolean = false
)

class GameViewModel(private val gameRepository: GameRepository) : ViewModel(), GameUI {

    private val _uiState = MutableStateFlow(
        GameUiState(
            gameBoard = List(gameRepository.gameBoardEdgeSize * gameRepository.gameBoardEdgeSize) { 0 },
            gameRoomId = gameRepository.getGameRoomId(),
            isUserTurn = gameRepository.isUserTurn
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val gameIsActive get() = !_uiState.value.isLost && !_uiState.value.isWon
    val gameBoardEdgeSize = gameRepository.gameBoardEdgeSize

    init {
        gameRepository.subscribe(this)
    }

    override fun update(gameBoard: GameBoard) {
        _uiState.update {
            it.copy(
                gameBoard = gameBoard.toList(),
                gameRoomId = gameRepository.getGameRoomId(),
                isUserTurn = gameRepository.isUserTurn
            )
        }
    }

    override fun onWin() {
        if (gameIsActive) {
            _uiState.update { it.copy(isWon = true) }
            viewModelScope.launch {
                delay(POPUP_EXISTING_DELAY / 2)
                restartGame()
                _uiState.update { it.copy(isWon = false) }
            }
        }
    }

    override fun onLose() {
        if (gameIsActive) {
            _uiState.update { it.copy(isLost = true) }
            viewModelScope.launch {
                delay(POPUP_EXISTING_DELAY)
                restartGame()
                _uiState.update { it.copy(isLost = false) }
            }
        }
    }

    fun onClickCell(position: Int, onFailure: () -> Unit) {
        if (gameIsActive) {
            gameRepository.tryToPlaceLabel(
                position,
                onFailure = onFailure
            )
        }
    }

    fun restartGame() {
        gameRepository.restartGame()
    }

    companion object {
        // used to save ui state between configuration changes
        fun provideFactory(gameRepository: GameRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(gameRepository) as T
                }
            }

        const val POPUP_EXISTING_DELAY = 3000L
    }

}