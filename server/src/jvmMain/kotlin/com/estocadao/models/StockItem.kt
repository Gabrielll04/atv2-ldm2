package com.estocadao.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockItem(
    val id: String,
    @SerialName("product_id")
    val productId: String,
    val quantity: Int,
    @SerialName("unit_price")
    val unitPrice: Double,
    val location: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class StockItemRequest(
    @SerialName("product_id")
    val productId: String,
    val quantity: Int,
    @SerialName("unit_price")
    val unitPrice: Double,
    val location: String
)

@Serializable
data class StockSummary(
    @SerialName("product_id")
    val productId: String,
    @SerialName("product_name")
    val productName: String,
    @SerialName("total_quantity")
    val totalQuantity: Int
)
