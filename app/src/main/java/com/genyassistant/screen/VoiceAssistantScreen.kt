package com.genyassistant.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
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
import io.livekit.android.compose.local.RoomScope
import io.livekit.android.compose.state.rememberAgent
import io.livekit.android.compose.state.rememberParticipants
import io.livekit.android.compose.state.rememberRoomInfo
import io.livekit.android.compose.state.rememberTracks
import io.livekit.android.compose.ui.VideoTrackView
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import com.genyassistant.ui.ControlBar
import com.genyassistant.ui.ChatLog
import com.genyassistant.ui.ChatBar
import kotlinx.serialization.Serializable
import io.livekit.android.compose.state.rememberLocalParticipant
import io.livekit.android.compose.local.rememberVideoTrack
import io.livekit.android.compose.local.rememberVideoTrackPublication
import kotlinx.coroutines.launch

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

    RoomScope(
        url = route.url,
        token = route.token,
        connect = true,
        audio = true,
        room = room
    ) {
        val participants = rememberParticipants()
        val tracks = rememberTracks()
        val roomInfo = rememberRoomInfo()
        val chatState = rememberChat()
        val chat by chatState.messages.collectAsState(initial = emptyList())
        val agent = rememberAgent()

        val canEnableMic by rememberCanEnableMic()
        val localParticipant = rememberLocalParticipant()
        val coroutineScope = rememberCoroutineScope()

        var isChatOpen by remember { mutableStateOf(false) }

        // Camera and Screenshare
        val cameraTrackPub = rememberVideoTrackPublication(participant = localParticipant)
        val cameraTrack = rememberVideoTrack(videoTrackPublication = cameraTrackPub)

        val screenShareTrackPub = rememberVideoTrackPublication(
            participant = localParticipant,
            source = Track.Source.SCREEN_SHARE
        )
        val screenShareTrack = rememberVideoTrack(videoTrackPublication = screenShareTrackPub)

        val context = LocalContext.current

        // Start the session when we have at least microphone permissions.
        LaunchedEffect(canEnableMic) {
            if (!canEnableMic) {
                return@LaunchedEffect
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

        var chatMessage by remember { mutableStateOf("") }

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

                if (isChatOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f))
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            ChatLog(
                                room = room,
                                messages = chat,
                                modifier = Modifier.weight(1f)
                            )
                            ChatBar(
                                value = chatMessage,
                                onValueChange = { chatMessage = it },
                                onChatSend = {
                                    coroutineScope.launch {
                                        chatState.send(it)
                                    }
                                    chatMessage = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
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
                            trackReference = trackToDisplay,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            ControlBar(
                isMicEnabled = localParticipant.isMicrophoneEnabled(),
                onMicClick = { localParticipant.setMicrophoneEnabled(!localParticipant.isMicrophoneEnabled()) },
                localAudioTrack = localParticipant.audioTrack,
                isCameraEnabled = localParticipant.isCameraEnabled(),
                onCameraClick = { localParticipant.setCameraEnabled(!localParticipant.isCameraEnabled()) },
                isScreenShareEnabled = localParticipant.isScreenShareEnabled(),
                onScreenShareClick = { localParticipant.setScreenShareEnabled(!localParticipant.isScreenShareEnabled()) },
                isChatEnabled = isChatOpen,
                onChatClick = { isChatOpen = !isChatOpen },
                onExitClick = onEndCall,
                modifier = Modifier
                    .layoutId("controlBar")
                    .height(56.dp)
            )
        }
    }
}
