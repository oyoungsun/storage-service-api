package com.moki.kioskupdateapi.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.moki.kioskupdateapi.model.entity.KioskInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@kotlinx.serialization.Serializable
data class KioskInfoDate(
    val kioskType: String,
    val version: String,
    val packageName: String,
    val releaseNote: String,
    val modifiedAt: String?
)

fun KioskInfo.toDateDto() = KioskInfoDate(
    kioskType = this.kioskType,
    version = this.version,
    packageName = this.packageName,
    releaseNote = this.releaseNote,
    modifiedAt = this.modifiedAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
)
