package com.moki.kioskupdateapi.model.repository

import com.moki.kioskupdateapi.model.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface KioskInfoRepository : JpaRepository<KioskInfo, Int> {
    fun findByKioskType(kioskType: String): KioskInfo?
    override fun findAll(pageable: Pageable): Page<KioskInfo>

}