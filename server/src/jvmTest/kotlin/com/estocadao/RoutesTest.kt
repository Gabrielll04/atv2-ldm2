package com.estocadao

import com.estocadao.models.Product
import com.estocadao.models.ProductRequest
import com.estocadao.models.StockItem
import com.estocadao.models.StockItemRequest
import com.estocadao.models.StockSummary
import com.estocadao.routes.configureRoutes
import com.estocadao.services.ProductService
import com.estocadao.services.StockService
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutesTest {
    private val product = Product(
        id = "11111111-1111-1111-1111-111111111111",
        name = "Caneta Azul",
        description = "Caneta esferografica",
        sku = "CAN-AZUL",
        category = "Papelaria",
        createdAt = "2026-05-18T10:00:00",
        updatedAt = "2026-05-18T10:00:00"
    )

    private val stockItem = StockItem(
        id = "22222222-2222-2222-2222-222222222222",
        productId = product.id,
        quantity = 10,
        unitPrice = 2.50,
        location = "Prateleira A",
        updatedAt = "2026-05-18T10:00:00"
    )

    @Test
    fun productCrudRoutesReturnExpectedStatusCodes() = testApplication {
        application {
            install(ServerContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            configureRoutes(FakeProductService(product), FakeStockService(stockItem))
        }

        val client = createClient {
            install(ClientContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        assertEquals(listOf(product), client.get("/products").body())
        assertEquals(product, client.get("/products/${product.id}").body())

        val created = client.post("/products") {
            contentType(ContentType.Application.Json)
            setBody(ProductRequest("Lapis", "Lapis preto", "LAP-01", "Papelaria"))
        }
        assertEquals(HttpStatusCode.Created, created.status)

        val updated = client.put("/products/${product.id}") {
            contentType(ContentType.Application.Json)
            setBody(ProductRequest("Caneta", "Atualizada", "CAN-01", "Papelaria"))
        }
        assertEquals(HttpStatusCode.OK, updated.status)

        assertEquals(HttpStatusCode.NoContent, client.delete("/products/${product.id}").status)
        assertEquals(HttpStatusCode.NotFound, client.get("/products/nao-existe").status)
    }

    @Test
    fun stockRoutesIncludeSummaryEndpoint() = testApplication {
        application {
            install(ServerContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            configureRoutes(FakeProductService(product), FakeStockService(stockItem))
        }

        val client = createClient {
            install(ClientContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        assertEquals(listOf(stockItem), client.get("/stock").body())
        assertEquals(stockItem, client.get("/stock/${stockItem.id}").body())
        assertEquals(
            listOf(StockSummary(product.id, product.name, 10)),
            client.get("/stock/summary").body()
        )

        val created = client.post("/stock") {
            contentType(ContentType.Application.Json)
            setBody(StockItemRequest(product.id, 5, 3.40, "Prateleira B"))
        }
        assertEquals(HttpStatusCode.Created, created.status)

        val updated = client.put("/stock/${stockItem.id}") {
            contentType(ContentType.Application.Json)
            setBody(StockItemRequest(product.id, 20, 2.90, "Prateleira C"))
        }
        assertEquals(HttpStatusCode.OK, updated.status)

        assertEquals(HttpStatusCode.NoContent, client.delete("/stock/${stockItem.id}").status)
        assertEquals(HttpStatusCode.NotFound, client.get("/stock/nao-existe").status)
    }

    private class FakeProductService(private val product: Product) : ProductService {
        override suspend fun listProducts() = listOf(product)
        override suspend fun getProduct(id: String) = product.takeIf { id == product.id }
        override suspend fun createProduct(request: ProductRequest) = product.copy(
            name = request.name,
            description = request.description,
            sku = request.sku,
            category = request.category
        )
        override suspend fun updateProduct(id: String, request: ProductRequest) = getProduct(id)?.copy(
            name = request.name,
            description = request.description,
            sku = request.sku,
            category = request.category
        )
        override suspend fun deleteProduct(id: String) = id == product.id
    }

    private class FakeStockService(private val stockItem: StockItem) : StockService {
        override suspend fun listStock() = listOf(stockItem)
        override suspend fun getStockItem(id: String) = stockItem.takeIf { id == stockItem.id }
        override suspend fun createStockItem(request: StockItemRequest) = stockItem.copy(
            productId = request.productId,
            quantity = request.quantity,
            unitPrice = request.unitPrice,
            location = request.location
        )
        override suspend fun updateStockItem(id: String, request: StockItemRequest) = getStockItem(id)?.copy(
            productId = request.productId,
            quantity = request.quantity,
            unitPrice = request.unitPrice,
            location = request.location
        )
        override suspend fun deleteStockItem(id: String) = id == stockItem.id
        override suspend fun getSummary() = listOf(StockSummary(stockItem.productId, "Caneta Azul", 10))
    }
}
