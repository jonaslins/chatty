package io.github.jonaslins.chatty

import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import java.time.Duration
import java.time.LocalDateTime

class ChatTopic {

    private val name: String

    private val processor: FluxProcessor<Message, Message>

    private val garbageCollector: Flux<Long> =
        Flux.interval(Duration.ofSeconds(10))

    constructor(name: String) {
        this.name = name
        this.processor = DirectProcessor.create<Message>()
            .serialize()
        initGarbageCollector()
    }

    private fun initGarbageCollector() {
        garbageCollector.subscribe {
            processor.onNext(
                Message(
                    "",
                    "",
                    LocalDateTime.now()
                )
            )
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
                ServerSentEvent.builder<Any>()
                    .event("heartbeat").retry(Duration.ofSeconds(10)).build()
            }
        }
    }

}