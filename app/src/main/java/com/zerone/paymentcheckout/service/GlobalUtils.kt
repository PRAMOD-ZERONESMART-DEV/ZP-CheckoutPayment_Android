import android.content.Context
import com.zerone.paymentcheckout.R
import com.zerone.paymentcheckout.service.BasicAuthInterceptor
import com.zerone.paymentcheckout.service.MyApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GlobalUtils {
    private const val BASE_URL = "https://api-uat.zeronepay.com/zerone-pay/"
    private const val PROD_BASE_URL = "https://api.zeronepay.com/pg/"

    object RetrofitClient {

        fun createRetrofit(name: String, password: String, sToken: String): Retrofit {

            val basicAuthInterceptor = BasicAuthInterceptor(name, password, sToken)

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(basicAuthInterceptor)
                .build()

            return if (Globals.isProdEnable) {
                Retrofit.Builder()
                    .baseUrl(GlobalUtils.PROD_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()
            } else {
                Retrofit.Builder()
                    .baseUrl(GlobalUtils.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()
            }

//            return Retrofit.Builder()
//                .baseUrl(BASE_URL)
//
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient)
//                .build()
        }
    }

    fun createApiService(): MyApiService {


        return if (Globals.isProdEnable) {
            val retrofit = Retrofit.Builder()
                .baseUrl(PROD_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            // Create an instance of the MyApiService interface
            return retrofit.create(MyApiService::class.java)
        } else {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            // Create an instance of the MyApiService interface
            return retrofit.create(MyApiService::class.java)
        }

    }

    fun createAuthService(name: String, password: String, sToken: String): MyApiService {
        val retrofit = RetrofitClient.createRetrofit(name, password, sToken)
        return retrofit.create(MyApiService::class.java)
    }
}
