package com.example.safepass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.safepass.ui.concurrency.AnrSimulationScreen
import com.example.safepass.ui.theme.SafePassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafePassTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnrSimulationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
