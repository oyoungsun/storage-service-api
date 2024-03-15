package com.moki.kioskupdateapi.controller.api

import com.moki.kioskupdateapi.dto.KioskInfoDto
import com.moki.kioskupdateapi.service.KioskUpdateService
import com.moki.kioskupdateapi.utils.FileConstant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriUtils
import java.nio.file.Paths

/**
 * ### 키오스크 업데이트 컨트롤러
 * 키오스크 업데이트 서비스 리턴 값을 사용자가 받을 수 있도록 [ResponseEntity]로 래핑한 후 리턴하는 컨트롤러입니다.
 *
 * @property kioskUpdateService 키오스크 업데이트 서비스 클래스
 * @author donguk, jaehyeon
 * @since 1.1.0
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class KioskUpdateController(
    @Autowired
    val kioskUpdateService: KioskUpdateService
) {

    /**
     * 해당 사업자 번호의 최신 APK 정보를 가져옵니다.
     *
     * @param businessNumber 사업자 번호
     */
    @GetMapping("/latest_apk_info")
    fun getLatestApkInfo(
        @RequestParam(required = false) businessNumber: String
    ): ResponseEntity<KioskInfoDto> {
        val kioskInfo = kioskUpdateService.getLatestApkInfoOrNull(businessNumber)

        return if (kioskInfo != null) {
            ResponseEntity
                .ok()
                .body(kioskInfo)
        } else {
            ResponseEntity
                .notFound() // code 404
                .build()
        }
    }


    /**
     * 해당 사업자의 APK 파일을 다운로드합니다.
     *
     * @param businessNumber 사업자 번호
     */
    @GetMapping("/download_apk")
    fun downloadApk(
        @RequestParam(required = true) businessNumber: String
    ): ResponseEntity<Resource> {
        val apkResource = kioskUpdateService.getApkResourceOrNull(businessNumber)
        //TODO : 최신 버전 이름 가져오기
        //TODO : url Type+version으로 변경하기
        return if (apkResource != null) {
            ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + apkResource.filename + "\"")
                .body(apkResource)
        } else {
            ResponseEntity
                .notFound() // code 404
                .build()
        }
    }
    /**
     * 웹 상에서 해당 버전의 APK를 다운로드합니다.
     *
     * @param KioskType, version : apkPath 생성용
     */
    @GetMapping("/kiosk/info/{kioskType}/{version}")
    fun getVersionApk(
            @PathVariable(required = false) kioskType: String,
            @PathVariable(required = false) version: String
    ) : ResponseEntity<Resource> {
        val apkPath = FileConstant.getApkPathOrNull(kioskType, version) + FileConstant.APK_FILE_NAME
        val resource : Resource = UrlResource(Paths.get(apkPath).toUri());
        return if (apkPath != null) {
            ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.filename?.let { UriUtils.encode(it, "UTF-8") } + "\"")
                    .body(resource)
        } else {
            ResponseEntity
                    .notFound() // code 404
                    .build()
        }
    }

}