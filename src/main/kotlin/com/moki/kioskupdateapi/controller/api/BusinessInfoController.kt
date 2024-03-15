package com.moki.kioskupdateapi.controller.api

import com.moki.kioskupdateapi.dto.*
import com.moki.kioskupdateapi.service.BusinessInfoService
import com.moki.kioskupdateapi.service.KioskInfoService
import org.springframework.web.bind.annotation.*

/**
 * Business info 와 관련된 페이지를 다루는 컨트롤러
 *
 * @property businessInfoService 비즈니스 인포 서비스 클래스
 * @property kioskInfoService 키오스크 인포 서비스 클래스
 * @author jaehyeon
 */
@RestController
@RequestMapping("/api/business")
class BusinessInfoController(
        private val businessInfoService: BusinessInfoService,
        private val kioskInfoService: KioskInfoService
) {
    /**
     * business-info 전체 목록을 반환합니다.
     * RequestParam을 통해 사업자 번호또는 키오스크 타입으로 검색한 결과를 반환합니다.
     * url : http://localhost:8080/kiosk_update/admin/business/info?businessNumber={businessNumber}&kioskType={kioskType}
     * @param businessNumber 비즈니스 번호 (검색 필터)
     * @param kioskType 키오스크 타입 (검색 필터)
     */
    @GetMapping("/info")
    fun getBusinessInfos(
            @RequestParam("businessNumber", required = false, defaultValue = "") businessNumber: String,
            @RequestParam("kioskType", required = false, defaultValue = "") kioskType: String,
    ): Response<List<BusinessInfoResponse?>> {
        var businessInfos: List<BusinessInfoResponse?>
        if (!businessNumber.isEmpty()) {
            businessInfos = listOf(businessInfoService.getBusinessInfoByBusinessNumber(businessNumber))
        } else if (!kioskType.isEmpty()) {
            businessInfos = businessInfoService.getAllBusinessInfosByKioskType(kioskType)
        } else { //검색 조건 없는 경우
            businessInfos = businessInfoService.getAllBusinessInfosOrNull()
        }
        return Response.success(businessInfos)
    }

    /**
     * Business info를 생성하고 list 페이지로 리다이렉트 합니다.
     * @param businessInfoReq: 비즈니스 넘버, 키오스크 타입 가지는 DTO
     */
    @PostMapping("/info/create")
    fun createBusinessInfo(
            @RequestBody businessInfoReq: BusinessInfoReq,
    ): Response<BusinessInfoResponse> {
        try {
            val created = businessInfoService.createBusinessInfoOrNull(businessInfoReq)
            if (created == null) {
                throw IllegalArgumentException("비지니스 정보 생성에 실패했습니다.")
            }
            return Response.success(
                    code = 201,
                    result = created
            )
        } catch (e: IllegalArgumentException) {
            return Response.error(
                    code = 500,
                    message = e.message
            )
        }
    }

    /**
     * business-info 수정 후 리다이렉트 합니다.
     *
     * @param businessInfoId 비즈니스 인포 pk
     * @param kioskType 키오스크 타입명
     */
    @PutMapping("/{businessInfoId}")
    fun updateBusinessInfo(
            @PathVariable businessInfoId: Int,
            @RequestBody kioskType: String,
    ): Response<BusinessInfoResponse> {
        try {
            var updated = businessInfoService.updateBusinessInfoOrNull(businessInfoId, kioskType)
            return Response.success(
                    code = 201,
                    result = updated,
            )
        } catch (e: IllegalArgumentException) {
            return Response.error(
                    code = 500,
                    message = e.message
            )
        }
    }

    /**
     * business info 삭제 후 기존 필터 정보를 유지한 채로 리다이렉트 합니다.
     * 실패시에는 페이지를 유지하도록 리다이렉트 합니다.
     * @param businessInfoId 비즈니스 인포 pk
     */
    @DeleteMapping("/info/{businessInfoId}")
    fun deleteBusinessInfo(
            @PathVariable businessInfoId: Int
    ): Response<Int> {
        return try {
            businessInfoService.deleteBusinessInfo(businessInfoId)
            Response.success(
                    message = "성공적으로 삭제되었습니다",
                    result = businessInfoId)
        } catch (e: IllegalArgumentException) {
            //이미 삭제된 경우 예외 발생
            Response.error(
                    code = 404,
                    message = e.message
            )
        }
    }


}