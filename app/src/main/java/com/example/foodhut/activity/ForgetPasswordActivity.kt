package com.example.foodhut.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.widget.Button
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
import org.w3c.dom.Text

class ForgetPasswordActivity : AppCompatActivity() {

    lateinit var forgetMobileNumber : EditText
    private lateinit var forgetEmail : EditText
    private lateinit var nextButton : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        forgetMobileNumber = findViewById(R.id.edtForgetMobileNumber)
        forgetEmail = findViewById(R.id.edtForgetEmail)
        nextButton = findViewById(R.id.btnNext)

        nextButton.setOnClickListener {
            if(forgetInputValidations()){

                val forgetQueue = Volley.newRequestQueue(this)

                val forgetUrl = "http://13.235.250.119/v2/forgot_password/fetch_result"

                val forgetJsonParams = JSONObject()
                forgetJsonParams.put("mobile_number" , forgetMobileNumber.text)
                forgetJsonParams.put("email" , forgetEmail.text)

                if(ConnectionManager().checkConnectivity(this@ForgetPasswordActivity)){

                    val jsonObjectRequest = object : JsonObjectRequest(Method.POST , forgetUrl , forgetJsonParams ,
                        Response.Listener {

                            try{

                                val forgetData = it.getJSONObject("data")

                                val success = forgetData.getBoolean("success")

                                if(success){
                                    val resetPasswordIntent = Intent(this , ResetPasswordActivity::class.java)
                                    val mobileNumber = forgetMobileNumber.text.toString()
                                    resetPasswordIntent.putExtra("mobile_number" , mobileNumber)
                                    startActivity(resetPasswordIntent)
                                }else{
                                    val errMsg = forgetData.getString("errorMessage")
                                    Toast.makeText(this , errMsg , Toast.LENGTH_SHORT).show()
                                }
                            } catch (e : JSONException){

                                Toast.makeText(this , "Some unexpected error occur please try again later" , Toast.LENGTH_LONG).show()
                            }

                        }, Response.ErrorListener {

                            Toast.makeText(this , "Some unexpected error occur please try again later" , Toast.LENGTH_LONG).show()

                        }){

                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String , String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "10ac7e21ebbf29"
                            return headers
                        }

                    }

                    forgetQueue.add(jsonObjectRequest)
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

    private fun forgetInputValidations() : Boolean{
        var result = false

        val mobileNumber = forgetMobileNumber.text.toString()
        val email = forgetEmail.text.toString()

        when {
            mobileNumber.isEmpty() -> {
                Toast.makeText(this , "Please enter your mobile number" , Toast.LENGTH_SHORT).show()
            }
            mobileNumber.length != 10 ->{
                Toast.makeText(this , "Please enter your valid mobile number" , Toast.LENGTH_SHORT).show()
            }
            email.isEmpty() -> {
                Toast.makeText(this , "Please enter your email" , Toast.LENGTH_SHORT).show()
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this , "Please enter a valid email address" , Toast.LENGTH_SHORT).show()
            }
            else -> {
                result  = true
            }
        }

        return result
    }

}