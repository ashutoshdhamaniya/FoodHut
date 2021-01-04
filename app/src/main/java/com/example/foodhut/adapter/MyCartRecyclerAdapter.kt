package com.example.foodhut.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhut.R
import com.example.foodhut.activity.MyCartActivity
import com.example.foodhut.database.ItemEntity
import com.example.foodhut.model.RestaurantMenu

class MyCartRecyclerAdapter(val context: Context, private val cartList : List<ItemEntity>) : RecyclerView.Adapter<MyCartRecyclerAdapter.MyCartViewHolder>(){

    class MyCartViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val txtMyCartItemName : TextView = view.findViewById(R.id.txtMyCartItemName)
        val txtMyCartItemPrice : TextView = view.findViewById(R.id.txtMyCartItemPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_my_cart_single_row , parent , false)
        return MyCartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        val items = cartList[position]

        holder.txtMyCartItemName.text = items.itemName
        holder.txtMyCartItemPrice.text = "Rs. ${items.itemCostForOne}"

    }

}