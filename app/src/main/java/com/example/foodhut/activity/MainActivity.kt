package com.example.foodhut.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.inflate
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodhut.R
import com.example.foodhut.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView

    private var previousMenuItem : MenuItem ?= null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.mainActivityToolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)

        val headerView : View = navigationView.getHeaderView(0)
        val drawerHeaderUserName : TextView = headerView.findViewById(R.id.txtDrawerHeaderUserName)
        val drawerHeaderUserMobileNumber : TextView = headerView.findViewById(R.id.txtDrawerHeaderUserMobileNumber)

        drawerHeaderUserName.text = sharedPreferences.getString("name" , "")
        drawerHeaderUserMobileNumber.text = "+91-${sharedPreferences.getString("mobile_number" , "")}"

        setUpMainToolbar()

        openHome()

        // For adding hamburger icon
        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity , drawerLayout , R.string.open_drawer , R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.menuHome -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        MyProfileFragment()
                    ).commit()
                    drawerLayout.closeDrawers()

                    supportActionBar?.title = "My Profile"
                }
                R.id.favouriteRestaurants -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FavouriteRestaurantsFragment()
                    ).commit()
                    drawerLayout.closeDrawers()

                    supportActionBar?.title = "Favourite Restaurants"
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        OrderHistoryFragment()
                    ).commit()
                    drawerLayout.closeDrawers()

                    supportActionBar?.title = "Order History"
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FAQsFragment()
                    ).commit()
                    drawerLayout.closeDrawers()

                    supportActionBar?.title = "FAQs"
                }
                R.id.logOut -> {

                    drawerLayout.closeDrawers()

                    val logoutDialog = android.app.AlertDialog.Builder(this)
                    logoutDialog.setTitle("Confirmation")
                    logoutDialog.setMessage("Are you sure you want to log out ?")
                    logoutDialog.setPositiveButton("Yes") { _, _ ->

                        sharedPreferences.edit().putBoolean("isLoggedIn" , false).apply()
                        ActivityCompat.finishAffinity(this)
                        sendUserToLoginActivity()

                    }
                    logoutDialog.setNegativeButton("No") { _, _ ->

                        openHome()

                    }

                    logoutDialog.create()
                    logoutDialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }

    }

    private fun sendUserToLoginActivity() {
        val loginIntent = Intent(this@MainActivity , LoginActivity::class.java)
        startActivity(loginIntent)
    }

    private fun setUpMainToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // For making hamburger icon functional
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openHome(){
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment).commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.menuHome)
    }

    override fun onBackPressed() {

        when(supportFragmentManager.findFragmentById(R.id.frameLayout)){
            !is HomeFragment -> openHome()

            else -> {
                ActivityCompat.finishAffinity(this)
                super.onBackPressed()
            }
        }
    }
}
