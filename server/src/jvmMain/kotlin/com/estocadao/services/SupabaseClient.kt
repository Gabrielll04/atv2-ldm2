package com.estocadao.services

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun defaultHttpClient() = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
}

fun HttpRequestBuilder.supabaseUrl(baseUrl: String, path: String) {
    require(baseUrl.isNotBlank()) { "SUPABASE_URL nao foi configurada" }

    url {
        val cleanUrl = baseUrl.removeSuffix("/")
            .removePrefix("https://")
            .removePrefix("http://")
        protocol = URLProtocol.HTTPS
        host = cleanUrl
        path("rest", "v1", path)
    }
}

fun HttpRequestBuilder.supabaseHeaders(key: String) {
    require(key.isNotBlank()) { "SUPABASE_KEY nao foi configurada" }

    header("apikey", key)
    header(HttpHeaders.Authorization, "Bearer $key")
}
