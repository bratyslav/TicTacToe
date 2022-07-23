package com.example.tictactoe.data.game

import com.example.tictactoe.model.GameBoard

interface GameRepository {

    val gameBoardEdgeSize: Int
    var isUserTurn: Boolean

    fun subscribe(gameUI: GameUI)

    suspend fun setGame(gameRoomId: String, userTeam: GameBoard.Team)

    suspend fun pullUpdate()

    fun update(gameBoard: GameBoard, currentTurnTeam: GameBoard.Team)

    fun tryToPlaceLabel(position: Int, onFailure: () -> Unit)

    fun getGameRoomId(): String

    fun restartGame()

}