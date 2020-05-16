package io.github.chatty

import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.FluxSink
import java.time.LocalDateTime


@RestController
@RequestMapping("/chatty")
class Controller {

    private val processor: FluxProcessor<Message, Message> = DirectProcessor.create<Message>().serialize()
    private val sink: FluxSink<Message> = processor.sink()

    @PostMapping("/msg")
    fun sendMessage(@RequestBody message: Message) {
        if(message.text != "q")
        sink.next(message)
    }

    @GetMapping("/events", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getChatSse(): Flux<ServerSentEvent<Any>> {
        return processor.map { e: Any ->
            ServerSentEvent.builder(e).build()
        }
    }
}

data class Message(val author: String, val text: String, val date: LocalDateTime = LocalDateTime.now())