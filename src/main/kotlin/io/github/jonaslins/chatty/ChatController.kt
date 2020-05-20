package io.github.jonaslins.chatty

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux


@RestController
@RequestMapping("/chatty")
class ChatController @Autowired constructor(
    private val chatBroker: ChatBroker
) {

    @PostMapping("/{chatRoom}/msg")
    fun sendMessage(@PathVariable chatRoom: String, @RequestBody message: Message) {
        chatBroker.getOrCreateChatRoom(chatRoom).sendMessage(message)
    }

    @GetMapping("/{chatRoom}/subscribe", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getChatSse(@PathVariable chatRoom: String): Flux<ServerSentEvent<out Any>> {
        return chatBroker.getOrCreateChatRoom(chatRoom).getChatSse()
    }
}

