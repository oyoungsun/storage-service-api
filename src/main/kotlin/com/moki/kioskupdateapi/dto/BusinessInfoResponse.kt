package com.moki.kioskupdateapi.dto

import com.moki.kioskupdateapi.model.entity.BusinessInfo

@kotlinx.serialization.Serializable
data class BusinessInfoResponse(
    val businessInfoId: Int,
    val businessNumber: String,
    val kioskType: String,
)

fun BusinessInfo.toDto() = BusinessInfoResponse(
    businessInfoId = this.businessInfoId!!,
    businessNumber = this.businessNumber,
    kioskType = this.kioskInfo.kioskType
)