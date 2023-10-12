package com.zerone.paymentcheckout.model

data class User(
    val statusCode: String,
    val message: Any,
    val error : String,
    val data: User,
    val token: String,
    val  checkout_page : String

)
