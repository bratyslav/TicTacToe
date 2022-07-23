package com.example.tictactoe

import com.example.tictactoe.model.GameBoard
import org.junit.Test

class GameBoardUnitTest {

    @Test
    fun checkForWin_isCorrect_case1() {
        val gameBoardCells = MutableList(3) { MutableList(3) {0} }
        gameBoardCells[1][1] = 1
        gameBoardCells[1][2] = 1
        gameBoardCells[2][0] = 2
        gameBoardCells[2][1] = 2
        gameBoardCells[2][2] = 2
        val gameBoard = GameBoard(3, GameBoard.Team.FIRST, gameBoardCells)
        assert(!gameBoard.isWon())
        assert(gameBoard.isLost())
    }

    @Test
    fun checkForWin_isCorrect_case2() {
        val gameBoardCells = MutableList(4) { MutableList(4) {0} }
        gameBoardCells[0][3] = 1
        gameBoardCells[1][3] = 1
        gameBoardCells[2][3] = 1
        gameBoardCells[3][3] = 1
        gameBoardCells[0][2] = 2
        gameBoardCells[3][2] = 1
        gameBoardCells[3][1] = 2
        gameBoardCells[0][1] = 2
        gameBoardCells[1][2] = 2
        val gameBoard = GameBoard(4, GameBoard.Team.FIRST, gameBoardCells)
        assert(gameBoard.isWon())
        assert(!gameBoard.isLost())
    }

    @Test
    fun checkForWin_isCorrect_case3() {
        val gameBoardCells = MutableList(2) { MutableList(2) {0} }
        gameBoardCells[0][0] = 2
        gameBoardCells[1][1] = 2
        gameBoardCells[1][0] = 1
        val gameBoard = GameBoard(2, GameBoard.Team.FIRST, gameBoardCells)
        assert(!gameBoard.isWon())
        assert(gameBoard.isLost())
    }

}