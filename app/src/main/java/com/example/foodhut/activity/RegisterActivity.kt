package com.example.foodhut.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhut.R
import com.example.foodhut.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var edtRegisterName : EditText
    private lateinit var edtRegisterEmail : EditText
    private lateinit var edtRegisterMobileNumber : EditText
    private lateinit var edtRegisterDeliveryAddress : EditText
    private lateinit var edtRegisterPassword : EditText
    private lateinit var edtConfirmPassword : EditText

    private lateinit var registerButton : TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)

        toolbar = findViewById(R.id.registerToolbar)
        edtRegisterName = findViewById(R.id.edtRegisterName)
        edtRegisterEmail = findViewById(R.id.edtRegisterEmail)
        edtRegisterMobileNumber = findViewById(R.id.edtRegisterMobileNumber)
        edtRegisterDeliveryAddress = findViewById(R.id.edtRegisterDeliveryAddress)
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)

        registerButton = findViewById(R.id.btnRegister)

        setUpRegisterToolbar()

        registerButton.setOnClickListener{

            if(registerInputValidations()) {

                val queue = Volley.newRequestQueue(this@RegisterActivity)

                val url = "http://13.235.250.119/v2/register/fetch_result/"

                val jsonParams = JSONObject()

                jsonParams.put("name", edtRegisterName.text)
                jsonParams.put("mobile_number", edtRegisterMobileNumber.text)
                jsonParams.put("password", edtRegisterPassword.text)
                jsonParams.put("address", edtRegisterDeliveryAddress.text)
                jsonParams.put("email", edtRegisterEmail.text)

                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {

                    val jsonRequest =
                        object : JsonObjectRequest(Method.POST, url, jsonParams,
                            Response.Listener {
                                try {
                                    val dataObject = it.getJSONObject("data")
                                    val success = dataObject.getBoolean("success")

                                    if (success) {

                                        val jsonDataObject = dataObject.getJSONObject("data")

                                        sharedPreferences.edit().putBoolean("isLoggedIn" , true).apply()
                                        sharedPreferences.edit().putString("user_id", jsonDataObject.getString("user_id")).apply()
                                        sharedPreferences.edit().putString("name", jsonDataObject.getString("name")).apply()
                                        sharedPreferences.edit().putString("mobile_number", jsonDataObject.getString("mobile_number")).apply()
                                        sharedPreferences.edit().putString("address", jsonDataObject.getString("address")).apply()
                                        sharedPreferences.edit().putString("email", jsonDataObject.getString("email")).apply()

                                        sendUserToMainActivity()

                                    } else {

                                        val errMsg = dataObject.getString("errorMessage")

                                        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()

                                    }
                                } catch (e: JSONException) {

                                    Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()

                                }
                            },
                            Response.ErrorListener {

                                Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()

                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()

                                headers["Content-type"] = "application/json"
                                headers["token"] = "10ac7e21ebbf29"

                                return headers
                            }
                        }

                    queue.add(jsonRequest)

                } else {
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
    }

    private fun registerInputValidations() : Boolean {

        var result = false

        val name = edtRegisterName.text.toString()
        val mobileNumber = edtRegisterMobileNumber.text.toString()
        val email = edtRegisterEmail.text.toString()
        val address = edtRegisterDeliveryAddress.text.toString()
        val password = edtRegisterPassword.text.toString()
        val confirmPassword = edtConfirmPassword.text.toString()

        when {
            name.isEmpty() -> {
                Toast.makeText(this , "Please enter your name" , Toast.LENGTH_SHORT).show()
            }
            name.length < 3 -> {
                Toast.makeText(this , "Please enter your valid name" , Toast.LENGTH_SHORT).show()
            }
            email.isEmpty() -> {
                Toast.makeText(this , "Please enter your email" , Toast.LENGTH_SHORT).show()
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this , "Please enter a valid email address" , Toast.LENGTH_SHORT).show()
            }
            mobileNumber.isEmpty() -> {
                Toast.makeText(this , "Please enter your mobile number" , Toast.LENGTH_SHORT).show()
            }
            mobileNumber.length != 10 -> {
                Toast.makeText(this , "Please enter your valid mobile number" , Toast.LENGTH_SHORT).show()
            }
            address.isEmpty() -> {
                Toast.makeText(this , "Please enter your address" , Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(this , "Please enter your password" , Toast.LENGTH_SHORT).show()
            }
            password.length < 6 -> {
                Toast.makeText(this , "password should be  6 characters  long" , Toast.LENGTH_SHORT).show()
            }
            confirmPassword.isEmpty() -> {
                Toast.makeText(this , "Please enter your confirm password" , Toast.LENGTH_SHORT).show()
            }
            password != confirmPassword -> {
                Toast.makeText(this , "Password and ConfirmPassword doesn't matched" , Toast.LENGTH_SHORT).show()
                result = false
            }
            else -> {
                result = true
            }
        }

        return result
    }

    private fun sendUserToMainActivity() {
        Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show()
        val mainIntent = Intent(this , MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun setUpRegisterToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
