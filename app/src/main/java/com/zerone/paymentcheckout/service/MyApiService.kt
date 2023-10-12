package com.zerone.paymentcheckout.service

import com.google.gson.JsonObject
import com.zerone.paymentcheckout.model.TransactionDetails
import com.zerone.paymentcheckout.model.User
import retrofit2.Response
import retrofit2.http.*

interface MyApiService {
    @GET("v1/token")
    suspend fun getAuth(): Response<User>

    @Headers("Content-Type: application/json")
    @POST("v1/merchant/order")
     suspend fun getProductDetails(
        @Header("Authorization") authorization: String,
        @Body user: JsonObject
    ): Response<User>


    @Headers("Content-Type: application/json")
    @GET("v1/transaction/details/{txnId}")
    suspend fun getTransactionStatus(
        @Header("Authorization") auth: String,
        @Path("txnId") txnId: String
    ): Response<TransactionDetails>
}
