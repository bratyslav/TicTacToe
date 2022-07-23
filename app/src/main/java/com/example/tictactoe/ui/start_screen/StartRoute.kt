package com.example.tictactoe.ui.start_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.R
import com.example.tictactoe.ui.theme.CarroburgCrimson
import com.example.tictactoe.ui.theme.CaveatFamily

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StartRoute(viewModel: StartScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = uiState.isLogoVisible,
            enter = fadeIn(animationSpec = tween(StartScreenViewModel.LOGO_APPEARANCE_TIME.toInt())),
            exit = fadeOut(animationSpec = tween(StartScreenViewModel.LOGO_DISAPPEARANCE_TIME.toInt()))
        ) {
            Row(
                modifier = Modifier.width(224.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.ic_tictactoe),
                        modifier = Modifier.size(224.dp, 224.dp),
                        contentDescription = ""
                    )
                    Text(
                        text = stringResource(R.string.start_screen_header),
                        fontFamily = CaveatFamily,
                        fontSize = 64.sp,
                        color = CarroburgCrimson,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}