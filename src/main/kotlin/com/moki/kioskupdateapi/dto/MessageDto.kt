package com.moki.kioskupdateapi.dto

import org.springframework.web.bind.annotation.RequestMethod

data class MessageDto(
    val message: String,
    val redirectUri: String,
    val method: RequestMethod,
    val data: Map<String, Any>
)
