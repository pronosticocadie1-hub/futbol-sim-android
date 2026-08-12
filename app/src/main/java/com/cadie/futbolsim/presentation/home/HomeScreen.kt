package com.cadie.futbolsim.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

data class Partido(
    val id: String,
    val local: String,
    val visitante: String,
    val escudoLocal: String,
    val escudoVisitante: String,
    val fechaHora: String,
    val prediccion: String,
    val confianza: Int
)

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val partidos: List<Partido> = emptyList(),
    val error: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSimulacion: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLiga by remember { mutableStateOf("Todas las ligas") }
    val ligas = listOf("Todas las ligas", "LaLiga", "Segunda División", "Premier League", "Ligue 1")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚽ Simulador de Fútbol", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onRefresh,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Actualizar")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
            }
            
            Box(modifier = Modifier.padding(16.dp)) {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedLiga, color = MaterialTheme.colorScheme.onBackground)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    ligas.forEach { liga ->
                        DropdownMenuItem(
                            text = { Text(liga) },
                            onClick = {
                                selectedLiga = liga
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (uiState.isOffline) {
                Surface(color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Sin conexión · Mostrando datos locales",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onError,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // Shimmer effect placeholder
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.error != null) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text("Reintentar")
                    }
                }
            } else {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = uiState.isRefreshing),
                    onRefresh = onRefresh
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.partidos) { partido ->
                            PartidoCard(partido = partido, onClick = { onNavigateToSimulacion(partido.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PartidoCard(partido: Partido, onClick: () -> Unit) {
    val confianzaColor = when {
        partido.confianza > 70 -> Color(0xFF00C853)
        partido.confianza in 50..70 -> Color(0xFFFFD600)
        else -> Color(0xFFD50000)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = partido.escudoLocal,
                    contentDescription = partido.local,
                    modifier = Modifier.size(48.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = partido.local, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(text = partido.fechaHora, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = partido.visitante, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }
                AsyncImage(
                    model = partido.escudoVisitante,
                    contentDescription = partido.visitante,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔮 Predicción: ${partido.prediccion}",
                    color = Color(0xFF00C853),
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = confianzaColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${partido.confianza}%",
                        color = confianzaColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
