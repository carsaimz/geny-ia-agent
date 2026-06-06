package com.genyassistant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.livekit.android.LiveKit
import com.genyassistant.screen.VoiceAssistantRoute
import io.livekit.android.token.TokenSource
import io.livekit.android.token.cached

/**
 * This ViewModel handles holding onto the Room object, so that it is
 * maintained across configuration changes, such as rotation.
 */
class VoiceAssistantViewModel(
    application: Application, 
    private val route: VoiceAssistantRoute
) : AndroidViewModel(application) {

    val room = LiveKit.create(application)

    val tokenSource: TokenSource = if (route.sandboxId.isNotEmpty()) {
        TokenSource.fromSandboxTokenServer(sandboxId = route.sandboxId).cached()
    } else {
        TokenSource.fromLiteral(route.url, route.token).cached()
    }

    override fun onCleared() {
        super.onCleared()
        room.disconnect()
        room.release()
    }

    class Factory(private val route: VoiceAssistantRoute) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
            val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
            return VoiceAssistantViewModel(application, route) as T
        }
    }
}
