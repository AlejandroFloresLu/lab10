package com.example.safepass.ui.concurrency

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Paso 3 (versión bloqueante, solo para la captura del ANR en Logcat).
 * Para usarla, en MainActivity reemplace AnrSimulationScreen(...) por AnrBloqueanteScreen(...).
 */
@Composable
fun AnrBloqueanteScreen(modifier: Modifier = Modifier) {
    var textInput by remember { mutableStateOf("") }
    var resultState by remember { mutableStateOf<SimulationUiState>(SimulationUiState.Idle) }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Laboratorio Concurrencia - Simulación ANR",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Escriba aquí para probar fluidez") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                Log.d("ANR_LAB", "Inicio de operación pesada en Hilo Principal: " + Thread.currentThread().name)
                resultState = SimulationUiState.Loading
                // LLAMADA BLOQUEANTE CRÍTICA: monopoliza el hilo actual
                val resultData = ejecutarCalculoMasivoBloqueante()
                resultState = SimulationUiState.Success(resultData)
                Log.d("ANR_LAB", "Fin de operación en Hilo Principal")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cargar Datos Pesados (Bloqueante)")
        }
        Spacer(modifier = Modifier.height(24.dp))
        when (val state = resultState) {
            is SimulationUiState.Idle -> Text("Estado: Listo para iniciar")
            is SimulationUiState.Loading -> CircularProgressIndicator()
            is SimulationUiState.Success -> Text("Resultado: ${state.dataMessage}")
            is SimulationUiState.Error -> Text("Error: ${state.errorMessage}")
        }
    }
}

/** Simula procesamiento intensivo: consume el hilo que la invoque. */
fun ejecutarCalculoMasivoBloqueante(): String {
    val tiempoInicio = System.currentTimeMillis()
    Log.d("ANR_LAB", "Procesando bucle de alta densidad en el Main Thread...")
    try {
        Thread.sleep(5000)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
    val tiempoTotal = System.currentTimeMillis() - tiempoInicio
    return "Procesamiento completado con éxito en $tiempoTotal ms"
}
