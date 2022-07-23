package com.example.tictactoe.data.connect.by_id

interface ConnectByIdRepository {

    fun getUserGameRoomId(): String

    fun getLastOtherRoomIds(): List<String>

    fun setLastOtherRoomIds(ids: List<String>)

    suspend fun connectOtherRoom(gameRoomId: String): Boolean

    suspend fun connectUserRoom()

}