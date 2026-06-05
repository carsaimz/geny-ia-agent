package com.genyassistant

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ChatMessage(
    val sender: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatHistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chat_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveMessage(sender: String, message: String) {
        val history = getHistory().toMutableList()
        history.add(ChatMessage(sender, message))
        // Limitar a 100 mensagens para não sobrecarregar
        if (history.size > 100) history.removeAt(0)
        
        prefs.edit().putString("history_json", gson.toJson(history)).apply()
    }

    fun getHistory(): List<ChatMessage> {
        val json = prefs.getString("history_json", null) ?: return emptyList()
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearHistory() {
        prefs.edit().remove("history_json").apply()
    }
}
