package io.github.chatty

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.time.LocalDateTime

@RestController
@RequestMapping("/chatty")
class Controller {

    private lateinit var sseEmitter: SseEmitter

    @PostMapping("/msg")
    fun sendMessage(@RequestBody message: Message) {

        sseEmitter.send(SseEmitter.event()
            .data(message)
            .id("1")
            .reconnectTime(10_000L))
    }

    @GetMapping("/events", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getChatSse(): SseEmitter {
        sseEmitter = SseEmitter()
//        sseEmitter.onTimeout {
//            sseEmitter = SseEmitter()
//        }
        return sseEmitter
    }

}

data class Message(val text: String, val date: LocalDateTime = LocalDateTime.now())