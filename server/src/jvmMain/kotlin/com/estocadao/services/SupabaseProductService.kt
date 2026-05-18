package com.estocadao.services

import com.estocadao.models.Product
import com.estocadao.models.ProductRequest
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

class SupabaseProductService(
    private val supabaseUrl: String,
    private val supabaseKey: String,
    private val client: HttpClient = defaultHttpClient()
) : ProductService {
    override suspend fun listProducts(): List<Product> =
        client.get {
            supabaseUrl(supabaseUrl, "products")
            supabaseHeaders(supabaseKey)
            url.parameters.append("select", "*")
            url.parameters.append("order", "created_at.desc")
        }.body()

    override suspend fun getProduct(id: String): Product? =
        client.get {
            supabaseUrl(supabaseUrl, "products")
            supabaseHeaders(supabaseKey)
            url.parameters.append("id", "eq.$id")
            url.parameters.append("select", "*")
        }.body<List<Product>>().firstOrNull()

    override suspend fun createProduct(request: ProductRequest): Product =
        client.post {
            supabaseUrl(supabaseUrl, "products")
            supabaseHeaders(supabaseKey)
            contentType(ContentType.Application.Json)
            headers.append("Prefer", "return=representation")
            setBody(request)
        }.body<List<Product>>().first()

    override suspend fun updateProduct(id: String, request: ProductRequest): Product? =
        client.patch {
            supabaseUrl(supabaseUrl, "products")
            supabaseHeaders(supabaseKey)
            contentType(ContentType.Application.Json)
            headers.append("Prefer", "return=representation")
            url.parameters.append("id", "eq.$id")
            setBody(request)
        }.body<List<Product>>().firstOrNull()

    override suspend fun deleteProduct(id: String): Boolean {
        if (getProduct(id) == null) return false

        val response = client.delete {
            supabaseUrl(supabaseUrl, "products")
            supabaseHeaders(supabaseKey)
            url.parameters.append("id", "eq.$id")
        }

        return response.status.isSuccess()
    }
}
