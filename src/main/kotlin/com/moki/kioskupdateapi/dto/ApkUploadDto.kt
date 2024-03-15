package com.moki.kioskupdateapi.dto

import org.springframework.web.multipart.MultipartFile

@kotlinx.serialization.Serializable
data class ApkUploadDto(
    val kioskType: String,
    val release: String?,
    val file: MultipartFile,
    val version: String
) {
    fun toKioskInfoDto(packageName: String): KioskInfoDto {
        val release = this.release ?: "None"
        return KioskInfoDto(
            this.kioskType,
            this.version,
            packageName,
            release,
        )
    }

    fun isEmpty(): Boolean {
        return kioskType.isNullOrBlank() || version.isNullOrBlank()
    }
}