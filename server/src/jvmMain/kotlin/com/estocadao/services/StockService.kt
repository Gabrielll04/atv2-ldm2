package com.estocadao.services

import com.estocadao.models.StockItem
import com.estocadao.models.StockItemRequest
import com.estocadao.models.StockSummary

interface StockService {
    suspend fun listStock(): List<StockItem>
    suspend fun getStockItem(id: String): StockItem?
    suspend fun createStockItem(request: StockItemRequest): StockItem
    suspend fun updateStockItem(id: String, request: StockItemRequest): StockItem?
    suspend fun deleteStockItem(id: String): Boolean
    suspend fun getSummary(): List<StockSummary>
}
