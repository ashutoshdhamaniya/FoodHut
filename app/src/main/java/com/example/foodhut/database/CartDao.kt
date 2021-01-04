package com.example.foodhut.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {

    @Insert
    fun insertItemIntoCart(itemEntity: ItemEntity)

    @Delete
    fun deleteItemFromCart(itemEntity: ItemEntity)

    @Query("SELECT * FROM orderItems WHERE itemId = :id")
    fun getItemById(id : String) : ItemEntity

    @Query("SELECT * FROM orderItems")
    fun getAllItems() : List<ItemEntity>

    @Query("DELETE FROM orderItems WHERE restaurantId = :resId")
    fun deleteOrders(resId: String)

}
