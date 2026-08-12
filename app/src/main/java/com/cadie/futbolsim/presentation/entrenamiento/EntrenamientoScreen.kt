package com.cadie.futbolsim.presentation.entrenamiento

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
fun EntrenamientoScreen(
    viewModel: EntrenamientoViewModel = hiltViewModel(),
    onComenzarClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is EntrenamientoUiState.Ejecutando -> {
                EntrenamientoEnProgreso()
            }
            is EntrenamientoUiState.Completado -> {
                EntrenamientoCompletado(state.resumen, onComenzarClick)
            }
            is EntrenamientoUiState.Error -> {
                Text("Error durante el entrenamiento", color = Color.Red)
            }
        }
    }
}

@Composable
fun EntrenamientoEnProgreso() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ball_rotation"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "⚽",
            fontSize = 80.sp,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .rotate(rotation)
        )
        
        Text(
            text = "Entrenando modelo...",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "El sistema está aprendiendo de los datos históricos.\nPor favor, no cierres la aplicación.",
            fontSize = 16.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        LinearProgressIndicator(
            color = Color(0xFF00FF7F),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Nota: Este proceso puede tardar varios minutos",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EntrenamientoCompletado(resumen: List<ResumenJornada>, onComenzarClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "✅ Entrenamiento completado",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00FF7F),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "El modelo ha aprendido de la temporada 2025/2026",
            fontSize = 16.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Gráfica Vico
        val entries = resumen.map { FloatEntry(it.jornada.toFloat(), it.precision.toFloat()) }
        val chartEntryModel = entryModelOf(entries)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Chart(
                chart = lineChart(),
                model = chartEntryModel,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¡El sistema está listo para hacer predicciones!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onComenzarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
        ) {
            Text("COMENZAR", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
