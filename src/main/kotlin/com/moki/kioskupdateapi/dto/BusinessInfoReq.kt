package com.moki.kioskupdateapi.dto

@kotlinx.serialization.Serializable
data class BusinessInfoReq(
    val businessNumber: String,
    val kioskType: String
)