package io.github.jonaslins.chatty

import java.time.LocalDateTime

data class Message(
    val author: String,
    val text: String,
    val date: LocalDateTime = LocalDateTime.now()
)