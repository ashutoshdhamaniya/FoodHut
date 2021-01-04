package com.example.foodhut.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhut.R
import com.example.foodhut.model.RestaurantMenu

class CustomRowRecyclerAdapter(val context: Context , private val cartList: List<RestaurantMenu>)
    : RecyclerView.Adapter<CustomRowRecyclerAdapter.CustomRowViewHolder>(){

    class CustomRowViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val itemName : TextView = view.findViewById(R.id.txtCustomRowItemName)
        val itemCostForOne : TextView = view.findViewById(R.id.txtCustomRowItemPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomRowViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_custom_row, parent , false)
        return CustomRowViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: CustomRowViewHolder, position: Int) {
        val cartObject = cartList[position]



        holder.itemName.text = cartObject.itemName
        holder.itemCostForOne.text = "Rs. ${cartObject.itemCostForOne}"

        Log.d("Ashu" , "Item name === ${holder.itemName.text}")
        Log.d("Ashu" , "Item name === ${cartObject.itemName}")
    }

}