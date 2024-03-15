package com.moki.kioskupdateapi.dto

@kotlinx.serialization.Serializable
data class VersionInfo (
        val version:String,
        val releaseNote:String,
)