package com.example.foodhut.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.example.foodhut.R
import com.example.foodhut.database.RestaurantDatabase

class OrderSuccessfulActivity : AppCompatActivity() {

    private lateinit var okButton : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_successful)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        okButton = findViewById(R.id.orderSuccessfulOkBtn)

        val clear = DBAsyncTask(this , RestaurantMenuActivity.restaurantId.toString()).execute().get()

        okButton.setOnClickListener{
            val intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    class DBAsyncTask(val context: Context, val restaurantId : String) : AsyncTask<Void, Void, Boolean>(){

        private val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            db.cartDao().deleteOrders(restaurantId)
            db.close()
            return true
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }

}