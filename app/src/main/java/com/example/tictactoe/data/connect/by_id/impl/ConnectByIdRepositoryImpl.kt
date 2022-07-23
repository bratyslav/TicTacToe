package com.example.tictactoe.data.connect.by_id.impl

import com.example.tictactoe.data.connect.ConnectDataSource
import com.example.tictactoe.data.connect.by_id.ConnectByIdRepository
import com.example.tictactoe.model.GameBoard

class ConnectByIdRepositoryImpl(
    private val connectDataSource: ConnectDataSource
) : ConnectByIdRepository {

    override fun getUserGameRoomId(): String = connectDataSource.getGameRoomId()

    override fun getLastOtherRoomIds(): List<String> = connectDataSource.getLastOtherRoomIds()

    override fun setLastOtherRoomIds(ids: List<String>) {
        connectDataSource.setLastOtherRoomIds(ids)
    }

    override suspend fun connectOtherRoom(gameRoomId: String): Boolean {
        return connectDataSource.isGameRoomExisting(gameRoomId)
    }

    override suspend fun connectUserRoom() {
        val gameRoomId = getUserGameRoomId()
        if (!connectDataSource.isGameRoomExisting(gameRoomId)) {
            connectDataSource.createNewGameRoom(
                gameRoomId,
                GameBoard.Team.FIRST, // owner is always the first team, probably will change later
            )
        }
    }

}