package com.example.tictactoe.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tictactoe.AppContainer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tictactoe.Accelerometer
import com.example.tictactoe.R
import com.example.tictactoe.model.GameBoard
import com.example.tictactoe.ui.connect.by_id.ConnectByIdRoute
import com.example.tictactoe.ui.connect.by_id.ConnectByIdViewModel
import com.example.tictactoe.ui.game.GameRoute
import com.example.tictactoe.ui.game.GameViewModel
import com.example.tictactoe.ui.start_screen.StartRoute
import com.example.tictactoe.ui.start_screen.StartScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun TictactoeNavGraph(
    appContainer: AppContainer,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    accelerometer: Accelerometer,
    startDestination: String = TictactoeDestinations.START_ROUTE
) {

    LaunchedEffect("navigation") {
        Navigator.sharedFlow.onEach {
            navController.navigate(it) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute == TictactoeDestinations.START_ROUTE) {
                    popUpTo(TictactoeDestinations.START_ROUTE) { inclusive = true }
                }
            }
        }.launchIn(this)
    }

    NavHost(navController, startDestination) {

        composable(TictactoeDestinations.START_ROUTE) {
            val viewModel: StartScreenViewModel = viewModel(
                factory = StartScreenViewModel.provideFactory()
            )
            StartRoute(viewModel)
        }

        composable("${TictactoeDestinations.GAME_ROUTE}/{gameRoomId}/{userTeamId}") {
            val gameRoomId = it.arguments?.getString("gameRoomId")!!
            val userTeamId = it.arguments?.getString("userTeamId")!!

            val viewModel: GameViewModel = viewModel(
                factory = GameViewModel.provideFactory(appContainer.gameRepository)
            )
            LaunchedEffect("gameSetup") {
                launch(Dispatchers.IO) {
                    appContainer.gameRepository.setGame(
                        gameRoomId,
                        GameBoard.Team.fromId(userTeamId.toInt())
                    )
                    appContainer.gameRepository.pullUpdate()
                }
            }
            GameRoute(viewModel)
        }

        composable(TictactoeDestinations.CONNECT_BY_ID_ROUTE) {
            val viewModel: ConnectByIdViewModel = viewModel(
                factory = ConnectByIdViewModel.provideFactory(
                    appContainer.connectByIdRepository,
                    showSnackbarNetworkUnavailable = {
                        val message = appContainer.context.resources
                            .getString(R.string.network_is_unavailable_snackbar)

                        scaffoldState.snackbarHostState.showSnackbar(
                            message,
                            duration = SnackbarDuration.Short
                        )
                    },
                    accelerometer
                )
            )
            ConnectByIdRoute(viewModel)
        }

    }

}

object TictactoeDestinations {
    const val START_ROUTE = "startScreen"
    const val CONNECT_BY_ID_ROUTE = "connectById"
    const val GAME_ROUTE = "game"
}