package com.example.tictactoe

import android.content.Context
import com.example.tictactoe.data.connect.by_id.ConnectByIdRepository
import com.example.tictactoe.data.connect.by_id.impl.ConnectByIdRepositoryImpl
import com.example.tictactoe.data.connect.by_id.impl.ConnectDataSourceImpl
import com.example.tictactoe.data.game.GameRepository
import com.example.tictactoe.data.game.impl.GameDataSourceImpl
import com.example.tictactoe.data.game.impl.GameRepositoryImpl

interface AppContainer {

    val context: Context

    val connectByIdRepository: ConnectByIdRepository

    val gameRepository: GameRepository

}

class AppContainerImpl(override val context: Context) : AppContainer {

    override val connectByIdRepository = ConnectByIdRepositoryImpl(
        ConnectDataSourceImpl(context)
    )

    override val gameRepository: GameRepository = GameRepositoryImpl(
        MainActivity.GAME_BOARD_EDGE_SIZE,
        GameDataSourceImpl()
    )

}