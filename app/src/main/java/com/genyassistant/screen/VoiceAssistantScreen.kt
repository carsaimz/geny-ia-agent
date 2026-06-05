package com.genyassistant.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.lifecycle.viewmodel.compose.viewModel
import com.genyassistant.rememberCanEnableMic
import com.genyassistant.ui.AgentVisualization
import com.genyassistant.ui.theme.NeonBlue
import com.genyassistant.viewmodel.VoiceAssistantViewModel
import io.livekit.android.annotations.Beta
import io.livekit.android.compose.chat.rememberChat
import io.livekit.android.compose.local.RoomLocal
import io.livekit.android.compose.state.rememberParticipants
import io.livekit.android.compose.state.rememberRoomInfo
import io.livekit.android.compose.state.rememberTracks
import io.livekit.android.compose.state.rememberAgent
import io.livekit.android.compose.ui.VideoTrackView
import io.livekit.android.room.track.VideoTrack
import io.livekit.android.room.track.Track
import io.livekit.android.compose.local.rememberVideoTrack
import io.livekit.android.compose.local.rememberVideoTrackPublication
import io.livekit.android.compose.types.TrackReference
import kotlinx.serialization.Serializable

@Serializable
data class VoiceAssistantRoute(
    val sandboxId: String = "",
    val url: String = "",
    val token: String = "",
)

@OptIn(Beta::class)
@Composable
fun VoiceAssistantScreen(
    route: VoiceAssistantRoute,
    onEndCall: () -> Unit,
    viewModel: VoiceAssistantViewModel = viewModel(
        factory = VoiceAssistantViewModel.Factory(route)
    )
) {
    val room = viewModel.room
    val participants = rememberParticipants()
    val tracks = rememberTracks()
    val roomInfo = rememberRoomInfo()
    val chat by rememberChat()
    val agent = rememberAgent()

    val canEnableMic by rememberCanEnableMic()

    // Camera and Screenshare
    val cameraTrackPub = rememberVideoTrackPublication(participant = room.localParticipant)
    val cameraTrack = rememberVideoTrack(videoTrackPublication = cameraTrackPub)

    val screenShareTrackPub = rememberVideoTrackPublication(
        participant = room.localParticipant,
        source = Track.Source.SCREEN_SHARE
    )
    val screenShareTrack = rememberVideoTrack(videoTrackPublication = screenShareTrackPub)

    val context = LocalContext.current
    val retryCount = remember { mutableIntStateOf(0) }
    val maxRetries = 3

    RoomLocal(room = room) {
        // Start the session when we have at least microphone permissions.
        LaunchedEffect(canEnableMic) {
            if (!canEnableMic) {
                return@LaunchedEffect
            }
            // A conexão é gerenciada pelo ViewModel ou externamente
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
                if (agent != null) {
                    AgentVisualization(agent = agent)
                }
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
                            trackReference = TrackReference(
                                participant = room.localParticipant,
                                publication = if (screenShareTrack != null) screenShareTrackPub else cameraTrackPub,
                                track = trackToDisplay
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
