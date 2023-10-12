package com.zerone.paymentcheckout

import Globals
import MyViewModel
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.zerone.paymentcheckout.databinding.TransactionFormBinding
import java.util.*


class TransactionFormActivity : AppCompatActivity() {

    private lateinit var binding: TransactionFormBinding
    private var isClickedOrder = false

    private val viewModel: MyViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TransactionFormBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //  for sharedpref
//        val sp = getSharedPreferences("key", 0)
//        val tValue = sp.getString("textvalue", "")
//        val tOperative = sp.getString("txtopertaive", "")

        //auto generate number
        //val randomNumber = generateRandom12DigitNumber()
        val tradNumber = Globals.generateUnique16DigitNumber()
        binding.tradeNumberEditText.setText(tradNumber)

        //set default null
        Globals.RETURN_URL = ""

        if (!TextUtils.isEmpty(Globals.AUTH_TOKEN)) {
            binding.authTokenEditText.setText(Globals.AUTH_TOKEN)
        }

        binding.amountEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateAmount(v.text.toString())
                true
            } else {
                false
            }
        }

        binding.amountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateAmount(s?.toString() ?: "")
            }
        })

        // Set a click listener for the submit button
        binding.submitButton.setOnClickListener {

            val authToken = binding.authTokenEditText.text.toString()
            val returnUrl = binding.returnUrlEditText.text.toString()
            val callbackUrl = binding.callbackUrlEditText.text.toString()
            val amount = binding.amountEditText.text.toString().toFloatOrNull()
            val tradeNumber = binding.tradeNumberEditText.text.toString()
            val mobile = binding.mobileEditText.text.toString()
            val email = binding.emailEditText.text.toString()

            val roundoffAmount = String.format("%.2f", amount)
            Log.i("TAG", "===>  " + roundoffAmount)

            isClickedOrder = true
            // Validate the input fields
            if (authToken.isNotEmpty() && returnUrl.isNotEmpty() && callbackUrl.isNotEmpty() && amount != null && tradeNumber.isNotEmpty() && mobile.isNotEmpty() && email.isNotEmpty()) {

                if (Globals.isEmailValid(email)) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("returnUrl", returnUrl)
                    jsonObject.addProperty("callBackUrl", callbackUrl)
                    jsonObject.addProperty("currency", "INR")
                    jsonObject.addProperty("amount", roundoffAmount)
                    jsonObject.addProperty("tradeNumber", tradeNumber)
                    jsonObject.addProperty("mobileNo", mobile)
                    jsonObject.addProperty("email", email)
                    //set for transaction check
                    Globals.RETURN_URL = returnUrl
                    binding.progress.visibility = View.VISIBLE
                    makeOrderApi(authToken, jsonObject)
                } else {
                    Toast.makeText(this, "Please enter valid email id.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun validateAmount(input: String) {
        if (isValidAmount(input)) {
            // Valid amount with up to two digits after decimal
            // You can perform further actions here if needed
        } else {

            binding.amountEditText.error =
                "Invalid input. Please enter up to two digits after the decimal point."
        }
    }

    private fun isValidAmount(input: String): Boolean {
        return input.matches(Regex("^\\d+(\\.\\d{0,2})?$"))
    }


    override fun onBackPressed() {
        // Handle the back button click event
        super.onBackPressed()
    }

    private fun makeOrderApi(token: String, data: JsonObject) {

        viewModel.getProductDetails("Bearer $token", data)
        // Observe the result
        viewModel.user.observe(this) { user ->
            if (isClickedOrder) {
                isClickedOrder = false
                if (user != null) {
                    val code = user.statusCode

                    if (code == "201") {
                        binding.progress.visibility = View.GONE
                        val url = user.data.checkout_page

                        //for browser
//                        Log.i("TAG", "===>>>>>>  $url");
//                        val browserIntent =
//                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                        startActivity(browserIntent)

                        if (!TextUtils.isEmpty(url)){
                            val intent = Intent(this, WebViewActivity::class.java)
                             intent.putExtra("url", url)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,"url not found!",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.progress.visibility = View.GONE
                        try {
                            val _message = user.message
                            Log.i("TAG", "$_message")
                            Toast.makeText(this, "$_message", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            val _message = user.message
                            Toast.makeText(this, "$_message", Toast.LENGTH_SHORT).show()
                        }

                    }

                } else {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(this, "Data is empty", Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "user empty")
                }

            }
        }
    }

}



