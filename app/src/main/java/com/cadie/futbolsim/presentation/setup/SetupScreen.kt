package com.cadie.futbolsim.presentation.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    viewModel: SetupViewModel = hiltViewModel(),
    onSetupComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var supabaseUrl by remember { mutableStateOf("") }
    var supabaseKey by remember { mutableStateOf("") }
    var backendUrl by remember { mutableStateOf("") }
    var apiFootballKey by remember { mutableStateOf("") }
    var footballDataKey by remember { mutableStateOf("") }
    var theOddsKey by remember { mutableStateOf("") }
    var openweatherKey by remember { mutableStateOf("") }
    var cloudinaryUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarDesdeSupabase()
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is SetupUiState.Saved -> onSetupComplete()
            is SetupUiState.Error -> {
                scope.launch { snackbarHostState.showSnackbar(state.msg) }
            }
            is SetupUiState.PartialValid -> {
                scope.launch { snackbarHostState.showSnackbar("Configuración parcial guardada. Faltan APIs opcionales.") }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Configuración Inicial",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Introduce las claves API para activar todas las funciones",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (uiState is SetupUiState.Verifying) {
                    item {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF00FF7F)
                        )
                    }
                }

                // 🔑 Supabase
                item {
                    SectionTitle("🔑 Supabase (OBLIGATORIO)")
                    SecretField("Supabase URL", supabaseUrl) { supabaseUrl = it }
                    SecretField("Supabase Key", supabaseKey) { supabaseKey = it }
                }

                // 🌐 Backend
                item {
                    SectionTitle("🌐 Backend URL (OBLIGATORIO)")
                    SecretField("URL de Render", backendUrl) { backendUrl = it }
                }

                // 📊 APIs Datos
                item {
                    SectionTitle("📊 APIs de Datos (Opcionales)")
                    SecretField("API Football Key", apiFootballKey) { apiFootballKey = it }
                    SecretField("Football-Data.org Key", footballDataKey) { footballDataKey = it }
                }

                // 🎲 Odds / Clima
                item {
                    SectionTitle("🎲 APIs de Odds y Clima (Opcionales)")
                    SecretField("The-Odds API Key", theOddsKey) { theOddsKey = it }
                    SecretField("OpenWeather Key", openweatherKey) { openweatherKey = it }
                }

                // 🖼️ Imágenes
                item {
                    SectionTitle("🖼️ Imágenes (Opcional)")
                    SecretField("Cloudinary URL", cloudinaryUrl) { cloudinaryUrl = it }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.verificarYGuardar(
                                mapOf(
                                    "supabaseUrl" to supabaseUrl,
                                    "supabaseKey" to supabaseKey,
                                    "backendUrl" to backendUrl,
                                    "apiFootballKey" to apiFootballKey,
                                    "footballDataKey" to footballDataKey,
                                    "theOddsKey" to theOddsKey,
                                    "openweatherKey" to openweatherKey,
                                    "cloudinaryUrl" to cloudinaryUrl
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(bottom = 32.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                        enabled = uiState !is SetupUiState.Verifying
                    ) {
                        Text("VERIFICAR Y GUARDAR", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF00FF7F),
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretField(label: String, value: String, onValueChange: (String) -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF00FF7F),
            unfocusedBorderColor = Color.DarkGray,
            textColor = Color.White
        ),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp)
    )
}
