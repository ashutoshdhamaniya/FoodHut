package com.example.foodhut.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodhut.R
import com.example.foodhut.adapter.FavouriteRecyclerAdapter
import com.example.foodhut.database.RestaurantDatabase
import com.example.foodhut.database.RestaurantEntity

class FavouriteRestaurantsFragment : Fragment() {

    private lateinit var favRecyclerView : RecyclerView
    private lateinit var favProgressLayout : RelativeLayout
    private lateinit var favProgressBar : ProgressBar
    private lateinit var favLayoutManager : RecyclerView.LayoutManager
    private lateinit var favRecyclerAdapter : FavouriteRecyclerAdapter
    private lateinit var favEmptyLayout : RelativeLayout
    private var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)

        favRecyclerView = view.findViewById(R.id.favouriteRecyclerView)
        favProgressLayout = view.findViewById(R.id.favouriteProgressLayout)
        favProgressBar = view.findViewById(R.id.favouriteProgressBar)
        favEmptyLayout = view.findViewById(R.id.emptyFavouritesLayouts)

        favLayoutManager = LinearLayoutManager(activity as Context , LinearLayoutManager.VERTICAL , false)

        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()

        if(activity != null){

            if (dbRestaurantList.isEmpty()){
                favProgressLayout.visibility = View.GONE

                favEmptyLayout.visibility = View.VISIBLE
            }

            favProgressLayout.visibility = View.GONE
            favRecyclerAdapter = FavouriteRecyclerAdapter(activity as Context , dbRestaurantList)
            favRecyclerView.adapter = favRecyclerAdapter
            favRecyclerView.layoutManager = favLayoutManager
        }

        return view
    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void , Void , List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context , RestaurantDatabase::class.java , "restaurant-db").build()
            return db.restaurantDao().getAllRestaurant()
        }
    }
}

