package com.dealer.streamerrordemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@SpringBootApplication
@RestController
class StreamErrorDemoApplication {
	@GetMapping("/error")
	fun error(response: ServerHttpResponse): Mono<Void> {
		val factory = response.bufferFactory()
		val data = "This is a response!"
		val body = Flux.fromIterable(data.chunked(2))
			.delayElements(Duration.ofMillis(300))
			.map { string ->
				if(string.contains("o")) {
					throw IllegalStateException("Erroring")
				}
				factory.wrap(string.toByteArray())
			}

		response.statusCode = HttpStatus.OK
		response.headers.set("Content-Type", "text/plain")
		response.headers.set("Content-Length", data.toByteArray().size.toString())
		return response.writeWith(body)
	}
}

fun main(args: Array<String>) {
	runApplication<StreamErrorDemoApplication>(*args)
}
