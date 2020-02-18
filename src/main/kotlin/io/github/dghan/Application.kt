package io.github.dghan

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).apply {
        start(wait = true)
    }
}

fun Application.module() {
    install()
    routing()
}

fun Application.install() {
    install(DefaultHeaders) {}
    install(CallLogging) {
        level = Level.DEBUG
    }
    install(ContentNegotiation) {
        jackson {
        }
    }
}

fun Application.routing() {
    routing {
        route("accounts") {
            post {
                val account = call.receive<Account>()
                call.respond(account)
            }
            put("/{id}") {
                val account = call.receive<Account>()
                call.respond(account)
            }
            get {
                call.respond(listOf(Account(1, "name"), Account(2, "name1")))
            }
            get("/{id}") {
                call.respond(Account(1, "name"))
            }
            delete("/{id}") {
                call.respondText("OK")
            }
        }
    }
}
