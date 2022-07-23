package com.example.tictactoe.ui.connect.by_id

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.R
import com.example.tictactoe.ui.game.noRippleClickable
import com.example.tictactoe.ui.theme.*
import com.example.tictactoe.ui.utils.Border
import com.example.tictactoe.ui.utils.border

@Composable
fun ConnectByIdRoute(viewModel: ConnectByIdViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    val config = LocalConfiguration.current
    val topPadding = when (config.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            128.dp
        }
        else -> {
            16.dp
        }
    }

    Image(
        painter = painterResource(id = R.drawable.ic_tictactoe),
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                when (config.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        translationY = 16 * uiState.value.posAxisY - 50
                    }
                    else -> {
                        translationX = 16 * uiState.value.posAxisY
                    }
                }
            },
        contentDescription = "",
        contentScale = ContentScale.Crop,
        alpha = 0.15f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding)
            .verticalScroll(rememberScrollState())
            .noRippleClickable { focusManager.clearFocus() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EnterUserRoomButton(viewModel, uiState)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                "or",
                fontSize = 80.sp,
                color = Gilded
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EnterOtherIdCell(viewModel, uiState, focusManager)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 64.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EnterOtherRoomButton(viewModel, uiState, focusManager)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnterOtherIdCell(
    viewModel: ConnectByIdViewModel,
    uiState: State<ConnectByIdUiState>,
    focusManager: FocusManager
) {
    val isFocused = remember { mutableStateOf(false) }

    Column {
        Row {
            Column(Modifier.width(256.dp)) {
                TextField(
                    value = uiState.value.otherUserGameRoomId,
                    onValueChange = { newValue -> viewModel.onOtherGameRoomIdValueChange(newValue) },
                    placeholder = { Text(stringResource(R.string.connect_by_id_enter_other_id_placeholder)) },
                    modifier = Modifier.onFocusChanged { state ->
                        isFocused.value = state.isFocused
                        if (state.isFocused) {
                            viewModel.updateLastOtherRoomIds()
                            viewModel.hideInvalidIdError()
                        }
                    }
                )

                val itemBorder = Border(1.dp, Color.LightGray)

                AnimatedVisibility(visible = isFocused.value) {
                    Column {
                        uiState.value.lastOtherRoomIds.forEach { item ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .border(
                                        start = itemBorder,
                                        end = itemBorder,
                                        bottom = itemBorder
                                    )
                                    .clickable {
                                        viewModel.onOtherGameRoomIdValueChange(item)
                                        focusManager.clearFocus()
                                    }
                            ) {
                                Text(
                                    item,
                                    color = Color.Black,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = uiState.value.isInvalidIdErrorShowing) {
            Row {
                Text(
                    stringResource(R.string.connect_by_id_enter_other_id_placeholder),
                    fontSize = 14.sp,
                    color = CheGuevaraRed
                )
            }
        }
    }
}

@Composable
fun EnterOtherRoomButton(
    viewModel: ConnectByIdViewModel,
    uiState: State<ConnectByIdUiState>,
    focusManager: FocusManager
) {
    Button(
        onClick = {
            if (uiState.value.isClickable) {
                viewModel.enterOtherRoom()
                focusManager.clearFocus()
            }
        },
        shape = CircleShape
    ) {
        Text(
            stringResource(R.string.connect_by_id_enter_other_room)
        )
    }
}

@Composable
fun EnterUserRoomButton(viewModel: ConnectByIdViewModel, uiState: State<ConnectByIdUiState>) {
    Row(
        Modifier.padding(top = 24.dp)
    ) {
        Button(
            onClick = {
                if (uiState.value.isClickable) {
                    viewModel.enterUserRoom()
                }
            },
            shape = CircleShape
        ) {
            Text(
                stringResource(R.string.connect_by_id_use_your_room)
            )
        }
    }
}