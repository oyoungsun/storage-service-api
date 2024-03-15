package com.moki.kioskupdateapi.dto

import com.moki.kioskupdateapi.model.entity.KioskInfo

/**
 * ### 키오스크 정보 모델
 *
 * @author donguk
 * @property kioskType 키오스크 종류
 * @property version 버전
 * @property packageName 패키지 네임
 * @property releaseNote 릴리즈노트
 */
@kotlinx.serialization.Serializable
class KioskInfoDto(
        val kioskType: String,
        var version: String,
        val packageName: String,
        var releaseNote: String,
) {
    fun isEmpty(): Boolean {
        return kioskType.isNullOrBlank()||packageName.isNullOrBlank()
    }
}

fun KioskInfo.toDto() = KioskInfoDto(
    kioskType = this.kioskType,
    version = this.version,
    packageName = this.packageName,
    releaseNote = this.releaseNote
)
