package com.moki.kioskupdateapi.model.entity

import com.moki.kioskupdateapi.dto.KioskInfoDto
import javax.persistence.*

@Entity
class KioskInfo(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val kioskInfoId: Int? = null,

        @Column(unique = true)
        var kioskType: String,
        var version: String,
        var packageName: String,
        var releaseNote: String
) : BaseTimeEntity() {
    fun update(kioskInfoDto: KioskInfoDto) {
        this.kioskType = kioskInfoDto.kioskType
        this.version = kioskInfoDto.version
        this.packageName = kioskInfoDto.packageName
        this.releaseNote = kioskInfoDto.releaseNote
    }
}