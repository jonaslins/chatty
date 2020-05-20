package io.github.jonaslins.chatty

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatBroker {

    private val chatRooms: ConcurrentHashMap<String, ChatTopic> = ConcurrentHashMap<String, ChatTopic>()

    fun getOrCreateChatRoom(name: String): ChatTopic {
        return chatRooms[name] ?: run {
            ChatTopic(name).also { chatRooms[name] = it }
        }
    }

    fun removeChatRoom(name: String) {
        chatRooms.remove(name)
    }

}