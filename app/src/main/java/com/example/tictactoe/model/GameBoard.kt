package com.example.tictactoe.model

import kotlin.math.sqrt

class GameBoard(
    private val edgeSize: Int,
    val userTeam: Team,
    private val board: MutableList<MutableList<Int>> = MutableList(edgeSize) { MutableList(edgeSize) { 0 } }
) {

    fun isWon(): Boolean {
        return isWon(userTeam.id())
    }

    fun isLost(): Boolean {
        return isWon((!userTeam).id())
    }

    private fun isWon(teamId: Int): Boolean {
        // check rows
        for (rowIdx in 0 until edgeSize) {
            // if all labels belong to this team
            if (board[rowIdx].all { it == teamId }) {
                return true
            }
        }

        // check cols
        for (colIdx in 0 until edgeSize) {
            val col = MutableList(edgeSize) { 0 }
            for (rowIdx in 0 until edgeSize) {
                col[rowIdx] = board[rowIdx][colIdx]
            }

            if (col.all { it == teamId }) {
                return true
            }
        }

        // check diagonals
        val d1 = MutableList(edgeSize) { 0 }
        val d2 = MutableList(edgeSize) { 0 }
        for (d in 0 until edgeSize) {
            d1[d] = board[d][d]
            d2[d] = board[d][edgeSize - d - 1]
        }
        if (d1.all { it == teamId } || d2.all { it == teamId }) {
            return true
        }

        return false
    }

    fun toList(): List<Int> = board.flatten().toList()

    operator fun get(index: Int): Int = board.flatten()[index]

    fun setLabel(index: Int) {
        board[index / edgeSize][index % edgeSize] = userTeam.id()
    }

    enum class Team {

        FIRST, SECOND;

        fun id(): Int {
            return when (this) {
                FIRST -> 1
                SECOND -> 2
            }
        }

        companion object {
            fun fromId(id: Int): Team {
                return if (id == 1) FIRST else SECOND
            }
        }

        operator fun not(): Team {
            return if (this == FIRST) SECOND else FIRST
        }

    }

    companion object {
        fun fromFlatList(list: List<Int>, team: Team): GameBoard {
            val edgeSize = sqrt(list.size.toDouble()).toInt()
            val board = MutableList(edgeSize) { MutableList(edgeSize) { 0 } }
            for (i in list.indices) {
                board[i / edgeSize][i % edgeSize] = list[i]
            }
            return GameBoard(edgeSize, team, board)
        }
    }

}