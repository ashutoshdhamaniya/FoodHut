package com.example.foodhut.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodhut.R
import com.example.foodhut.activity.RestaurantMenuActivity
import com.example.foodhut.database.ItemEntity
import com.example.foodhut.database.RestaurantDatabase
import com.example.foodhut.model.RestaurantMenu

class RestaurantMenuRecyclerAdapter(private val context: Context, private val menuItemList : ArrayList<RestaurantMenu>)
    : RecyclerView.Adapter<RestaurantMenuRecyclerAdapter.RecyclerMenuViewHolder>() {

    private var itemCount = 0
    companion object {
        var isCartEmpty : Boolean = true

    }

    class RecyclerMenuViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val menuItemSerialNumber : TextView  = view.findViewById(R.id.txtMenuRecyclerSerialNumber)
        val menuItemName : TextView = view.findViewById(R.id.txtMenuRecyclerItemName)
        val menuItemCostForOne : TextView = view.findViewById(R.id.txtMenuRecyclerItemCostForOne)
        val menuItemAddToCartButton : TextView = view.findViewById(R.id.txtMenuRecyclerAddButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerMenuViewHolder {
        val restaurantMenuView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_menu_single_row , parent ,false)

        return RecyclerMenuViewHolder(restaurantMenuView)
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }

    override fun onBindViewHolder(holder: RecyclerMenuViewHolder, position: Int) {

        val restaurantMenuItem = menuItemList[position]

        holder.menuItemSerialNumber.text = "${position+1}"
        holder.menuItemName.text = restaurantMenuItem.itemName
        holder.menuItemCostForOne.text = "Rs ${restaurantMenuItem.itemCostForOne}"

        val itemEntity = ItemEntity(
            RestaurantMenuActivity.restaurantId ,
            restaurantMenuItem.itemId.toInt() ,
            restaurantMenuItem.itemName ,
            restaurantMenuItem.itemCostForOne
        )

        holder.menuItemAddToCartButton.setOnClickListener {

            if(!DBAsyncTask(context , itemEntity , 1).execute().get()){
                val async = DBAsyncTask(context , itemEntity , 2).execute()
                val result = async.get()

                if(result){
                    holder.menuItemAddToCartButton.text = "Remove"
                    itemCount++
                    val favColor = ContextCompat.getColor(context , R.color.colorPrimary)
                    holder.menuItemAddToCartButton.setBackgroundColor(favColor)
                }else{
                    Toast.makeText(context , "Error" , Toast.LENGTH_SHORT).show()
                }
            }else{
                val async = DBAsyncTask(context , itemEntity , 3).execute()
                val result = async.get()

                if(result){
                    holder.menuItemAddToCartButton.text = "Add"
                    itemCount--
                    val favColor = ContextCompat.getColor(context , R.color.btn_color)
                    holder.menuItemAddToCartButton.setBackgroundColor(favColor)
                }else{
                    Toast.makeText(context , "Error" , Toast.LENGTH_SHORT).show()
                }
            }

            if(!isCartEmpty && itemCount > 0){
                RestaurantMenuActivity.proceedToCartButton.visibility = View.VISIBLE
            }else{
                RestaurantMenuActivity.proceedToCartButton.visibility = View.INVISIBLE
            }

        }

    }

    class DBAsyncTask(private val context: Context , private val itemEntity: ItemEntity , private val mode : Int) : AsyncTask<Void , Void , Boolean>(){

        private val itemDb = Room.databaseBuilder(context , RestaurantDatabase::class.java , "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1 -> {
                    val item : ItemEntity? = itemDb.cartDao().getItemById(itemEntity.itemId.toString())
                    itemDb.close()
                    return item != null
                }
                2 -> {
                    itemDb.cartDao().insertItemIntoCart(itemEntity)
                    itemDb.close()
                    isCartEmpty = false
                    return true
                }
                3 -> {
                    itemDb.cartDao().deleteItemFromCart(itemEntity)
                    itemDb.close()
                    return true
                }
            }
            return false
        }

    }

    fun getItemSelectedCount(): Int {
        return itemCount
    }

}