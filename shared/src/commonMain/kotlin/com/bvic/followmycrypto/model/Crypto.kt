package com.bvic.followmycrypto.model

data class Crypto(
    val symbol: String,
    val name: String,
    val price: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val changePercent: Double,
)
