package com.zerone.paymentcheckout

import Globals
import MyViewModel
import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.zerone.paymentcheckout.databinding.GenerateAuthBinding
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


class GenerateAuthActivity : AppCompatActivity() {

    private lateinit var binding: GenerateAuthBinding
    private val viewModel: MyViewModel by viewModels()
    private var isClicked = false
    private var isPasswordShow = true


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GenerateAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //  setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // binding.passwordEditText.transformationMethod = PasswordTransformationMethod()

        binding.isProd.setOnCheckedChangeListener { _, isChecked ->
            Log.i("TAG", "checked==>  $isChecked");
            Globals.isProdEnable = isChecked
        }

        // Set a click listener for the submit button
        binding.submitButton.setOnClickListener {
            viewModel.job?.cancel();
            apiLoginRequest();
        }

        binding.showHidePassword.setOnClickListener {
            if (isPasswordShow) {
                binding.showHidePassword.setBackgroundResource(0)
                binding.showHidePassword.setBackgroundResource(R.drawable.show_password)
                binding.passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                binding.passwordEditText.setSelection(binding.passwordEditText.getText().length)
                isPasswordShow = false
            } else {
                binding.showHidePassword.setBackgroundResource(0)
                binding.showHidePassword.setBackgroundResource(R.drawable.hide_password)
                //etpass1.setInputType(InputType.TYPE_TEXT);
                binding.passwordEditText.setInputType(
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                )
                binding.passwordEditText.setSelection(binding.passwordEditText.getText().length)
                isPasswordShow = true
            }
        }

        binding.tokenText.setOnClickListener {
            val cm: ClipboardManager =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setText(binding.tokenText.getText())
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearText() {
        binding.tokenText.text = ""
        binding.tokenTitle.visibility = View.GONE
        binding.tokenText.visibility = View.GONE
        binding.passwordEditText.clearFocus()

    }

    private fun apiLoginRequest() {
        clearText()
        val name = binding.userNameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val pubKey = binding.pubkeyEditText.text.toString()
        isClicked = true
        // Validate the input fields
        if (name.isNotEmpty() && password.isNotEmpty() && pubKey.isNotEmpty()) {
            val currentTimeMillis = System.currentTimeMillis()
            val currentTimeInSeconds = currentTimeMillis / 1000
            var publicToken = encryptData("$password.$currentTimeInSeconds", pubKey)
            Log.i("time_seconds", "$password.$currentTimeInSeconds")
            if (publicToken != null) {
                publicToken = publicToken.replace("\n", "").replace(" ", "")
                println(publicToken)
            } else {
                println("Encryption failed.")
                isClicked = false
                return
            }

            binding.progress.visibility = View.VISIBLE
            viewModel.generateToken(name, password, publicToken)
            // Observe the result
            viewModel.user.observe(this) { user ->
                if (isClicked) {
                    isClicked = false
                    if (user != null) {
                        if (user.statusCode == "200") {
                            val token = user.data.token
                            Log.i("TAG", "token ==> $token")
                            // Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
                            binding.tokenText.text = token
                            binding.tokenTitle.visibility = View.VISIBLE
                            binding.tokenText.visibility = View.VISIBLE
                            val cm: ClipboardManager =
                                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.text = (binding.tokenText.text)

                            Globals.AUTH_TOKEN = token
                        } else {
                            clearText()
                            Toast.makeText(this, "${user.message}", Toast.LENGTH_SHORT).show()
                        }

//                val sp = getSharedPreferences("key", 0)
//                val sedt = sp.edit()
//                sedt.putString("token", tokenText.getText().toString())
//                sedt.commit()


                    } else {
                        clearText()
                        Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show()
                        binding.tokenText.visibility = View.GONE
                        binding.tokenTitle.visibility = View.GONE
                        Log.i("TAG", "401 Unauthorized")
                    }
                    binding.progress.visibility = View.GONE

                }
            }

        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun verifyGoogleReCAPTCHA() {

        SafetyNet.getClient(this)
            .verifyWithRecaptcha("6LdPma4nAAAAABBcdYKV34bCHNvNLxWcRFXDVmx0")
            .addOnSuccessListener(this, OnSuccessListener { response ->
                // Indicates communication with reCAPTCHA service was
                // successful.
                val userResponseToken = response.tokenResult
                Log.i("TAG", "token ====>>>>>>>>   $userResponseToken")
                // print("TAG  ===>>>   $userResponseToken");
                if (response.tokenResult?.isNotEmpty() == true) {
                    apiLoginRequest();
                    // Validate the user response token using the
                    // reCAPTCHA siteverify API.
                }
            })
            .addOnFailureListener(this, OnFailureListener { e ->
                if (e is ApiException) {
                    // An error occurred when communicating with the
                    // reCAPTCHA service. Refer to the status code to
                    // handle the error appropriately.
                    Log.d(
                        "TAG",
                        "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}"
                    )
                } else {
                    // A different, unknown type of error occurred.
                    Log.d("TAG", "Error: ${e.message}")
                }
            })
    }

    private fun encryptData(txt: String, publicKeyBase64: String): String? {
        return try {
            val publicBytes: ByteArray = Base64.decode(publicKeyBase64, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val pubKey: PublicKey = keyFactory.generatePublic(keySpec)
            val cipher: Cipher =
                Cipher.getInstance("RSA/ECB/PKCS1PADDING") // or "RSA" without padding
            cipher.init(Cipher.ENCRYPT_MODE, pubKey)
            val encrypted = cipher.doFinal(txt.toByteArray())
            Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception or return an error message, depending on your use case.
            null
        }
    }


}

