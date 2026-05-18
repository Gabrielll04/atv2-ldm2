package com.estocadao.services

import com.estocadao.models.Product
import com.estocadao.models.ProductRequest

interface ProductService {
    suspend fun listProducts(): List<Product>
    suspend fun getProduct(id: String): Product?
    suspend fun createProduct(request: ProductRequest): Product
    suspend fun updateProduct(id: String, request: ProductRequest): Product?
    suspend fun deleteProduct(id: String): Boolean
}
