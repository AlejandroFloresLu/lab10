package com.example.safepass.ui.concurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SimulationViewModel : ViewModel() {

    // Estado mutable interno (fuente única de verdad)
    private val _uiState = MutableStateFlow<SimulationUiState>(SimulationUiState.Idle)

    // Estado inmutable expuesto de forma segura hacia la vista (Compose)
    val uiState: StateFlow<SimulationUiState> = _uiState.asStateFlow()

    /**
     * Inicia el proceso asíncronamente atado al ciclo de vida del ViewModel.
     * Utiliza la concurrencia estructurada para evitar la proliferación de procesos zombies.
     */
    fun cargarDatosAsincronos() {
        viewModelScope.launch {
            Log.d("ANR_LAB", "Inicio Corrutina en hilo: " + Thread.currentThread().name)
            _uiState.value = SimulationUiState.Loading

            try {
                // Desplaza la carga pesada del Main al pool optimizado para E/S
                val resultado = withContext(Dispatchers.IO) {
                    Log.d(
                        "ANR_LAB",
                        "Procesando cálculo intensivo en segundo plano de forma no bloqueante sobre hilo: " +
                            Thread.currentThread().name
                    )

                    // Simulación de retraso por lectura de archivos o red sin congelar el Main Thread
                    Thread.sleep(5000)

                    "Datos descargados con éxito desde segundo plano de forma fluida."
                }

                // Al retornar de withContext, la corrutina retoma Dispatchers.Main
                Log.d("ANR_LAB", "Retorno seguro a Dispatchers.Main sobre hilo: " + Thread.currentThread().name)
                _uiState.value = SimulationUiState.Success(resultado)

            } catch (e: Exception) {
                _uiState.value = SimulationUiState.Error("Fallo asíncrono controlado: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Desafío: valida el texto del usuario; si es "ERROR" aborta y emite un estado de error.
     */
    fun cargarDatosAsincronosConValidacion(textoUsuario: String) {
        viewModelScope.launch {
            Log.d("ANR_LAB", "Inicio Corrutina (con validación) en hilo: " + Thread.currentThread().name)
            _uiState.value = SimulationUiState.Loading
            try {
                // Evaluación proactiva del criterio de error
                if (textoUsuario.trim().uppercase() == "ERROR") {
                    // Lanzar excepción para activar el bloque catch de protección asíncrona
                    throw IllegalArgumentException("Entrada restringida detectada por simulación de seguridad.")
                }

                val resultado = withContext(Dispatchers.IO) {
                    Log.d(
                        "ANR_LAB",
                        "Procesando cálculo intensivo en segundo plano de forma no bloqueante sobre hilo: " +
                            Thread.currentThread().name
                    )
                    Thread.sleep(3000)
                    "Procesamiento asíncrono limpio completado."
                }
                Log.d("ANR_LAB", "Retorno seguro a Dispatchers.Main sobre hilo: " + Thread.currentThread().name)
                _uiState.value = SimulationUiState.Success(resultado)
            } catch (e: Exception) {
                // El estado de error captura la excepción previniendo el crash de la app
                _uiState.value = SimulationUiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }
}
