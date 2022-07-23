package com.example.tictactoe.data.game

import com.example.tictactoe.model.GameBoard

interface GameDataSource {

    suspend fun setGame(gameRoomId: String, userTeam: GameBoard.Team)

    fun subscribe(gameRepository: GameRepository)

    fun pushUpdate(gameBoard: GameBoard)

    fun getGameRoomId(): String

    fun getUserTeam(): GameBoard.Team

    fun restartGame()

    suspend fun pullUpdate()

}