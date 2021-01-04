package com.example.foodhut.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodhut.R
import com.example.foodhut.activity.MainActivity
import com.example.foodhut.activity.RestaurantMenuActivity
import com.example.foodhut.database.RestaurantDatabase
import com.example.foodhut.database.RestaurantEntity
import com.example.foodhut.model.Restaurant
import com.squareup.picasso.Picasso


class HomeRecyclerAdapter(private val context : Context, private val itemList : ArrayList<Restaurant>) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(){

    class HomeViewHolder (view : View) : RecyclerView.ViewHolder(view){

        /* Assigning recycler_home_single_row elements */

        val imgFoodImageView : ImageView = view.findViewById(R.id.imgHomeRestaurantImage)
        val txtRestaurantName : TextView = view.findViewById(R.id.txtHomeRestaurantName)
        val txtFoodPrice : TextView = view.findViewById(R.id.txtHomeCostForOne)
        val txtAddBtnFavourite : TextView = view.findViewById(R.id.txtHomeAddFavouriteImageBtn)
        val txtRestaurantRating : TextView = view.findViewById(R.id.txtHomeRestaurantRating)
        val llContainer : CardView = view.findViewById(R.id.llHomeRecyclerContainer)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row , parent , false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtFoodPrice.text = " Rs." +restaurant.restaurantCostPerPerson + "/person"
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).into(holder.imgFoodImageView)

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId.toInt() ,
            restaurant.restaurantName ,
            restaurant.restaurantRating ,
            restaurant.restaurantCostPerPerson ,
            restaurant.restaurantImage
        )

        holder.llContainer.setOnClickListener{

            val restaurantMenuIntent = Intent(context , RestaurantMenuActivity::class.java)
            restaurantMenuIntent.putExtra("restaurantId" , restaurant.restaurantId.toInt())
            restaurantMenuIntent.putExtra("restaurantName" , restaurant.restaurantName.toString())

            context.startActivity(restaurantMenuIntent)

        }

        /* To check that a restaurant is already added to favourites or not */

        val checkFav = DBAsyncTask(context , restaurantEntity , 1).execute()
        val isFav = checkFav.get()

        if(isFav){
            holder.txtAddBtnFavourite.setBackgroundResource(R.drawable.ic_favourite)
        }else{
            holder.txtAddBtnFavourite.setBackgroundResource(R.drawable.ic_favourite_border)
        }

        /* Adding a restaurant into favourites */

        holder.txtAddBtnFavourite.setOnClickListener{
            if(!DBAsyncTask(context , restaurantEntity , 1).execute().get()){
                val async = DBAsyncTask(context , restaurantEntity , 2).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context , "Added" , Toast.LENGTH_SHORT).show()

                    holder.txtAddBtnFavourite.setBackgroundResource(R.drawable.ic_favourite)
                }else{
                    Toast.makeText(context , "Error" , Toast.LENGTH_SHORT).show()
                }
            }else{
                val async = DBAsyncTask(context , restaurantEntity , 3).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context , "Remove" , Toast.LENGTH_SHORT).show()

                    holder.txtAddBtnFavourite.setBackgroundResource(R.drawable.ic_favourite_border)
                }else{
                    Toast.makeText(context , "Error" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /* Class for background tasks for adding and removing from favourites */
    /*
        1 -> for check already added to favourite or not
        2 -> for adding into favourite
        3 -> for remove from favourite
    */

    class DBAsyncTask(private val context: Context, private val restaurantEntity: RestaurantEntity, private val mode : Int) : AsyncTask<Void, Void, Boolean>(){

        private val db = Room.databaseBuilder(context , RestaurantDatabase::class.java , "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1 -> {
                    val restaurant: RestaurantEntity?= db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return restaurant != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}