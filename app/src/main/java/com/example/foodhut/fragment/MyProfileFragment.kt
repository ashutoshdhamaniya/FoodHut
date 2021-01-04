package com.example.foodhut.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.foodhut.R

class MyProfileFragment : Fragment() {

    private lateinit var myProfileName : TextView
    private lateinit var myProfileMobileNumber : TextView
    private lateinit var myProfileEmail : TextView
    private lateinit var myProfileAddress : TextView

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        myProfileName = view.findViewById(R.id.txtMyProfileName)
        myProfileMobileNumber = view.findViewById(R.id.txtMyProfileMobileNumber)
        myProfileEmail = view.findViewById(R.id.txtMyProfileEmail)
        myProfileAddress = view.findViewById(R.id.txtMyProfileAddress)

        sharedPreferences = this.activity!!.getSharedPreferences(getString(R.string.preferences_file_name) , Context.MODE_PRIVATE)

        myProfileName.text = sharedPreferences.getString("name" , " Ashutosh")
        myProfileMobileNumber.text = "91-" + sharedPreferences.getString("mobile_number" , "1234567890")
        myProfileEmail.text = sharedPreferences.getString("email" , "Ashutosh@gmail.com")
        myProfileAddress.text = sharedPreferences.getString("address" , "jaipur")

        return view
    }

}
