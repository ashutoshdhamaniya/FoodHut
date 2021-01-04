package com.example.foodhut.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodhut.R
import com.example.foodhut.activity.MyCartActivity
import com.example.foodhut.activity.RestaurantMenuActivity
import com.example.foodhut.database.RestaurantDatabase
import com.example.foodhut.database.RestaurantEntity
import com.example.foodhut.model.RestaurantMenu
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, private val restaurantList: List<RestaurantEntity>) : RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFavRestaurantName: TextView = view.findViewById(R.id.txtFavouriteRestaurantName)
        val txtFavRestaurantRating: TextView = view.findViewById(R.id.txtFavouriteRestaurantRating)
        val txtFavCostForOne: TextView = view.findViewById(R.id.txtFavouriteCostForOne)
        val imgFavRestaurantImage: ImageView = view.findViewById(R.id.imgFavouriteRestaurantImage)
        val txtFavButtonImage: TextView = view.findViewById(R.id.txtFavouriteAddImageButton)
        val llFavContainer: CardView = view.findViewById(R.id.llFavouriteRecyclerContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row , parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        holder.txtFavRestaurantName.text = restaurant.restaurantName
        holder.txtFavRestaurantRating.text = restaurant.restaurantRating
        holder.txtFavCostForOne.text = "Rs. ${restaurant.costPerPerson} /person"
        Picasso.get().load(restaurant.restaurantImage).into(holder.imgFavRestaurantImage)

        // for removing from favourites

        val restaurantEntity = RestaurantEntity(
            restaurant.id,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.costPerPerson,
            restaurant.restaurantImage
        )

        holder.txtFavButtonImage.setOnClickListener {

            val async = DBAsyncTask(context, restaurantEntity).execute()
            val result = async.get()

            if (result) {
                Toast.makeText(context, "removed", Toast.LENGTH_SHORT).show()
                holder.txtFavButtonImage.setBackgroundResource(R.drawable.ic_favourite_border)
            } else {
                Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
            }
        }

        holder.llFavContainer.setOnClickListener{
            val menuIntent = Intent(context , RestaurantMenuActivity::class.java)
            menuIntent.putExtra("restaurantId" , restaurantEntity.id)
            menuIntent.putExtra("restaurantName" , restaurantEntity.restaurantName)

            context.startActivity(menuIntent)

        }

    }

    class DBAsyncTask(private val context: Context, private val restaurantEntity: RestaurantEntity) : AsyncTask<Void, Void, Boolean>() {

        private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            db.restaurantDao().deleteRestaurant(restaurantEntity)
            db.close()
            return true
        }
    }
}
