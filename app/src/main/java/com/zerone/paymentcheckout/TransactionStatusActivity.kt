package com.zerone.paymentcheckout

import Globals
import MyViewModel
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.zerone.paymentcheckout.databinding.TranStatusLayoutBinding

class TransactionStatusActivity : AppCompatActivity() {

    private val viewModel: MyViewModel by viewModels()
    private lateinit var binding: TranStatusLayoutBinding
    private var isClickedOrder : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TranStatusLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

       val txnId = intent.getStringExtra("txnId")

        binding.progress.visibility = View.VISIBLE

        // Set onClickListener for the "Go Home" button
        val submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
            Toast.makeText(this,"Redirecting to home page...", Toast.LENGTH_SHORT).show()
            // Handle button click to go back to the main activity
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

       // makeOrderApi(Globals.AUTH_TOKEN, txnId)

        if (!txnId.isNullOrBlank()) {
            makeOrderApi(Globals.AUTH_TOKEN, txnId)
        }else{
            Toast.makeText(this,"Transaction id is ==>   $txnId", Toast.LENGTH_SHORT).show()
            binding.progress.visibility = View.GONE
        }

    }

    private fun makeOrderApi(token: String, txtId: String?) {

        if (txtId != null) {
            viewModel.getTransactionStatus("Bearer $token", txtId)

            // Observe the result
            viewModel.transDetail.observe(this) { transaction ->
                if (isClickedOrder){
                    isClickedOrder = false
                    if (transaction != null) {
                        binding. progress.visibility = View.GONE
                        if (transaction.statusCode == "200") {
                            val status = transaction.data.gatewayTxnStatus
                            binding.statusValue.text = status
                            binding.TransactionIdValue.text = "$transaction"
                            var _status = transaction.data.state
                            if (_status == "SUCCESS"){
                                binding.statusValue.setTextColor(Color.GREEN)
                                val resourceId = R.drawable.ok // Replace "your_image_name" with the name of your image resource
                                binding.statusImage.setImageResource(resourceId)
                            }else{
                                binding.statusValue.setTextColor(Color.RED)
                                val resourceId = android.R.drawable.ic_delete // Replace "your_image_name" with the name of your image resource
                                binding.statusImage.setImageResource(resourceId)
                            }

                        }else{
                            binding.statusValue.setTextColor(Color.RED)
                            val resourceId = android.R.drawable.ic_delete // Replace "your_image_name" with the name of your image resource
                            binding.statusImage.setImageResource(resourceId)
                            binding.statusValue.text = "Fail"
                            binding.TransactionIdValue.text = "${transaction.message}"
                            Toast.makeText(this,transaction.message, Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this,"Data not found", Toast.LENGTH_SHORT).show()
                        binding.progress.visibility = View.GONE
                        Log.i("TAG", "Data not found")
                    }

                }
            }
        }

    }

}
