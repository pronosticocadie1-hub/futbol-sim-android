package com.cadie.futbolsim

import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cadie.futbolsim.presentation.home.HomeScreen
import com.cadie.futbolsim.presentation.home.HomeUiState
import com.cadie.futbolsim.presentation.settings.SettingsScreen
import com.cadie.futbolsim.presentation.settings.SettingsViewModel
import com.cadie.futbolsim.presentation.simulacion.SimulacionScreen
import com.cadie.futbolsim.presentation.simulacion.SimulacionViewModel
import com.cadie.futbolsim.presentation.theme.FutbolSimTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = 0 // Dark mode system UI

        setContent {
            FutbolSimTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("login") {
            // Placeholder for LoginScreen
            Text("Login Screen")
        }
        composable("setup") {
            // Placeholder for SetupScreen
            Text("Setup Screen")
        }
        composable("entrenamiento") {
            // Placeholder for EntrenamientoScreen
            Text("Entrenamiento Screen")
        }
        composable("home") {
            HomeScreen(
                uiState = HomeUiState(),
                onRefresh = {},
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToSimulacion = { partidoId -> navController.navigate("simulacion/$partidoId") }
            )
        }
        composable(
            route = "simulacion/{partidoId}",
            arguments = listOf(navArgument("partidoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val partidoId = backStackEntry.arguments?.getString("partidoId") ?: return@composable
            val viewModel: SimulacionViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsState().value
            
            SimulacionScreen(
                partidoId = partidoId,
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onConfirmRecalculo = { viewModel.confirmarRecalculo(it) },
                onRejectRecalculo = { viewModel.rechazarRecalculo(it) },
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("settings") {
            val viewModel: SettingsViewModel = hiltViewModel()
            val uiState = viewModel.uiState.collectAsState().value
            val claves = viewModel.claves.collectAsState().value
            
            SettingsScreen(
                uiState = uiState,
                claves = claves,
                onBack = { navController.popBackStack() },
                onSave = { viewModel.guardarClaves(it) },
                onReset = { 
                    viewModel.resetApp()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
