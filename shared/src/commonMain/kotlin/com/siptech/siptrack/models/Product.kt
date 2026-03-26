package com.siptech.siptrack.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val brandId: String? = null,
    val brandName: String? = null,
    val category: ProductCategory,
    val subcategory: String? = null,
    val vintage: Int? = null,
    val abv: Float,
    val proof: Float? = null,
    val countryOfOrigin: String? = null,
    val region: String? = null,
    val description: String? = null,
    val flavorProfile: List<String> = emptyList(),
    val imageUrl: String? = null,
    val availability: Availability = Availability.NATIONWIDE,
    val status: ProductStatus = ProductStatus.ACTIVE,
    val avgPriceUsd: Float? = null,
    val ratingAvg: Float? = null,
    val ratingCount: Int = 0,
)

@Serializable
enum class ProductCategory(val displayName: String) {
    BEER("Beer"),
    WINE("Wine"),
    SPIRIT("Spirit"),
    HARD_SELTZER("Hard Seltzer"),
    RTD_COCKTAIL("Ready-to-Drink"),
    CIDER("Cider"),
    MEAD("Mead"),
    SAKE("Sake"),
    OTHER("Other")
}

@Serializable
enum class Availability {
    NATIONWIDE, REGIONAL, LIMITED, SEASONAL, DISCONTINUED
}

@Serializable
enum class ProductStatus {
    ACTIVE, ENRICHMENT_PENDING, QUARANTINED, DISCONTINUED
}
