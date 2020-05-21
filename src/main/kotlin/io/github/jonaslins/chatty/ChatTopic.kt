package io.github.jonaslins.chatty

import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import java.time.Duration

class ChatTopic {

    private val name: String
    private val processor: FluxProcessor<Message, Message>
    private val keepAliveDelay: Long = 10

    constructor(name: String) {
        this.name = name
        this.processor = DirectProcessor.create<Message>().serialize()
    }

    fun sendMessage(message: Message) {
        processor.onNext(message)
    }

    fun getChatSse(): Flux<ServerSentEvent<out Any>> {
        return Flux.merge(
            processor.map { ServerSentEvent.builder(it).build() },
            buildKeepAliveFlux()
        )
    }

    private fun buildKeepAliveFlux(): Flux<ServerSentEvent<Any>> {
        return Flux.interval(Duration.ofSeconds(keepAliveDelay))
            .map {
                ServerSentEvent
                    .builder<Any>()
                    .comment("keep-alive")
                    .build()
            }
    }

}