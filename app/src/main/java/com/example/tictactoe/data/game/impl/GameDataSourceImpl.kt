package com.example.tictactoe.data.game.impl

import com.example.tictactoe.MainActivity
import com.example.tictactoe.data.game.GameDataSource
import com.example.tictactoe.data.game.GameRepository
import com.example.tictactoe.model.GameBoard
import com.example.tictactoe.model.GameDocument
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class GameDataSourceImpl : GameDataSource {

    private val db = Firebase.firestore
    private var gameRepository: GameRepository? = null
    private var gameRoomId: String = ""
    private var userTeam: GameBoard.Team = GameBoard.Team.FIRST

    override suspend fun setGame(gameRoomId: String, userTeam: GameBoard.Team) {
        this.gameRoomId = gameRoomId
        this.userTeam = userTeam
        pullUpdate()
        db.collection(GameDocument.PARENT_COLLECTION).document(gameRoomId)
            .addSnapshotListener { snapshot, e ->
                if (e == null && snapshot != null && snapshot.exists()) {
                    pullUpdate(snapshot)
                }
            }
    }

    override fun subscribe(gameRepository: GameRepository) {
        this.gameRepository = gameRepository
    }

    override fun pushUpdate(gameBoard: GameBoard) {
        val newGameData = hashMapOf(
            GameDocument.GAME_BOARD to gameBoard.toList(),
            GameDocument.LAST_TURN_TEAM to gameBoard.userTeam.id()
        )

        db.collection(GameDocument.PARENT_COLLECTION).document(gameRoomId).set(newGameData, SetOptions.merge())
    }

    override fun getGameRoomId(): String = gameRoomId

    override fun getUserTeam(): GameBoard.Team = userTeam

    override fun restartGame() {
        val newGameData = hashMapOf(
            GameDocument.GAME_BOARD to MutableList(
                MainActivity.GAME_BOARD_EDGE_SIZE * MainActivity.GAME_BOARD_EDGE_SIZE
            ) { 0 },
            GameDocument.LAST_TURN_TEAM to 2
        )

        db.collection(GameDocument.PARENT_COLLECTION).document(gameRoomId).set(newGameData, SetOptions.merge())
    }

    override suspend fun pullUpdate() {
        db.collection(GameDocument.PARENT_COLLECTION).document(gameRoomId).get().await().run {
            pullUpdate(this)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun pullUpdate(gameSnapshot: DocumentSnapshot) {
        val gameBoardData = gameSnapshot[GameDocument.GAME_BOARD] as? List<Int>
        val lastTurnTeamId = gameSnapshot[GameDocument.LAST_TURN_TEAM] as Long
        val currentTurnTeam = !GameBoard.Team.fromId(lastTurnTeamId.toInt())

        gameBoardData?.run {
            gameRepository?.update(
                GameBoard.fromFlatList(this, userTeam),
                currentTurnTeam
            )
        }
    }

}