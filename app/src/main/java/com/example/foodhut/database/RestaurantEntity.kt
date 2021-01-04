package com.example.foodhut.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity (
    @PrimaryKey val id : Int,
    @ColumnInfo(name = "restaurant_name") val restaurantName : String,
    @ColumnInfo(name = "restaurant_rating") val  restaurantRating : String,
    @ColumnInfo(name = "cost_per_person") val costPerPerson : String,
    @ColumnInfo(name = "restaurant_image") val restaurantImage : String
)