package com.estocadao.routes

import com.estocadao.models.ProductRequest
import com.estocadao.models.StockItemRequest
import com.estocadao.services.ProductService
import com.estocadao.services.StockService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing

fun Application.configureRoutes(
    productService: ProductService,
    stockService: StockService
) {
    routing {
        get("/products") {
            call.respond(productService.listProducts())
        }

        get("/products/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText("ID invalido", status = HttpStatusCode.BadRequest)
            val product = productService.getProduct(id)
            if (product == null) call.respondText("Produto nao encontrado", status = HttpStatusCode.NotFound)
            else call.respond(product)
        }

        post("/products") {
            val request = call.receive<ProductRequest>()
            call.respond(HttpStatusCode.Created, productService.createProduct(request))
        }

        put("/products/{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText("ID invalido", status = HttpStatusCode.BadRequest)
            val request = call.receive<ProductRequest>()
            val product = productService.updateProduct(id, request)
            if (product == null) call.respondText("Produto nao encontrado", status = HttpStatusCode.NotFound)
            else call.respond(product)
        }

        delete("/products/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondText("ID invalido", status = HttpStatusCode.BadRequest)
            if (productService.deleteProduct(id)) call.respond(HttpStatusCode.NoContent)
            else call.respondText("Produto nao encontrado", status = HttpStatusCode.NotFound)
        }

        get("/stock") {
            call.respond(stockService.listStock())
        }

        get("/stock/summary") {
            call.respond(stockService.getSummary())
        }

        get("/stock/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText("ID invalido", status = HttpStatusCode.BadRequest)
            val item = stockService.getStockItem(id)
            if (item == null) call.respondText("Item de estoque nao encontrado", status = HttpStatusCode.NotFound)
            else call.respond(item)
        }

        post("/stock") {
            val request = call.receive<StockItemRequest>()
            call.respond(HttpStatusCode.Created, stockService.createStockItem(request))
        }

        put("/stock/{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText("ID invalido", status = HttpStatusCode.BadRequest)
            val request = call.receive<StockItemRequest>()
            val item = stockService.updateStockItem(id, request)
            if (item == null) call.respondText("Item de estoque nao encontrado", status = HttpStatusCode.NotFound)
            else call.respond(item)
        }

        delete("/stock/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondText("ID invalido", status = HttpStatusCode.BadRequest)
            if (stockService.deleteStockItem(id)) call.respond(HttpStatusCode.NoContent)
            else call.respondText("Item de estoque nao encontrado", status = HttpStatusCode.NotFound)
        }
    }
}
