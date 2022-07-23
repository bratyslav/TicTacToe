package com.example.tictactoe.ui.connect.by_id

import android.hardware.SensorEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tictactoe.Accelerometer
import com.example.tictactoe.AccelerometerListener
import com.example.tictactoe.data.connect.ConnectDataSource.Companion.GAME_ROOM_ID_LEN
import com.example.tictactoe.ui.Navigator
import com.example.tictactoe.data.connect.by_id.ConnectByIdRepository
import com.example.tictactoe.model.GameBoard
import com.example.tictactoe.ui.TictactoeDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class ConnectByIdUiState(
    val userGameRoomId: String = "",
    val otherUserGameRoomId: String = "",
    val userTeamId: Int = 1,
    val isInvalidIdErrorShowing: Boolean = false,
    val isClickable: Boolean = true,
    val lastOtherRoomIds: List<String> = emptyList(),
    val posAxisX: Float = 0f,
    val posAxisY: Float = 0f
)

class ConnectByIdViewModel(
    private val connectByIdRepository: ConnectByIdRepository,
    private val showSnackbarNetworkUnavailable: suspend () -> Unit,
    accelerometer: Accelerometer
) : ViewModel(), AccelerometerListener {

    private val validateIdsRegex = Regex(NUMBERS_LETTERS_REGEX)
    private val _uiState = MutableStateFlow(ConnectByIdUiState())
    val uiState: StateFlow<ConnectByIdUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(userGameRoomId = connectByIdRepository.getUserGameRoomId()) }
        accelerometer.subscribe(this)
    }

    fun updateLastOtherRoomIds() {
        val lastIds = connectByIdRepository.getLastOtherRoomIds()
        _uiState.update {
            it.copy(
                lastOtherRoomIds = lastIds
            )
        }
    }

    fun onOtherGameRoomIdValueChange(newOtherUserId: String) {
        // validate
        if (!validateIdsRegex.matches(newOtherUserId)) {
            return
        }

        val isEmptyIdErrorShouldShowing =
            _uiState.value.isInvalidIdErrorShowing && newOtherUserId.isEmpty()
        _uiState.update {
            it.copy(
                otherUserGameRoomId = newOtherUserId,
                isInvalidIdErrorShowing = isEmptyIdErrorShouldShowing
            )
        }
    }

    fun enterOtherRoom() {
        lockButtons()

        val gameRoomId = uiState.value.otherUserGameRoomId
        val newLastOtherRoomIds = (uiState.value.lastOtherRoomIds + gameRoomId).toMutableList()
        if (newLastOtherRoomIds.size > MAX_LAST_ROOM_IDS) {
            newLastOtherRoomIds.removeFirst()
        }
        viewModelScope.launch {
            try {
                val isSuccessful = connectByIdRepository.connectOtherRoom(gameRoomId)
                if (isSuccessful) {
                    connectByIdRepository.setLastOtherRoomIds(newLastOtherRoomIds.toList())
                    Navigator.navigateTo(
                        "${TictactoeDestinations.GAME_ROUTE}/${gameRoomId}/${GameBoard.Team.SECOND.id()}"
                    )
                    unlockButtons()
                } else {
                    _uiState.update { it.copy(isInvalidIdErrorShowing = true) }
                    unlockButtons()
                }
            } catch (e: IOException) {
                showSnackbarNetworkUnavailable()
                unlockButtons()
            }
        }
    }

    fun hideInvalidIdError() {
        _uiState.update { it.copy(isInvalidIdErrorShowing = false) }
    }

    fun enterUserRoom() {
        lockButtons()

        val gameRoomId = uiState.value.userGameRoomId
        viewModelScope.launch {
            try {
                connectByIdRepository.connectUserRoom()
                Navigator.navigateTo(
                    "${TictactoeDestinations.GAME_ROUTE}/${gameRoomId}/${GameBoard.Team.FIRST.id()}"
                )
                unlockButtons()
            } catch (e: IOException) {
                showSnackbarNetworkUnavailable()
                unlockButtons()
            }
        }
    }

    private fun lockButtons() {
        _uiState.update { it.copy(isClickable = false) }
    }

    private fun unlockButtons() {
        _uiState.update { it.copy(isClickable = true) }
    }

    override fun onAccelerometerEvent(event: SensorEvent?) {
        event?.run {
            _uiState.update {
                it.copy(
                    posAxisX = event.values[0],
                    posAxisY = event.values[1]
                )
            }
        }
    }

    companion object {
        // used to save ui state between configuration changes
        fun provideFactory(
            connectByIdRepository: ConnectByIdRepository,
            showSnackbarNetworkUnavailable: suspend () -> Unit,
            accelerometer: Accelerometer
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ConnectByIdViewModel(
                    connectByIdRepository,
                    showSnackbarNetworkUnavailable,
                    accelerometer
                ) as T
            }
        }

        const val MAX_LAST_ROOM_IDS = 2
        const val NUMBERS_LETTERS_REGEX = "[a-z0-9]{0,${GAME_ROOM_ID_LEN}}?"
    }

}