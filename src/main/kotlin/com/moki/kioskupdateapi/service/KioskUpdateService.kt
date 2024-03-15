package com.moki.kioskupdateapi.service

import com.moki.kioskupdateapi.model.entity.*
import com.moki.kioskupdateapi.dto.*
import com.moki.kioskupdateapi.model.repository.*
import com.moki.kioskupdateapi.utils.*
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

/**
 * ### 키오스크 업데이트 비즈니스 로직 클래스
 *
 * @author donguk
 * @since 1.0.0
 */
@Service
class KioskUpdateService(
        private val kioskInfoRepository: KioskInfoRepository,
        private val businessInfoRepository: BusinessInfoRepository
) {

    /**
     * 해당 사업자 번호의 최신 APK 정보를 가져오거나, null을 반환합니다.
     *
     * 키오스크 종류를 불러올 수 없거나, 버전 정보를 불러올 수 없을 때 null이 반환됩니다.
     *
     * null 반환 상황에 대한 자세한 내용은 [getKioskTypeOrNull], [getKioskInfoOrNull] 주석을 참조하세요.
     *
     * @param businessNumber 사업자 번호
     * @return 키오스크 정보 or null
     */
    fun getLatestApkInfoOrNull(businessNumber: String): KioskInfoDto? {
        val kioskType = businessInfoRepository.findKioskTypeByBusinessNumber(businessNumber) ?: return null
        val kioskInfo = kioskInfoRepository.findByKioskType(kioskType)
        if(kioskInfo!=null){
            return kioskInfo.toDto()
        }
        return null;
    }

    /**
     * 문자열에 있는 BOM을 삭제합니다.
     *
     * @return BOM이 삭제된 문자열
     */
    private fun String.deleteBom(): String = this.replace("\uFEFF", "")

    /**
     * 해당 사업자의 APK 파일을 다운로드합니다.
     *
     * APK 파일이 없는 경우 null을 반환합니다.
     *
     * @param businessNumber 사업자 번호
     */
    fun getApkResourceOrNull(businessNumber: String): Resource? {
        val kioskType = businessInfoRepository.findKioskTypeByBusinessNumber(businessNumber) ?: return null
        return getApkResourceOfKioskTypeOrNull(kioskType)
    }

    /**
     * [kioskType]의 APK 파일의 리소스를 가져옵니다.
     *
     * APK 파일이 없는 경우 null을 반환합니다.
     *
     * @param kioskType 키오스크 타입
     * @return APK 리소스
     */
    fun getApkResourceOfKioskTypeOrNull(kioskType: String): Resource? {
        val kioskInfo = kioskInfoRepository.findByKioskType(kioskType)
        val apkPath:String
        if(kioskInfo==null){
            apkPath = FileConstant.getApkPathOrNull(kioskType, "") + FileConstant.APK_FILE_NAME
        }else {
            apkPath = FileConstant.getApkPathOrNull(kioskType, kioskInfo.version) + FileConstant.APK_FILE_NAME
        }
        return try {
            FileIOUtils.loadAsResource(apkPath)
        } catch (_: Exception) {
            throw IllegalStateException("현재 서버에서 해당 경로로 파일을 가져올수 없습니다.")
        }
    }



}