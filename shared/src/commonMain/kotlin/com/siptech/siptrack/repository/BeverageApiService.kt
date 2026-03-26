package com.siptech.siptrack.repository

import com.siptech.siptrack.models.Product
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class BeverageApiService(
    private val baseUrl: String = "http://localhost:3000",
    private val apiKey: String = "",
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun searchBeverages(
        query: String,
        category: String? = null,
        limit: Int = 20,
    ): List<Product> = runCatching {
        client.get("$baseUrl/api/v1/beverages/search") {
            parameter("q", query)
            if (category != null) parameter("category", category)
            parameter("limit", limit)
            if (apiKey.isNotBlank()) header("x-api-key", apiKey)
        }.body<List<Product>>()
    }.getOrDefault(emptyList())

    suspend fun getBeverage(id: String): Product? = runCatching {
        client.get("$baseUrl/api/v1/beverages/$id") {
            if (apiKey.isNotBlank()) header("x-api-key", apiKey)
        }.body<Product>()
    }.getOrNull()

    suspend fun createBeverage(product: Product): Product? = runCatching {
        client.post("$baseUrl/api/v1/beverages") {
            header("x-api-key", apiKey)
            setBody(product)
        }.body<Product>()
    }.getOrNull()

    fun close() { client.close() }
}
