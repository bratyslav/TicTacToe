package com.example.tictactoe.data.game

interface GameUserInterface {

    fun receiveUpdate(arrayList: ArrayList<Byte>)

    fun onWin()

    fun onLose()

}