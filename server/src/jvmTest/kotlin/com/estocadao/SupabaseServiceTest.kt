package com.estocadao

import com.estocadao.models.ProductRequest
import com.estocadao.services.SupabaseProductService
import com.estocadao.services.SupabaseStockService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class SupabaseServiceTest {
    @Test
    fun productServiceUsesSupabaseRestHeadersAndPaths() = runBlocking {
        val seenUrls = mutableListOf<String>()
        val seenAuth = mutableListOf<String?>()
        val client = HttpClient(MockEngine) { engine {
            addHandler { request ->
                seenUrls += request.url.encodedPath
                seenAuth += request.headers[HttpHeaders.Authorization]
                respond(
                    content = """[{"id":"1","name":"Caneta","description":"Azul","sku":"CAN-1","category":"Papelaria","created_at":"now","updated_at":"now"}]""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val service = SupabaseProductService(
            supabaseUrl = "https://teste.supabase.co",
            supabaseKey = "abc123",
            client = client
        )

        service.listProducts()
        service.createProduct(ProductRequest("Caneta", "Azul", "CAN-1", "Papelaria"))

        assertEquals(listOf("/rest/v1/products", "/rest/v1/products"), seenUrls)
        assertEquals(listOf<String?>("Bearer abc123", "Bearer abc123"), seenAuth)
    }

    @Test
    fun stockSummaryUsesRpcEndpoint() = runBlocking {
        val seenUrls = mutableListOf<String>()
        val client = HttpClient(MockEngine) { engine {
            addHandler { request ->
                seenUrls += request.url.encodedPath
                respond(
                    content = """[{"product_id":"1","product_name":"Caneta","total_quantity":30}]""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        }
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val service = SupabaseStockService(
            supabaseUrl = "https://teste.supabase.co",
            supabaseKey = "abc123",
            client = client
        )

        val summary = service.getSummary()

        assertEquals(listOf("/rest/v1/rpc/stock_summary"), seenUrls)
        assertEquals(30, summary.first().totalQuantity)
    }
}
