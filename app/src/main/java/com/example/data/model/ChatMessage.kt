package com.example.data.model

import android.graphics.Bitmap

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val image: Bitmap? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ChatSender {
    USER,
    AI
}
