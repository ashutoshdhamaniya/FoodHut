package com.example.foodhut.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orderItems")
data class ItemEntity (
    @ColumnInfo(name = "restaurantId") val restaurantId : Int,
    @PrimaryKey val itemId : Int,
    @ColumnInfo(name = "item_name") val itemName : String,
    @ColumnInfo(name = "item_cost_per_person") val itemCostForOne : String
)
