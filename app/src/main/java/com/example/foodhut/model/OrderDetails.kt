package com.example.foodhut.model

import org.json.JSONArray

data class OrderDetails(

    val orderId: Int,
    val resName: String,
    val orderDate: String,
    val foodItems: JSONArray
)