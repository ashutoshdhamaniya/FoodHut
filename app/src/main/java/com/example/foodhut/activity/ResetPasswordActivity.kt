package com.example.foodhut.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
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

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var edtOtp : EditText
    private lateinit var edtNewPassword : EditText
    private lateinit var edtConfirmPassword : EditText
    private lateinit var txtSubmitButton : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        edtOtp = findViewById(R.id.edtResetPasswordOtp)
        edtNewPassword = findViewById(R.id.edtResetPasswordNewPassword)
        edtConfirmPassword = findViewById(R.id.edtResetPasswordConfirmPassword)
        txtSubmitButton = findViewById(R.id.btnSubmit)

        val mobileNumber = intent.getStringExtra("mobile_number")

        txtSubmitButton.setOnClickListener {

            if(resetPasswordInputValidations()){

                val submitQueue = Volley.newRequestQueue(this)

                val submitUrl = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number" , mobileNumber)
                jsonParams.put("password" , edtNewPassword.text)
                jsonParams.put("otp" , edtOtp.text)

                if(ConnectionManager().checkConnectivity(this)){

                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST , submitUrl , jsonParams ,
                        Response.Listener {

                            try{
                                val dataObject = it.getJSONObject("data")
                                val success = dataObject.getBoolean("success")

                                if(success){
                                    Toast.makeText(this , "Password changed successfully" , Toast.LENGTH_SHORT).show()
                                    val mainIntent = Intent(this , MainActivity::class.java)
                                    startActivity(mainIntent)
                                }else{
                                    val errMsg = dataObject.getString("errorMessage")
                                    Toast.makeText(this , errMsg , Toast.LENGTH_SHORT).show()
                                }
                            } catch (e:JSONException){
                                Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()
                            }

                        } , Response.ErrorListener {

                            Toast.makeText(this, "Some unexpected error occur please try again later", Toast.LENGTH_LONG).show()

                        })
                    {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String , String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "10ac7e21ebbf29"
                            return headers
                        }
                    }

                    submitQueue.add(jsonObjectRequest)
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

    }

    private fun resetPasswordInputValidations() : Boolean {

        var result = false

        val newPassword = edtNewPassword.text.toString()
        val confirmPassword = edtConfirmPassword.text.toString()
        val otp = edtOtp.text.toString()

        when {
            otp.isEmpty() -> {
                Toast.makeText(this , "Please enter the OTP" , Toast.LENGTH_SHORT).show()
            }
            newPassword.isEmpty() -> {
                Toast.makeText(this , "Please enter your password" , Toast.LENGTH_SHORT).show()
            }
            newPassword.length < 6 -> {
                Toast.makeText(this , "password should be  6 characters  long" , Toast.LENGTH_SHORT).show()
            }
            confirmPassword.isEmpty() -> {
                Toast.makeText(this , "Please enter your confirm password" , Toast.LENGTH_SHORT).show()
            }
            newPassword != confirmPassword -> {
                Toast.makeText(this , "Password and ConfirmPassword doesn't matched" , Toast.LENGTH_SHORT).show()
                result = false
            }
            else -> {
                result = true
            }
        }

        return result
    }
}