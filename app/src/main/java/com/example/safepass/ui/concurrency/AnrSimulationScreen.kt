package com.example.safepass.ui.concurrency

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AnrSimulationScreen(
    modifier: Modifier = Modifier,
    // Inyección limpia del ViewModel consciente del ciclo de vida de la pantalla
    simulationViewModel: SimulationViewModel = viewModel()
) {
    var textInput by remember { mutableStateOf("") }

    // Suscripción reactiva al flujo de estado inmutable expuesto por la arquitectura
    val currentUiState by simulationViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Laboratorio Concurrencia Avanzada - Apps Fluidas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de entrada para evaluar la fluidez interactiva concurrente
        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Escriba aquí para comprobar que la UI NO SE CONGELA (o \"ERROR\")") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón que emite un evento lógico hacia el ViewModel (separación de responsabilidades)
        Button(
            onClick = {
                simulationViewModel.cargarDatosAsincronosConValidacion(textInput)
            },
            modifier = Modifier.fillMaxWidth(),
            // Deshabilitar la interacción si la corrutina está activa en background
            enabled = currentUiState !is SimulationUiState.Loading
        ) {
            Text("Cargar Datos Pesados (Asíncrono y Seguro)")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Evaluación exhaustiva y segura del estado de la UI
        when (val state = currentUiState) {
            is SimulationUiState.Idle -> {
                Text("Estado: Esperando acción del usuario")
            }
            is SimulationUiState.Loading -> {
                // Al no estar bloqueado el Main Thread, el indicador se anima fluidamente
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Procesando en segundo plano en Dispatchers.IO...")
            }
            is SimulationUiState.Success -> {
                Text(
                    text = "Resultado: ${state.dataMessage}",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is SimulationUiState.Error -> {
                Text(
                    text = "Error: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
