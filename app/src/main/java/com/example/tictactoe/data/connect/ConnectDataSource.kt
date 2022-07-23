package com.example.tictactoe.data.connect

import com.example.tictactoe.model.GameBoard

interface ConnectDataSource {

    fun getGameRoomId(): String

    fun getLastOtherRoomIds(): List<String>

    fun setLastOtherRoomIds(ids: List<String>)

    suspend fun isGameRoomExisting(gameRoomId: String): Boolean

    suspend fun createNewGameRoom( gameRoomId: String, team: GameBoard.Team)

    companion object {
        const val GAME_ROOM_ID_LEN = 6
    }

}