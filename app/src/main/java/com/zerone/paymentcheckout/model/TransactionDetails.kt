package com.zerone.paymentcheckout.model

data class TransactionDetails(

    val statusCode: String,
    val message: String,
    val data : TransactionDetails,
    val _id: String,
    val merchant: String,
    val order: String,
    val gatewayTxnStatus: String,
    val merchantTradeNo: String,
    val currency: String,
    val paymentChannel: String,
    val paymentMode: String,
    val amount: String,
    val amountNum: Int,
    val state: String,
    val amountNotMatchedStatus: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val gatewayReferenceId: String,
    val gatewayTransactionId: String,
    val merchantRequestId: String,
    val payeeVPA: String,
    val payerName: String,
    val payerVPA: String,
    val transactionTimestamp: String,
    val type: String
)