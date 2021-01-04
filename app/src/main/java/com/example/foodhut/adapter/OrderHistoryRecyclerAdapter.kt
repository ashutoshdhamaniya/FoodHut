package com.example.foodhut.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodhut.R
import com.example.foodhut.database.ItemEntity
import com.example.foodhut.model.OrderDetails
import com.example.foodhut.model.RestaurantMenu
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryRecyclerAdapter(val context: Context , private val orderHistoryList : ArrayList<OrderDetails>)
    : RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder (view : View) : RecyclerView.ViewHolder(view) {

        val singleRowRestaurantName : TextView = view.findViewById(R.id.txtRecSingleRowRestaurantName)
        val singleRowOrderDate : TextView = view.findViewById(R.id.txtRecSingleRowOrderDate)
        val singleRowRecyclerView : RecyclerView = view.findViewById(R.id.recyclerSingleRowRecyclerView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_row , parent , false)

        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val items = orderHistoryList[position]

        holder.singleRowRestaurantName.text = items.resName
        holder.singleRowOrderDate.text = formatDate(items.orderDate)
        setUpRecycler(holder.singleRowRecyclerView, items)
    }

    private fun setUpRecycler(recyclerResHistory: RecyclerView, orderHistoryList: OrderDetails) {
        val foodItemsList = ArrayList<RestaurantMenu>()
        for (i in 0 until orderHistoryList.foodItems.length()) {
            val foodJson = orderHistoryList.foodItems.getJSONObject(i)

            foodItemsList.add(
                RestaurantMenu(
                    foodJson.getString("food_item_id") ,
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val cartItemAdapter = CustomRowRecyclerAdapter(context , foodItemsList)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerResHistory.layoutManager = mLayoutManager
        recyclerResHistory.itemAnimator = DefaultItemAnimator()
        recyclerResHistory.adapter = cartItemAdapter
    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }

}