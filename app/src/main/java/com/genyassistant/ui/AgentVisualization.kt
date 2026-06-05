package com.genyassistant.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.livekit.android.annotations.Beta
import io.livekit.android.compose.state.Agent
import io.livekit.android.compose.state.AgentState
import io.livekit.android.compose.ui.ScaleType
import io.livekit.android.compose.ui.VideoTrackView
import io.livekit.android.compose.ui.audio.VoiceAssistantBarVisualizer
import com.genyassistant.ui.anim.CircleReveal
import com.genyassistant.ui.theme.NeonBlue
import kotlin.math.max
import kotlin.math.roundToInt

val revealSpringSpec = spring<Float>(stiffness = Spring.StiffnessVeryLow)
val hideSpringSpec = spring<Float>(stiffness = Spring.StiffnessMedium)

@OptIn(Beta::class)
@Composable
fun AgentVisualization(
    agent: Agent,
    modifier: Modifier = Modifier
) {

    val videoTrack = agent.videoTrack

    var hasFirstFrameRendered by remember(videoTrack) { mutableStateOf(false) }
    val revealed = videoTrack != null && hasFirstFrameRendered

    Box(modifier = modifier) {
        if (videoTrack != null) {
            VideoTrackView(
                trackReference = videoTrack,
                scaleType = ScaleType.FitInside,
                onFirstFrameRendered = { hasFirstFrameRendered = true },
                modifier = Modifier
                    .fillMaxSize(),
            )
        }
        CircleReveal(
            revealed = revealed,
            content = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val density = LocalDensity.current
                    var width by remember { mutableIntStateOf(0) }
                    var height by remember { mutableIntStateOf(0) }

                    val barWidth by remember {
                        derivedStateOf {
                            val widthDp = width / density.density
                            return@derivedStateOf max(1, (widthDp * 0.157f).roundToInt()).dp
                        }
                    }
                    val barMinHeightPercent by remember {
                        derivedStateOf {
                            val widthPx = barWidth.value * density.density
                            return@derivedStateOf max(0f, (widthPx / height))
                        }
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        VoiceAssistantBarVisualizer(
                            agentState = agent.agentState,
                            audioTrackRef = agent.audioTrack,
                            barCount = 5,
                            minHeight = barMinHeightPercent,
                            barWidth = barWidth,
                            brush = SolidColor(NeonBlue),
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .fillMaxHeight(0.22f)
                                .onSizeChanged { size ->
                                    width = size.width
                                    height = size.height
                                }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        val statusText = when (agent.agentState) {
                            AgentState.LISTENING -> "Ouvindo..."
                            AgentState.THINKING -> "Geny está pensando..."
                            AgentState.SPEAKING -> "Geny está falando"
                            else -> "Conectado"
                        }
                        
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = statusText,
                                color = NeonBlue.copy(alpha = 0.8f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            animationSpec = if (revealed) revealSpringSpec else hideSpringSpec,
        )
    }
}
