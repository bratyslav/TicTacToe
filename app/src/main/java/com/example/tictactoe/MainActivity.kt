package com.example.tictactoe

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.tictactoe.ui.TictactoeApp
import com.example.tictactoe.ui.theme.TictactoeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val accelerometer = Accelerometer()
        sensorManager.registerListener(accelerometer, sensor, SensorManager.SENSOR_DELAY_GAME)

        val appContainer: AppContainer = AppContainerImpl(applicationContext)

        setContent {
            TictactoeTheme {
                TictactoeApp(appContainer, accelerometer)
            }
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    companion object {
        const val GAME_BOARD_EDGE_SIZE = 3
    }

}