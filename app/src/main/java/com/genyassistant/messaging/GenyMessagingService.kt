package com.genyassistant.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class GenyMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("Novo token Firebase: $token")
        // Aqui você pode enviar o token para o seu servidor se necessário
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("Mensagem recebida de: ${remoteMessage.from}")
        
        remoteMessage.notification?.let {
            Timber.d("Corpo da notificação: ${it.body}")
        }
    }
}
