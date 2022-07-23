package com.example.tictactoe.data.game

import com.example.tictactoe.model.GameBoard

interface GameUI {

    fun update(gameBoard: GameBoard)

    fun onWin()

    fun onLose()

}