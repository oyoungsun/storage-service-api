package com.moki.kioskupdateapi.model.repository

import com.moki.kioskupdateapi.model.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BusinessInfoRepository : JpaRepository<BusinessInfo, Int> {

    //FK를 가지는 쪽에서 함수 선언하는 것이 좋음
    @Query("SELECT k.kioskType FROM BusinessInfo b INNER JOIN b.kioskInfo k where b.businessNumber = :businessNumber")
    fun findKioskTypeByBusinessNumber(businessNumber: String): String?

    fun findByBusinessNumber(businessNumber: String): BusinessInfo?

    @Query("SELECT b FROM BusinessInfo b INNER JOIN b.kioskInfo k where k.kioskType = :kioskType order by b.businessNumber ASC")
    fun findAllByKioskType(kioskType: String): List<BusinessInfo>

    @Query("SELECT b FROM BusinessInfo b order by b.businessNumber ASC")
    override fun findAll(): List<BusinessInfo>

    fun findAllByKioskInfo(kioskInfo: KioskInfo): List<BusinessInfo>
}