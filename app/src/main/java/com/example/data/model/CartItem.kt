package com.example.data.model

data class CartItem(
    val drugId: String,
    val tradeName: String,
    val agencyName: String,
    val agencyId: String = "",
    val netPrice: Double,
    val bonusRatio: String,
    val netCode: String,
    var quantity: Int
)
