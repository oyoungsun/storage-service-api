package com.moki.kioskupdateapi.dto

data class EmailMessage (
    val to: String,
    val subject: String,
    val message: String,
)