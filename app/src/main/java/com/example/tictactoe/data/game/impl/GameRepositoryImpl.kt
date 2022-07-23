package com.example.tictactoe.data.game.impl

import com.example.tictactoe.data.game.GameDataSource
import com.example.tictactoe.data.game.GameRepository
import com.example.tictactoe.data.game.GameUI
import com.example.tictactoe.model.GameBoard

class GameRepositoryImpl(
    override val gameBoardEdgeSize: Int,
    private val gameDataSource: GameDataSource
) : GameRepository {

    init {
        gameDataSource.subscribe(this)
    }

    private var gameBoard = GameBoard(gameBoardEdgeSize, gameDataSource.getUserTeam())
    private var gameUI: GameUI? = null

    override var isUserTurn = false

    override fun subscribe(gameUI: GameUI) {
        this.gameUI = gameUI
    }

    override suspend fun setGame(gameRoomId: String, userTeam: GameBoard.Team) {
        gameDataSource.setGame(gameRoomId, userTeam)
    }

    override suspend fun pullUpdate() {
        gameDataSource.pullUpdate()
    }

    override fun update(gameBoard: GameBoard, currentTurnTeam: GameBoard.Team) {
        this.gameBoard = gameBoard
        isUserTurn = (currentTurnTeam == gameBoard.userTeam)
        gameUI?.update(gameBoard)
        checkForLose()
    }

    override fun tryToPlaceLabel(position: Int, onFailure: () -> Unit) {
        if (
            isUserTurn &&
            position < gameBoardEdgeSize * gameBoardEdgeSize &&
            gameBoard[position] == 0
        ) {
            gameBoard.setLabel(position)
            gameUI?.update(gameBoard)
            gameDataSource.pushUpdate(gameBoard)
            checkForWin()
        } else {
            onFailure()
        }
    }

    override fun getGameRoomId(): String = gameDataSource.getGameRoomId()

    override fun restartGame() {
        gameDataSource.restartGame()
    }

    private fun checkForWin() {
        if (gameBoard.isWon()) {
            gameUI?.onWin()
        }
    }

    private fun checkForLose() {
        if (gameBoard.isLost()) {
            gameUI?.onLose()
        }
    }

}