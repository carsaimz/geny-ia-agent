package com.genyassistant.screen

import android.app.Activity
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.media.projection.MediaProjectionManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.genyassistant.MainActivity
import com.genyassistant.ui.AgentVisualization
import com.genyassistant.ui.theme.NeonBlue
import com.genyassistant.viewmodel.VoiceAssistantViewModel
import io.livekit.android.compose.VideoTrackView
import io.livekit.android.compose.chat.rememberChat
import io.livekit.android.compose.local.RoomLocal
import io.livekit.android.compose.local.SessionScope
import io.livekit.android.compose.local.rememberVideoTrack
import io.livekit.android.compose.local.rememberVideoTrackPublication
import io.livekit.android.compose.state.rememberParticipants
import io.livekit.android.compose.state.rememberRoomInfo
import io.livekit.android.compose.state.rememberTracks
import io.livekit.android.room.Room
import io.livekit.android.room.track.VideoTrack
import kotlinx.serialization.Serializable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Serializable
data class VoiceAssistantRoute(
    val url: String,
    val token: String,
)

@Composable
fun VoiceAssistantScreen(
    url: String,
    token: String,
    onEndCall: () -> Unit,
    viewModel: VoiceAssistantViewModel = viewModel(
        factory = VoiceAssistantViewModel.Factory(url, token)
    )
) {
    val session = viewModel.session
    val room = viewModel.room
    val participants = rememberParticipants(room = room)
    val tracks = rememberTracks(room = room)
    val roomInfo = rememberRoomInfo(room = room)
    val chat by rememberChat(room = room)
    val scope = rememberCoroutineScope()

    val canEnableMic = viewModel.canEnableMic

    // Camera and Screenshare
    val cameraTrackPub = rememberVideoTrackPublication(participant = room.localParticipant)
    val cameraTrack = rememberVideoTrack(videoTrackPublication = cameraTrackPub)

    val screenShareTrackPub = rememberVideoTrackPublication(
        participant = room.localParticipant,
        source = Room.TrackSource.SCREEN_SHARE
    )
    val screenShareTrack = rememberVideoTrack(videoTrackPublication = screenShareTrackPub)

    RoomLocal(
        room = room,
        content = {
            AgentVisualization(
                room = viewModel.room
            )
        }
    )

    val context = LocalContext.current
    val retryCount = remember { mutableIntStateOf(0) }
    val maxRetries = 3

    SessionScope(session = session) { session ->

        // Start the session when we have at least microphone permissions.
        // Permission removals kill the app, so this is a one-way transition.
        LaunchedEffect(canEnableMic) {
            if (!canEnableMic) {
                return@LaunchedEffect
            }

            suspend fun startSession() {
                val result = session.start()
                if (result.isFailure && retryCount.intValue < maxRetries) {
                    retryCount.intValue++
                    Toast.makeText(context, "Falha na conexão. Tentando novamente (${retryCount.intValue}/$maxRetries)...", Toast.LENGTH_SHORT).show()
                    startSession()
                } else if (result.isFailure) {
                    Toast.makeText(context, "Erro crítico de conexão. Verifique sua internet.", Toast.LENGTH_LONG).show()
                    onEndCall()
                }
            }
            
            startSession()
        }

        // End the session when leaving the screen.
        DisposableEffect(Unit) {
            onDispose {
                session.end()
            }
        }

        val constraintSet = ConstraintSet {
            val agentView = createRefFor("agentView")
            val controlBar = createRefFor("controlBar")
            val localVideo = createRefFor("localVideo")

            constrain(agentView) {
                top.linkTo(parent.top)
                bottom.linkTo(controlBar.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }

            constrain(controlBar) {
                bottom.linkTo(parent.bottom, margin = 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }

            constrain(localVideo) {
                bottom.linkTo(controlBar.top, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                width = Dimension.value(120.dp)
                height = Dimension.value(180.dp)
            }
        }

        ConstraintLayout(
            constraintSet = constraintSet,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .layoutId("agentView")
                    .fillMaxSize()
            ) {
                AgentVisualization(room = room)
            }

            // Local Video Preview
            if (cameraTrack != null || screenShareTrack != null) {
                Box(
                    modifier = Modifier
                        .layoutId("localVideo")
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, NeonBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    val trackToDisplay = screenShareTrack ?: cameraTrack
                    if (trackToDisplay is VideoTrack) {
                        VideoTrackView(
                            videoTrack = trackToDisplay,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
