package com.example.tictactoe.data.game

interface GameRepository {

    fun subscribe(userInterface: GameUserInterface)

    fun tryToPlaceMark(position: Int)

}