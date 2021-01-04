package com.example.foodhut.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntity::class , ItemEntity::class] , version = 1)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract fun restaurantDao() : RestaurantDao

    abstract fun cartDao() : CartDao

}

