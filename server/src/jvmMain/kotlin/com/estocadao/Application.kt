package com.estocadao

import com.estocadao.routes.configureRoutes
import com.estocadao.services.SupabaseProductService
import com.estocadao.services.SupabaseStockService
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Estocadao")

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                status = io.ktor.http.HttpStatusCode.BadRequest,
                message = ErrorResponse(cause.message ?: "Requisicao invalida")
            )
        }
    }

    val config = loadSupabaseConfig()
    logger.info(
        "Supabase config: url={}, key={}",
        if (config.url.isBlank()) "NAO CARREGADA" else config.url,
        if (config.key.isBlank()) "NAO CARREGADA" else "CARREGADA"
    )

    configureRoutes(
        productService = SupabaseProductService(config.url, config.key),
        stockService = SupabaseStockService(config.url, config.key)
    )
}
