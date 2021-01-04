package com.example.foodhut.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.example.foodhut.R
import com.example.foodhut.adapter.OrderHistoryRecyclerAdapter
import com.example.foodhut.model.OrderDetails
import com.example.foodhut.model.RestaurantMenu
import com.example.foodhut.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {

    private lateinit var orderHistoryRecyclerView : RecyclerView
    private lateinit var orderHistoryRecyclerAdapter: OrderHistoryRecyclerAdapter
    private lateinit var orderHistoryProgressLayout : RelativeLayout
    private lateinit var orderHistoryProgressBar : ProgressBar

    private lateinit var emptyOrderHistoryLayout : RelativeLayout

    private lateinit var sharedPreferences: SharedPreferences

    val orderHistoryItemList = arrayListOf<OrderDetails>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_order_history, container, false)

        orderHistoryRecyclerView = view.findViewById(R.id.orderHistoryRecyclerView)
        orderHistoryProgressLayout = view.findViewById(R.id.orderHistoryProgressLayout)
        orderHistoryProgressBar = view.findViewById(R.id.orderHistoryProgressBar)
        emptyOrderHistoryLayout = view.findViewById(R.id.emptyOrderHistoryLayout)

        orderHistoryProgressLayout.visibility = View.VISIBLE

        sharedPreferences = this.activity!!.getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)

        val orderHistoryQueue = Volley.newRequestQueue(activity as Context)

        val userId = sharedPreferences.getString("user_id" , "0")


        if(ConnectionManager().checkConnectivity(activity as Context)){

            val orderHistoryUrl = "http://13.235.250.119/v2/orders/fetch_result/$userId"

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET , orderHistoryUrl , null ,
                Response.Listener {

                    try{
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            orderHistoryProgressLayout.visibility = View.GONE
                            val resArray = data.getJSONArray("data")

                            if (resArray.length() == 0) {
                                emptyOrderHistoryLayout.visibility = View.VISIBLE
                            } else {
                                for (i in 0 until resArray.length()) {

                                    val orderObject = resArray.getJSONObject(i)
                                    val foodItems = orderObject.getJSONArray("food_items")

                                    val orderDetails = OrderDetails(
                                        orderObject.getInt("order_id"),
                                        orderObject.getString("restaurant_name"),
                                        orderObject.getString("order_placed_at"),
                                        foodItems
                                    )
                                    orderHistoryItemList.add(orderDetails)
                                    if (orderHistoryItemList.isEmpty()) {

                                        orderHistoryProgressLayout.visibility = View.VISIBLE
                                    } else {

                                        emptyOrderHistoryLayout.visibility = View.GONE
                                        if (activity != null) {
                                            orderHistoryRecyclerAdapter = OrderHistoryRecyclerAdapter(
                                                activity as Context,
                                                orderHistoryItemList
                                            )
                                            val mLayoutManager =
                                                LinearLayoutManager(activity as Context)
                                            orderHistoryRecyclerView.layoutManager = mLayoutManager
                                            orderHistoryRecyclerView.itemAnimator = DefaultItemAnimator()
                                            orderHistoryRecyclerView.adapter = orderHistoryRecyclerAdapter
                                        }
                                    }
                                }
                            }
                        }else{

                            orderHistoryProgressLayout.visibility = View.GONE
                            Toast.makeText(context, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT).show()

                        }
                    } catch (e:JSONException){

                        orderHistoryProgressLayout.visibility = View.GONE
                        Toast.makeText(context, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT).show()

                    }

                } , Response.ErrorListener {

                    orderHistoryProgressLayout.visibility = View.GONE
                    Toast.makeText(context, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT).show()

                })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String , String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "10ac7e21ebbf29"
                    return headers
                }
            }

            orderHistoryQueue.add(jsonObjectRequest)
        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Setting"){ _, _ ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){_ , _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

}
