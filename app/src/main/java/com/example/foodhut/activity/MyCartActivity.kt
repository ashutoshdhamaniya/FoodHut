package com.example.foodhut.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhut.R
import com.example.foodhut.adapter.MyCartRecyclerAdapter
import com.example.foodhut.database.ItemEntity
import com.example.foodhut.database.RestaurantDatabase
import com.example.foodhut.model.RestaurantMenu
import com.example.foodhut.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MyCartActivity : AppCompatActivity() {

    private lateinit var myCartToolbar : Toolbar
    private lateinit var txtMyCartRestaurantName : TextView
    private lateinit var myCartRecyclerView : RecyclerView
    private lateinit var myCartLayoutManager : RecyclerView.LayoutManager
    private var itemsList = listOf<ItemEntity>()
    private lateinit var myCartRecyclerAdapter : MyCartRecyclerAdapter
    private lateinit var myCartPlaceOrderButton : TextView

    private var totalCost = 0

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        myCartToolbar = findViewById(R.id.myCartToolbar)
        txtMyCartRestaurantName = findViewById(R.id.txtMyCartRestaurantName)
        myCartRecyclerView = findViewById(R.id.myCartRecyclerView)
        myCartPlaceOrderButton = findViewById(R.id.txtMyCartPlaceOrderButton)
        myCartLayoutManager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false)

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)

        setUpToolbar()

        itemsList = RetrieveItems(this).execute().get()

        val restaurantName = intent.getStringExtra("restaurantName")
        txtMyCartRestaurantName.text = "Ordering From : $restaurantName"

        var foodItemIdArray = JSONArray()

        for(i in itemsList.indices) {
            val foodItemIdObject = JSONObject()
            totalCost += itemsList[i].itemCostForOne.toInt()

            foodItemIdObject.put("food_item_id" , itemsList[i].itemId)
            foodItemIdArray = foodItemIdArray.put(i , foodItemIdObject)
        }

        myCartRecyclerAdapter = MyCartRecyclerAdapter(this , itemsList)
        myCartRecyclerView.adapter = myCartRecyclerAdapter
        myCartRecyclerView.layoutManager = myCartLayoutManager

        myCartPlaceOrderButton.text = "Place Order(Total Rs. $totalCost)"

        myCartPlaceOrderButton.setOnClickListener {

            val queue = Volley.newRequestQueue(this)
            val placeOrderUrl = "http://13.235.250.119/v2/place_order/fetch_result/"
            val placeOrderJsonParams = JSONObject()

            placeOrderJsonParams.put("user_id" , "${sharedPreferences.getString("user_id" , "0")}")
            placeOrderJsonParams.put("restaurant_id" , itemsList[0].restaurantId)
            placeOrderJsonParams.put("total_cost" , totalCost)
            placeOrderJsonParams.put("food" , foodItemIdArray)

            if(ConnectionManager().checkConnectivity(this)){

                val jsonObjectRequest = object : JsonObjectRequest(Method.POST , placeOrderUrl , placeOrderJsonParams ,
                    Response.Listener {
                        try {
                            val dataObject = it.getJSONObject("data")
                            val success = dataObject.getBoolean("success")

                            if(success){
                                Toast.makeText(this , "Order placed successfully" , Toast.LENGTH_SHORT).show()

                                val orderSuccessfulIntent = Intent(this , OrderSuccessfulActivity::class.java)
                                orderSuccessfulIntent.putExtra("restaurantId" , itemsList[0].restaurantId)
                                startActivity(orderSuccessfulIntent)
                            }else{
                                Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()
                            }
                        }catch (e:JSONException)
                        {
                            Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()
                        }

                    } , Response.ErrorListener {
                        Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()
                    }){

                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String , String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "10ac7e21ebbf29"
                        return headers
                    }

                }

                queue.add(jsonObjectRequest)
            }else {
                val dialog = AlertDialog.Builder(this)
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

        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(myCartToolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class RetrieveItems(val context: Context) : AsyncTask<Void , Void , List<ItemEntity>>(){
        override fun doInBackground(vararg params: Void?): List<ItemEntity> {
            val db = Room.databaseBuilder(context , RestaurantDatabase::class.java , "restaurant-db").build()
            return db.cartDao().getAllItems()
        }
    }
}