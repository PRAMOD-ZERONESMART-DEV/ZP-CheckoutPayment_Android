import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.zerone.paymentcheckout.model.TransactionDetails
import com.zerone.paymentcheckout.model.User
import com.zerone.paymentcheckout.service.MyApiService
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MyViewModel() : ViewModel() {

    val user: MutableLiveData<User> = MutableLiveData()
    val transDetail: MutableLiveData<TransactionDetails> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var job: Job? = null


    fun generateToken(name: String, password: String, sToken: String) {
        // Check if there's an ongoing job and cancel it
        job?.cancel()

        // Create a new job for the current API request
        job = CoroutineScope(viewModelScope.coroutineContext).launch {
            try {
                isLoading.postValue(true)
                val apiService = GlobalUtils.createAuthService(name, password, sToken)
                val response = apiService.getAuth()
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    user.postValue(userResponse)
                } else {
                    val userResponse = response.errorBody()
                    if (userResponse != null) {
                        try {
                            // Convert the error response to a string
                            val jsonString = userResponse.string()
                            // Parse the JSON string into an ErrorResponse object
                            val gson = Gson()
                            val errorResponse = gson.fromJson(jsonString, User::class.java)
                            // Set the ErrorResponse object as the value of the user LiveData
                            user.postValue(errorResponse)
                            // Make sure to close the response body after using it
                            userResponse.close()
                        } catch (e: Exception) {
                            // Handle JSON parsing error
                        }
                    }
                }
            } catch (e: Exception) {
                user.postValue(null) // Set user LiveData to null in case of an exception
            } finally {
                isLoading.postValue(false) // Set loading state to false
            }
        }
    }

//    fun generateToken(name: String, password: String, sToken : String) {
//        viewModelScope.launch {
//            try {
//                isLoading.value = true
//                val apiService = GlobalUtils.createAuthService( name, password, sToken)
//                val response = apiService.getAuth()
//                if (response.isSuccessful) {
//                    val userResponse = response.body()
//                    user.postValue(userResponse)
//                } else {
//                    val userResponse = response.errorBody()
//
//                    if (userResponse != null) {
//                        try {
//                            // Convert the error response to a string
//                            val jsonString = userResponse.string()
//                            Log.i("TAG", "==>>   $jsonString")
//
//                            // Parse the JSON string into an ErrorResponse object
//                            val gson = Gson()
//                            val errorResponse = gson.fromJson(jsonString, User::class.java)
//                            Log.i("TAG", "====>>>>>>>>>   $errorResponse")
//
//                            // Set the ErrorResponse object as the value of the user LiveData
//                            user.postValue(errorResponse)
//
//                            // Make sure to close the response body after using it
//                            userResponse.close()
//                        } catch (e: Exception) {
//                            Log.e("TAG", "Error parsing JSON: ${e.message}")
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                user.postValue(null) // Set user LiveData to null in case of exception
//            } finally {
//                isLoading.value = false // Set loading state to false
//            }
//        }
//    }

    fun getProductDetails(auth: String, transId: JsonObject) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val apiService = GlobalUtils.createApiService()
                val response = apiService.getProductDetails(auth, transId)
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    user.postValue(userResponse)
                } else {
                    val userResponse = response.errorBody()

                    if (userResponse != null) {
                        try {
                            // Convert the error response to a string
                            val jsonString = userResponse.string()
                            Log.i("TAG", "==>>   $jsonString")

                            // Parse the JSON string into an ErrorResponse object
                            val gson = Gson()
                            val errorResponse = gson.fromJson(jsonString, User::class.java)
                            Log.i("TAG", "====>>>>>>>>>   $errorResponse")

                            // Set the ErrorResponse object as the value of the user LiveData
                            user.postValue(errorResponse)

                            // Make sure to close the response body after using it
                            userResponse.close()
                        } catch (e: Exception) {
                            Log.e("TAG", "Error parsing JSON: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                user.postValue(null) // Set user LiveData to null in case of exception
            } finally {
                isLoading.value = false // Set loading state to false
            }
        }
    }

    fun getTransactionStatus(auth: String, transId: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val apiService = GlobalUtils.createApiService()
                val response = apiService.getTransactionStatus(auth, transId)
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    transDetail.postValue(userResponse)
                } else {
                    val userResponse = response.errorBody()

                    if (userResponse != null) {
                        try {
                            // Convert the error response to a string
                            val jsonString = userResponse.string()
                            Log.i("TAG", "==>>   $jsonString")

                            // Parse the JSON string into an ErrorResponse object
                            val gson = Gson()
                            val errorResponse =
                                gson.fromJson(jsonString, TransactionDetails::class.java)
                            Log.i("TAG", "====>>>>>>>>>   $errorResponse")

                            // Set the ErrorResponse object as the value of the user LiveData
                            transDetail.postValue(errorResponse)

                            // Make sure to close the response body after using it
                            userResponse.close()
                        } catch (e: Exception) {
                            Log.e("TAG", "Error parsing JSON: ${e.message}")
                        }
                    } // Set user LiveData to null if user is not found
                }
            } catch (e: Exception) {
                e.printStackTrace()
                transDetail.postValue(null) // Set user LiveData to null in case of exception
            } finally {
                isLoading.value = false // Set loading state to false
            }
        }
    }
}
