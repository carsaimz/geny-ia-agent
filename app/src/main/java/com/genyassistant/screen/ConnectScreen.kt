package com.genyassistant.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genyassistant.R
import com.genyassistant.hardcodedToken
import com.genyassistant.hardcodedUrl
import com.genyassistant.sandboxID
import com.genyassistant.ui.theme.NeonBlue
import kotlinx.serialization.Serializable

@Serializable
object ConnectRoute

@Composable
fun ConnectScreen(
    navigateToVoiceAssistant: (VoiceAssistantRoute) -> Unit
) {
    var isConnecting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.connect_icon),
            contentDescription = "Geny Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.size(32.dp))

        Text(
            text = "Geny AI Agent",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = NeonBlue
        )

        Spacer(Modifier.size(16.dp))

        Text(
            text = "Seu assistente inteligente de voz e vídeo em tempo real.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.size(48.dp))

        Button(
            onClick = {
                isConnecting = true
                val route = VoiceAssistantRoute(
                    sandboxId = sandboxID,
                    url = hardcodedUrl,
                    token = hardcodedToken
                )
                navigateToVoiceAssistant(route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .size(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonBlue,
                contentColor = Color.Black
            )
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "CONECTAR AGORA",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
