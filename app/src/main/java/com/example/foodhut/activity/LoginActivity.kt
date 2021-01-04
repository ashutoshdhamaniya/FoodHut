package com.example.foodhut.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodhut.R
import com.example.foodhut.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var edtLoginMobileNumber : EditText
    private lateinit var edtLoginPassword : EditText
    private lateinit var edtLoginButton : TextView
    private lateinit var forgetPasswordLink : TextView
    private lateinit var registerPageLink : TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn" , false)

        if(isLoggedIn){
            sendUserToMainActivity()
        }else{
            setContentView(R.layout.activity_login)
        }

        edtLoginMobileNumber = findViewById(R.id.edtLoginMobileNumber)
        edtLoginPassword = findViewById(R.id.edtLoginPassword)
        edtLoginButton = findViewById(R.id.btnLogin)
        forgetPasswordLink = findViewById(R.id.txtForgetPasswordPageLink)
        registerPageLink = findViewById(R.id.txtSignUpPageLink)

        forgetPasswordLink.setOnClickListener{
            sendUserToForgetActivity()
        }

        registerPageLink.setOnClickListener{
            sendUserToRegisterPage()
        }

        edtLoginButton.setOnClickListener {

            if(loginInputValidations()) {
                val queue = Volley.newRequestQueue(this@LoginActivity)

                val loginUrl = "http://13.235.250.119/v2/login/fetch_result/"

                val loginJsonParams = JSONObject()

                loginJsonParams.put("mobile_number", edtLoginMobileNumber.text.toString())
                loginJsonParams.put("password", edtLoginPassword.text.toString())

                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                    val loginJsonRequest = object : JsonObjectRequest(Method.POST, loginUrl, loginJsonParams,
                        Response.Listener
                        {

                            try {
                                val dataObject = it.getJSONObject("data")
                                val success = dataObject.getBoolean("success")

                                if (success) {

                                    Toast.makeText(this , "Login Successful" , Toast.LENGTH_SHORT).show()
                                    val data = dataObject.getJSONObject("data")

                                    sharedPreferences.edit().putBoolean("isLoggedIn" , true).apply()
                                    sharedPreferences.edit().putString("user_id", data.getString("user_id")).apply()
                                    sharedPreferences.edit().putString("name", data.getString("name")).apply()
                                    sharedPreferences.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                                    sharedPreferences.edit().putString("address", data.getString("address")).apply()
                                    sharedPreferences.edit().putString("email", data.getString("email")).apply()

                                    sendUserToMainActivity()

                                } else {
                                    val errorMsg = dataObject.getString("errorMessage")
                                    Toast.makeText(this, errorMsg , Toast.LENGTH_SHORT).show()

                                }
                            } catch (e: JSONException) {
                                Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }, Response.ErrorListener
                        {
                            Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_SHORT)
                                .show()
                        }) {

                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()

                            headers["Content-type"] = "application/json"
                            headers["token"] = "10ac7e21ebbf29"

                            return headers

                        }
                    }
                    queue.add(loginJsonRequest)
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

    private fun loginInputValidations() : Boolean{
        var result = false

        val mobileNumber = edtLoginMobileNumber.text.toString()
        val password = edtLoginPassword.text.toString()

        when {
            mobileNumber.isEmpty() -> {
                Toast.makeText(this , "Please enter your mobile number" , Toast.LENGTH_SHORT).show()
            }
            mobileNumber.length != 10 ->{
                Toast.makeText(this , "Please enter your valid mobile number" , Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(this , "Please enter your password" , Toast.LENGTH_SHORT).show()
            }
            password.length < 6 -> {
                Toast.makeText(this , "Password should be 6 characters long" , Toast.LENGTH_SHORT).show()
            }
            else -> {
                result  = true
            }
        }

        return result
    }

    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this , MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun sendUserToRegisterPage() {
        val registerIntent = Intent(this , RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    private fun sendUserToForgetActivity() {
        val forgetIntent = Intent(this , ForgetPasswordActivity::class.java)
        startActivity(forgetIntent)
    }

}
