package com.genyassistant

import android.os.Bundle
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.livekit.android.LiveKit
import com.genyassistant.screen.ConnectRoute
import com.genyassistant.screen.ConnectScreen
import com.genyassistant.screen.VoiceAssistantRoute
import com.genyassistant.screen.VoiceAssistantScreen
import com.genyassistant.screen.SettingsRoute
import com.genyassistant.screen.SettingsScreen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import com.genyassistant.ui.theme.GenyAssistantTheme
import com.genyassistant.ui.theme.NeonBlue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.genyassistant.viewmodel.VoiceAssistantViewModel
import io.livekit.android.util.LoggingLevel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LiveKit.loggingLevel = LoggingLevel.DEBUG

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val updateChecker = UpdateChecker(context)
                updateChecker.checkForUpdates(BuildConfig.VERSION_NAME) { release ->
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "Nova versão disponível: ${release.tagName}", Toast.LENGTH_LONG).show()
                        updateChecker.promptUpdate(release)
                    }
                }
            }
            GenyAssistantTheme(dynamicColor = false) {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        // Botão de Configurações no canto superior direito
                        IconButton(
                            onClick = { navController.navigate(SettingsRoute) },
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = NeonBlue)
                        }

                        // Set up NavHost for the app
                        NavHost(navController, startDestination = ConnectRoute) {
                            composable<ConnectRoute> {
                                ConnectScreen(navigateToVoiceAssistant = { voiceAssistantRoute ->
                                    runOnUiThread {
                                        navController.navigate(voiceAssistantRoute)
                                    }
                                })
                            }

                            composable<VoiceAssistantRoute> {
                                val viewModel = viewModel<VoiceAssistantViewModel>()
                                VoiceAssistantScreen(
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
}
