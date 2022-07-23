package com.example.tictactoe.ui.game

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.tictactoe.R
import com.example.tictactoe.model.GameBoard
import com.example.tictactoe.ui.game.GameViewModel.Companion.POPUP_EXISTING_DELAY
import com.example.tictactoe.ui.theme.AquaLake
import com.example.tictactoe.ui.theme.CheGuevaraRed

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameRoute(viewModel: GameViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    val config = LocalConfiguration.current
    val boardSize = when (config.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            config.screenWidthDp.dp
        }
        else -> {
            config.screenHeightDp.dp
        }
    }
    val whichTurn = if (uiState.isUserTurn)
            stringResource(R.string.game_route_your_turn)
        else
        stringResource(R.string.game_route_rival_turn)

    GameResultPopup(message = stringResource(R.string.game_route_you_win), uiState.isWon)
    GameResultPopup(message = stringResource(R.string.game_route_you_lost), uiState.isLost)

    if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Column(Modifier.padding(start = 16.dp)) {
                Row {
                    AnimatedVisibility(visible = uiState.gameRoomId.isNotEmpty()) {
                        GameRoomId(uiState.gameRoomId)
                    }
                }
                Row {
                    Text(
                        whichTurn,
                        fontSize = 24.sp,
                        color = CheGuevaraRed
                    )
                }
            }

            Row {
                GameBoardBox(viewModel, uiState, boardSize)
            }
            Row(
                Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                RefreshButton(viewModel)
            }
        }
    } else {
        Row(
            Modifier.fillMaxSize()
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Row {
                    AnimatedVisibility(visible = uiState.gameRoomId.isNotEmpty()) {
                        GameRoomId(uiState.gameRoomId)
                    }
                }
                Row {
                    Text(
                        whichTurn,
                        fontSize = 24.sp,
                        color = CheGuevaraRed
                    )
                }
            }
            Column(Modifier.weight(2f)) {
                GameBoardBox(viewModel, uiState, boardSize)
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                RefreshButton(viewModel)
            }
        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameResultPopup(message: String, isVisible: Boolean) {
    Popup(alignment = Alignment.Center) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = TweenSpec(durationMillis = (POPUP_EXISTING_DELAY / 3).toInt())),
            exit = fadeOut(animationSpec = TweenSpec(durationMillis = (POPUP_EXISTING_DELAY / 3).toInt()))
        ) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(16.dp))
            ) {
                Text(
                    message,
                    fontSize = 60.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(AquaLake)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun GameRoomId(id: String) {
    Column {
        Text(
            stringResource(R.string.game_route_game_room_id),
            fontSize = 14.sp,
            color = AquaLake
        )
        Text(
            id,
            fontSize = 32.sp,
            color = AquaLake
        )
    }
}

@Composable
fun RefreshButton(viewModel: GameViewModel) {
    Button(
        onClick = { viewModel.restartGame() },
        shape = CircleShape,
        modifier = Modifier.height(64.dp)
    ) {
        Icon(Icons.Default.Refresh, "refresh")
    }
}

@Composable
fun GameBoardBox(viewModel: GameViewModel, uiState: GameUiState, boardSize: Dp) {
    Box(
        Modifier.size(boardSize)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_tictactoe_empty),
            contentDescription = "board",
            modifier = Modifier
                .fillMaxSize()
        )
        GameBoard(viewModel, uiState, boardSize)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun GameBoard(viewModel: GameViewModel, uiState: GameUiState, boardSize: Dp) {
    val cellSize = boardSize / viewModel.gameBoardEdgeSize
    val haptic = LocalHapticFeedback.current

    LazyVerticalGrid(
        cells = GridCells.Fixed(viewModel.gameBoardEdgeSize),
    ) {
        items(uiState.gameBoard.size) { cellIndex ->
            val painter =
                if (uiState.gameBoard[cellIndex] == GameBoard.Team.FIRST.id())
                    painterResource(id = R.drawable.ic_tictactoe_x)
                else
                    if (uiState.gameBoard[cellIndex] == GameBoard.Team.SECOND.id())
                        painterResource(id = R.drawable.ic_tictactoe_o)
                    else
                        null

            Row(
                modifier = Modifier
                    .size(cellSize, cellSize)
                    .noRippleClickable {
                        viewModel.onClickCell(
                            cellIndex,
                            onFailure = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = (painter != null),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    painter?.run {
                        Image(painter = painter, contentDescription = "label")
                    }
                }
            }
        }
    }
}

inline fun Modifier.noRippleClickable(crossinline onClick: ()->Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}