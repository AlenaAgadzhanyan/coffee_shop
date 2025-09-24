package com.example.coffeeshop.Domain

data class Order(
    val orderId: String = "",
    val date: String = "",
    val total: Double = 0.0,
    val items: List<ItemsModel> = emptyList(),
    val userId: String = ""
)