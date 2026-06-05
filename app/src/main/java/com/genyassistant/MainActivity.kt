package com.genyassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.genyassistant.screen.ConnectRoute
import com.genyassistant.screen.ConnectScreen
import com.genyassistant.screen.SettingsRoute
import com.genyassistant.screen.SettingsScreen
import com.genyassistant.screen.VoiceAssistantRoute
import com.genyassistant.screen.VoiceAssistantScreen
import com.genyassistant.ui.theme.GenyAssistantTheme
import com.genyassistant.viewmodel.VoiceAssistantViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GenyAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = ConnectRoute
                    ) {
                        composable<ConnectRoute> {
                            ConnectScreen(navigateToVoiceAssistant = { voiceAssistantRoute ->
                                runOnUiThread {
                                    navController.navigate(voiceAssistantRoute)
                                }
                            })
                        }

                        composable<VoiceAssistantRoute> { backStackEntry ->
                            val route = backStackEntry.toRoute<VoiceAssistantRoute>()
                            val viewModel = viewModel<VoiceAssistantViewModel>(
                                factory = VoiceAssistantViewModel.Factory(route)
                            )
                            VoiceAssistantScreen(
                                route = route,
                                viewModel = viewModel,
                                onEndCall = {
                                    runOnUiThread { navController.navigateUp() }
                                }
                            )
                        }

                        composable<SettingsRoute> {
                            SettingsScreen(onBack = {
                                runOnUiThread { navController.navigateUp() }
                            })
                        }
                    }
                }
            }
        }
    }
}
