package com.genyassistant.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genyassistant.ChatHistoryManager
import com.genyassistant.ui.theme.NeonBlue
import kotlinx.serialization.Serializable

@Serializable
object SettingsRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val chatHistoryManager = remember { ChatHistoryManager(context) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações", color = NeonBlue) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = NeonBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Privacidade e Dados",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeonBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Histórico de Chat", fontWeight = FontWeight.Medium)
                        Text("Limpar todas as conversas salvas", fontSize = 12.sp, color = Color.Gray)
                    }
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Limpar", tint = Color.Red)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Sobre o Geny",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeonBlue,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Geny Assistant v1.0\nPowered by LiveKit & Firebase\n\nEste assistente foi personalizado para oferecer a melhor experiência de voz com inteligência artificial.",
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Limpar Histórico") },
                text = { Text("Tem certeza que deseja apagar todo o histórico de conversas?") },
                confirmButton = {
                    TextButton(onClick = {
                        chatHistoryManager.clearHistory()
                        showDialog = false
                    }) {
                        Text("Sim", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
