package com.cadie.futbolsim.presentation.simulacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cadie.futbolsim.presentation.home.Partido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulacionScreen(
    partidoId: String,
    uiState: SimulacionUiState,
    onBack: () -> Unit,
    onConfirmRecalculo: (String) -> Unit,
    onRejectRecalculo: (Partido) -> Unit,
    onNavigateHome: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = if (uiState is SimulacionUiState.Success) "${uiState.partido.local} vs ${uiState.partido.visitante}" else "Simulación",
                    color = MaterialTheme.colorScheme.onBackground
                ) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                is SimulacionUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(64.dp), color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Generando simulación...", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
                is SimulacionUiState.NeedConfirmation -> {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("¿Reemplazar simulación?") },
                        text = { Text("Hay nuevos datos disponibles. ¿Quieres recalcular la simulación?") },
                        confirmButton = {
                            Button(onClick = { onConfirmRecalculo(uiState.partido.id) }) { Text("Reemplazar") }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { onRejectRecalculo(uiState.partido) }) { Text("Mantener") }
                        }
                    )
                }
                is SimulacionUiState.Success -> {
                    val partido = uiState.partido
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(model = partido.escudoLocal, contentDescription = null, modifier = Modifier.size(64.dp))
                                    Text(text = partido.prediccion, fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                                    AsyncImage(model = partido.escudoVisitante, contentDescription = null, modifier = Modifier.size(64.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                val confianzaColor = when {
                                    partido.confianza > 70 -> Color(0xFF00C853)
                                    partido.confianza in 50..70 -> Color(0xFFFFD600)
                                    else -> Color(0xFFD50000)
                                }
                                Surface(shape = RoundedCornerShape(16.dp), color = confianzaColor.copy(alpha = 0.2f)) {
                                    Text(
                                        text = "${partido.confianza}% Confianza",
                                        color = confianzaColor,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = partido.fechaHora, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Banner Baja confianza
                        if (partido.confianza < 50) {
                            Surface(color = Color(0xFFF57F17), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "⚠️ Simulación con baja confianza por falta de datos",
                                    modifier = Modifier.padding(12.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Análisis", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Basado en los datos históricos y recientes, el equipo local muestra un fuerte dominio en el centro del campo, lo que podría resultar en más oportunidades de gol.",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Grid stats
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard("⚽ Goles", "2 - 1", Modifier.weight(1f))
                            StatCard("🎯 Remates", "12 - 8", Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard("🎯 A puerta", "5 - 3", Modifier.weight(1f))
                            StatCard("🟨 Tarjetas", "2 (0 🟥) - 3 (1 🟥)", Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatCard("🏴 Córners", "6 - 4", Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onNavigateHome,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("VOLVER AL INICIO", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is SimulacionUiState.Error -> {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Error: ${uiState.msg}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
