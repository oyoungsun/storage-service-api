package com.moki.kioskupdateapi.service

import com.moki.kioskupdateapi.dto.BusinessInfoReq
import com.moki.kioskupdateapi.dto.BusinessInfoResponse
import com.moki.kioskupdateapi.dto.toDto
import com.moki.kioskupdateapi.model.entity.BusinessInfo
import com.moki.kioskupdateapi.model.repository.BusinessInfoRepository
import com.moki.kioskupdateapi.model.repository.KioskInfoRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class BusinessInfoService(
        private val businessInfoRepository: BusinessInfoRepository,
        private val kioskInfoRepository: KioskInfoRepository
) {

    @Transactional
    fun createBusinessInfoOrNull(businessInfoReq: BusinessInfoReq): BusinessInfoResponse {
        //하나라도 비어있으면 예외 던짐
        if (businessInfoReq.businessNumber.equals("") || businessInfoReq.kioskType.equals("")) {
            throw IllegalArgumentException("빈 값은 전송할 수 없습니다.")
        }

        //kioskType이 존재하는지 확인, 없으면 예외 던짐
        val kioskInfoOrNull = kioskInfoRepository.findByKioskType(businessInfoReq.kioskType)
        if (kioskInfoOrNull == null) {
            throw IllegalArgumentException("존재하지 않는 키오스크 타입입니다.")
        }

        //business number가 존재하는지 확인, 중복이면 예외 던짐
        val businessInfoOrNull = businessInfoRepository.findByBusinessNumber(businessInfoReq.businessNumber)
        if (businessInfoOrNull != null) {
            throw IllegalArgumentException("이미 존재하는 사업자 번호입니다.")
        }

        val businessInfo = BusinessInfo(
                businessNumber = businessInfoReq.businessNumber,
                kioskInfo = kioskInfoOrNull
        )
        return businessInfoRepository.save(businessInfo).toDto()
    }

    @Transactional
    fun getAllBusinessInfosOrNull(): List<BusinessInfoResponse> {
        return businessInfoRepository.findAll().map { it.toDto() }
    }


    @Transactional
    fun deleteBusinessInfo(businessInfoId: Int): Int {
        val targetInfo = businessInfoRepository.findById(businessInfoId)
        if (targetInfo.isEmpty) {
            throw IllegalArgumentException("해당 정보는 존재하지 않습니다.")
        }
        businessInfoRepository.delete(targetInfo.get())
        return businessInfoId
    }

    @Transactional
    fun updateBusinessInfoOrNull(businessInfoId: Int, kioskType: String): BusinessInfoResponse {
        //kioskType이 존재하는지 확인 후 예외
        val newKioskInfo = kioskInfoRepository.findByKioskType(kioskType)
        if (newKioskInfo == null) {
            throw IllegalArgumentException("존재하지 않는 키오스크 타입입니다. type 이름 : ${kioskType}")
        }

        //유효한 id인지 확인 후 예외
        val targetBusinessInfo = businessInfoRepository.findById(businessInfoId)
        if (targetBusinessInfo.isEmpty) {
            throw IllegalArgumentException("존재하지 않는 사업자 정보입니다.")
        }

        val updatedInfo = targetBusinessInfo.get().apply {
            kioskInfo = newKioskInfo
        }
        return updatedInfo.toDto()
    }

    @Transactional
    fun getBusinessInfoByBusinessNumber(businessNumber: String): BusinessInfoResponse? {
        var business = businessInfoRepository.findByBusinessNumber(businessNumber)?.toDto()
        if (business == null) {

        }
        return business
    }

    @Transactional
    fun getAllBusinessInfosByKioskType(kioskType: String): List<BusinessInfoResponse> {
        return businessInfoRepository.findAllByKioskType(kioskType).map { it.toDto() }
    }


}