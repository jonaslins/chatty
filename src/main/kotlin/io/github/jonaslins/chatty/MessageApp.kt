package io.github.jonaslins.chatty

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap


@RestController
@RequestMapping("/chatty")
class Controller @Autowired constructor(
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

class ChatTopic {

    val name: String

    private val processor: FluxProcessor<Message, Message>

    private val garbageCollector: Flux<Long> = Flux.interval(Duration.ofSeconds(10))

    constructor(name: String) {
        this.name = name
        this.processor = DirectProcessor.create<Message>().serialize()
        initGarbageCollector()
    }

    private fun initGarbageCollector() {
        garbageCollector.subscribe {
            processor.onNext(Message("", "", LocalDateTime.now()))
        }
    }

    fun sendMessage(message: Message) {
        processor.onNext(message)
    }

    fun getChatSse(): Flux<ServerSentEvent<out Any>> {
        return processor.map { e: Any ->
            if (e is Message && e.author.isNotBlank()) {
                ServerSentEvent.builder(e).build()
            } else {
                ServerSentEvent.builder<Any>().event("heartbeat").retry(Duration.ofSeconds(10)).build()
            }
        }
    }

}

data class Message(
    val author: String,
    val text: String,
    val date: LocalDateTime = LocalDateTime.now()
)