package com.example.foodhut.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhut.R
import com.example.foodhut.adapter.FavouriteRecyclerAdapter
import com.example.foodhut.adapter.RestaurantMenuRecyclerAdapter
import com.example.foodhut.database.RestaurantDatabase
import com.example.foodhut.fragment.FavouriteRestaurantsFragment
import com.example.foodhut.fragment.HomeFragment
import com.example.foodhut.model.RestaurantMenu
import com.example.foodhut.util.ConnectionManager
import org.json.JSONException

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RestaurantMenuActivity : AppCompatActivity() {

    private lateinit var restaurantMenuToolbar : Toolbar
    private lateinit var restaurantMenuItemRecyclerView : RecyclerView
    private lateinit var menuItemLayoutManager : RecyclerView.LayoutManager
    private lateinit var menuItemRecyclerAdapter : RestaurantMenuRecyclerAdapter
    companion object {
        lateinit var proceedToCartButton: TextView
        var restaurantId : Int = 0
    }
    private lateinit var restaurantMenuProgressLayout : RelativeLayout
    private lateinit var restaurantMenuProgressBar : ProgressBar

    val restaurantMenuItemList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        restaurantMenuToolbar = findViewById(R.id.restaurantMenuToolbar)
        restaurantMenuProgressLayout = findViewById(R.id.restaurantMenuProgressLayout)
        restaurantMenuProgressBar = findViewById(R.id.restaurantMenuProgressBar)
        proceedToCartButton = findViewById(R.id.restaurantMenuProceedToCartBtn)

        restaurantMenuItemRecyclerView = findViewById(R.id.restaurantMenuRecyclerView)
        menuItemLayoutManager = LinearLayoutManager(this)

        restaurantId = intent.getIntExtra("restaurantId" , 0)
        val restaurantName = intent.getStringExtra("restaurantName")

        setUpToolbar(restaurantName)

        val menuUrl = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId/"

        restaurantMenuProgressLayout.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)

        if(ConnectionManager().checkConnectivity(this)) {

            val menuJsonObjectRequest =
                object : JsonObjectRequest(Method.GET, menuUrl, null,
                    Response.Listener {
                        try {
                            val dataObject = it.getJSONObject("data")
                            val success = dataObject.getBoolean("success")

                            if (success) {

                                restaurantMenuProgressLayout.visibility = View.GONE
                                val menuItems = dataObject.getJSONArray("data")

                                for (i in 0 until menuItems.length()) {

                                    val menuItemsJsonObject = menuItems.getJSONObject(i)

                                    val restaurantMenuItemObject = RestaurantMenu(
                                        menuItemsJsonObject.getString("id"),
                                        menuItemsJsonObject.getString("name"),
                                        menuItemsJsonObject.getString("cost_for_one")
                                    )

                                    restaurantMenuItemList.add(restaurantMenuItemObject)
                                    menuItemRecyclerAdapter =
                                        RestaurantMenuRecyclerAdapter(this, restaurantMenuItemList)
                                    restaurantMenuItemRecyclerView.adapter = menuItemRecyclerAdapter
                                    restaurantMenuItemRecyclerView.layoutManager =
                                        menuItemLayoutManager

                                }

                            } else {

                                restaurantMenuProgressLayout.visibility = View.GONE

                                Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {

                            restaurantMenuProgressLayout.visibility = View.GONE
                            Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()

                        }
                    },
                    Response.ErrorListener {

                        Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()

                    }) {

                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String, String>()

                        headers["Content-Type"] = "application/json"
                        headers["token"] = "10ac7e21ebbf29"

                        return headers
                    }
                }

            queue.add(menuJsonObjectRequest)
        }else {
            val dialog = android.app.AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Setting") { _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                this.finish()
            }

            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this)
            }

            dialog.create()
            dialog.show()
        }

        proceedToCartButton.setOnClickListener {
            val cartIntent = Intent(this , MyCartActivity::class.java)
            cartIntent.putExtra("restaurantName" , restaurantName)
            cartIntent.putExtra("restaurantId" , restaurantId)
            startActivity(cartIntent)
        }
    }

    private fun setUpToolbar(restaurantName : String) {
        setSupportActionBar(restaurantMenuToolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        if(menuItemRecyclerAdapter.getItemSelectedCount() > 0) {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Confirmation")
            alertDialog.setMessage("Going back will reset cart items. Do you still want to proceed?")
            alertDialog.setPositiveButton("Yes"){ _ , _ ->


                    val clearCart = DBAsyncTask(this, restaurantId.toString()).execute().get()
                    if (clearCart) {
                        Toast.makeText(this, "Cart Clear", Toast.LENGTH_SHORT).show()
                    }
                    RestaurantMenuRecyclerAdapter.isCartEmpty = true

                    val homeIntent = Intent(this , MainActivity::class.java)
                    startActivity(homeIntent)
                    finish()

            }
            alertDialog.setNegativeButton("No"){ _ , _ ->

                //Do nothing

            }

            alertDialog.create().show()

        }else{

            val homeIntent = Intent(this , MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        }

    }

    class DBAsyncTask(val context: Context , val restaurantId : String) : AsyncTask<Void , Void , Boolean>(){

        private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            db.cartDao().deleteOrders(restaurantId)
            db.close()
            return true
        }

    }

    override fun onDestroy() {
        Log.d("Ashu" , "Ashutosh OnDestory Called")
        super.onDestroy()
    }
}