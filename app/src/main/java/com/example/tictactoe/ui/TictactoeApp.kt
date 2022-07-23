package com.example.tictactoe.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.tictactoe.AppContainer
import com.example.tictactoe.Accelerometer

@Composable
fun TictactoeApp(appContainer: AppContainer, accelerometer: Accelerometer) {

    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()

    Scaffold(Modifier.fillMaxSize(), scaffoldState) {
        TictactoeNavGraph(appContainer, navController, scaffoldState, accelerometer)
    }

}