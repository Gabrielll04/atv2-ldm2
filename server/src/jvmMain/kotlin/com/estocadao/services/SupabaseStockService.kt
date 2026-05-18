package com.estocadao.services

import com.estocadao.models.StockItem
import com.estocadao.models.StockItemRequest
import com.estocadao.models.StockSummary
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class SupabaseStockService(
    private val supabaseUrl: String,
    private val supabaseKey: String,
    private val client: HttpClient = defaultHttpClient()
) : StockService {
    override suspend fun listStock(): List<StockItem> =
        client.get {
            supabaseUrl(supabaseUrl, "stock_items")
            supabaseHeaders(supabaseKey)
            url.parameters.append("select", "*")
        }.body()

    override suspend fun getStockItem(id: String): StockItem? =
        client.get {
            supabaseUrl(supabaseUrl, "stock_items")
            supabaseHeaders(supabaseKey)
            url.parameters.append("id", "eq.$id")
            url.parameters.append("select", "*")
        }.body<List<StockItem>>().firstOrNull()

    override suspend fun createStockItem(request: StockItemRequest): StockItem =
        client.post {
            supabaseUrl(supabaseUrl, "stock_items")
            supabaseHeaders(supabaseKey)
            contentType(ContentType.Application.Json)
            headers.append("Prefer", "return=representation")
            setBody(request)
        }.body<List<StockItem>>().first()

    override suspend fun updateStockItem(id: String, request: StockItemRequest): StockItem? =
        client.patch {
            supabaseUrl(supabaseUrl, "stock_items")
            supabaseHeaders(supabaseKey)
            contentType(ContentType.Application.Json)
            headers.append("Prefer", "return=representation")
            url.parameters.append("id", "eq.$id")
            setBody(request)
        }.body<List<StockItem>>().firstOrNull()

    override suspend fun deleteStockItem(id: String): Boolean {
        if (getStockItem(id) == null) return false

        val response = client.delete {
            supabaseUrl(supabaseUrl, "stock_items")
            supabaseHeaders(supabaseKey)
            url.parameters.append("id", "eq.$id")
        }

        return response.status.isSuccess()
    }

    override suspend fun getSummary(): List<StockSummary> =
        client.get {
            supabaseUrl(supabaseUrl, "rpc/stock_summary")
            supabaseHeaders(supabaseKey)
        }.body()
}
