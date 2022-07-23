package com.example.tictactoe.data.connect.by_id.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.tictactoe.MainActivity
import com.example.tictactoe.data.connect.ConnectDataSource
import com.example.tictactoe.data.connect.ConnectDataSource.Companion.GAME_ROOM_ID_LEN
import com.example.tictactoe.model.GameBoard
import com.example.tictactoe.model.GameDocument
import com.example.tictactoe.model.AppPrefs
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*


class ConnectDataSourceImpl(private val context: Context) : ConnectDataSource {

    private val db = Firebase.firestore
    private val roomIdsPrefs = context.getSharedPreferences(AppPrefs.LAST_ROOM_IDS, Context.MODE_PRIVATE)

    override fun getGameRoomId(): String {
        var gameRoomId = roomIdsPrefs.getString(AppPrefs.USER_ROOM_ID, "") ?: ""
        if (gameRoomId.isEmpty()) { // than create and save the new one
            gameRoomId = UUID.randomUUID().toString().substring(0, GAME_ROOM_ID_LEN)
            with (roomIdsPrefs.edit()) {
                putString(AppPrefs.USER_ROOM_ID, gameRoomId)
                apply()
            }
        }
        return gameRoomId
    }

    override fun getLastOtherRoomIds(): List<String> {
        val idsSet = roomIdsPrefs.getStringSet(AppPrefs.LAST_ROOM_IDS, null)
        idsSet?.run {
            return this.toList()
        }
        return emptyList()
    }

    override fun setLastOtherRoomIds(ids: List<String>) {
        with (roomIdsPrefs.edit()) {
            this.putStringSet(AppPrefs.LAST_ROOM_IDS, ids.toSet())
            apply()
        }
    }

    override suspend fun isGameRoomExisting(gameRoomId: String): Boolean {
        if (gameRoomId.isEmpty()) {
            return false
        }
        if (isNetworkUnavailable()) {
            throw IOException("Can't reach Backend")
        }

        return db.collection(GameDocument.PARENT_COLLECTION).document(gameRoomId).get().await().exists()
    }

    override suspend fun createNewGameRoom(gameRoomId: String, team: GameBoard.Team) {
        if (isNetworkUnavailable()) {
            throw IOException("Can't reach Backend")
        }

        val gameBoardList = GameBoard(MainActivity.GAME_BOARD_EDGE_SIZE, team).toList()
        val game = hashMapOf(
            GameDocument.GAME_BOARD to gameBoardList,
            GameDocument.LAST_TURN_TEAM to 2
        )
        db.collection(GameDocument.PARENT_COLLECTION).document(gameRoomId).set(game).await()
    }

    private fun isNetworkUnavailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return true
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return true
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> false
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> false
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> false
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> false
            else -> true
        }
    }

}