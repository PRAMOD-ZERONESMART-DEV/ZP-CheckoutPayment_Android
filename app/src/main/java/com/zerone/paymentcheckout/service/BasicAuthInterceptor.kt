package com.zerone.paymentcheckout.service

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BasicAuthInterceptor(
    private val username: String,
    private val password: String,
    private val sToken: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = Credentials.basic(username, password)
        val request = chain.request().newBuilder()
            .header("Authorization", credentials)
            .header("x-zp-signature", sToken)
            .build()
        return chain.proceed(request)
    }
}


