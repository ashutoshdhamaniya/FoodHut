package com.example.foodhut.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.example.foodhut.R
import com.example.foodhut.adapter.HomeRecyclerAdapter
import com.example.foodhut.model.Restaurant
import com.example.foodhut.util.ConnectionManager
import kotlinx.android.synthetic.main.sorting_layout.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var recyclerAdapter: HomeRecyclerAdapter

    private lateinit var homeProgressLayout : RelativeLayout
    private lateinit var homeProgressBar : ProgressBar
    private lateinit var radioButton : View

    val restaurantInfoList = arrayListOf<Restaurant>()

    private var ratingComparator= Comparator<Restaurant> { rest1, rest2 ->

        if(rest1.restaurantRating.compareTo(rest2.restaurantRating,true)==0){
            rest1.restaurantName.compareTo(rest2.restaurantName,true)
        }
        else{
            rest1.restaurantRating.compareTo(rest2.restaurantRating,true)
        }

    }
    private var costComparator= Comparator<Restaurant> { rest1, rest2 ->

        rest1.restaurantCostPerPerson.compareTo(rest2.restaurantCostPerPerson)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        homeRecyclerView = view.findViewById(R.id.homeRecyclerView)
        layoutManager = LinearLayoutManager(activity)

        homeProgressLayout = view.findViewById(R.id.homeProgressLayout)
        homeProgressBar = view.findViewById(R.id.homeProgressBar)

        homeProgressLayout.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url ,  null , Response.Listener
            {
                try{
                    val dataObject = it.getJSONObject("data")
                    val success = dataObject.getBoolean("success")
                    if(success){
                        homeProgressLayout.visibility = View.GONE
                        val data = dataObject.getJSONArray("data")

                        for(i in 0 until data.length()){
                            val restaurantMenuJsonObject = data.getJSONObject(i)
                            val restaurantMenuObject = Restaurant(
                                restaurantMenuJsonObject.getString("id") ,
                                restaurantMenuJsonObject.getString("name") ,
                                restaurantMenuJsonObject.getString("rating") ,
                                restaurantMenuJsonObject.getString("cost_for_one") ,
                                restaurantMenuJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantMenuObject)
                            recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantInfoList)

                            homeRecyclerView.adapter = recyclerAdapter
                            homeRecyclerView.layoutManager = layoutManager
                        }
                    }
                    else{
                        homeProgressLayout.visibility = View.GONE

                        Toast.makeText(context, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT).show()
                    }
                }catch (e : JSONException){
                    homeProgressLayout.visibility = View.GONE
                    Toast.makeText(activity as Context, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT).show()
                }

            } , Response.ErrorListener {
                if(activity != null) {
                    homeProgressLayout.visibility = View.GONE
                    Toast.makeText(activity as Context, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT).show()
                }
            }){

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String , String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "10ac7e21ebbf29"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }else{
            homeProgressLayout.visibility = View.GONE
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_home , menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when(id) {

            R.id.action_sort -> {
                radioButton = View.inflate(context, R.layout.sorting_layout, null)
                androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                    .setTitle("Sort By?")
                    .setView(radioButton)
                    .setPositiveButton("OK") { _, _ ->
                        if (radioButton.rating.isChecked) {
                            Collections.sort(restaurantInfoList, ratingComparator)
                            restaurantInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        if (radioButton.lowToHigh.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        if (radioButton.highToLow.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            restaurantInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                        }
                    }
                    .setNegativeButton("CANCEL") { _, _ ->

                    }
                    .create()
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
