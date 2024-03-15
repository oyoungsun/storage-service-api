package com.moki.kioskupdateapi.controller.api

import com.moki.kioskupdateapi.dto.*
import com.moki.kioskupdateapi.service.KioskInfoService
import com.moki.kioskupdateapi.utils.FileConstant
import com.moki.kioskupdateapi.utils.FileIOUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
@RequestMapping("/api/kiosk")
class KioskInfoController(
        private val kioskInfoService: KioskInfoService,
) {
    //TODO: 키오스크 타입 반환 api 작성

    /**
     * Kiosk info 리스트를 조회합니다.
     * @param criteria : 수정일 기준(modifiedAt)으로 입력 가능, 확장 가능성으로 열어둠
     * @param sort : 내림차순(DESC), 오름차순(ASC)로 입력 가능
     */
    @GetMapping("/info")
    fun getKioskInfos(
            @RequestParam(required = false, defaultValue = "modifiedAt", value = "orderby") criteria: String,
            @RequestParam(required = false, defaultValue = "DESC", value = "sort") sort: String
    ): Response<List<KioskInfoDate>> {
        val kioskInfos = kioskInfoService.getAllKioskInfo(criteria, sort)
        return Response.success(kioskInfos)
    }

    /**
     * Kiosk info를 생성합니다
     * @ModelAttribute member: KioskInfoDto
     */
    @PostMapping("/info")
    fun createKioskInfo(
            dto: KioskInfoDto,
            file: MultipartFile,
    ): Response<KioskInfoDto> {
        val saved = kioskInfoService.createKioskInfoOrNull(dto)
        if (saved == null) {
            val errorMessage = "이미 있는 키오스크 타입입니다."
            return Response.error(
                    code = 400,
                    message = errorMessage,
            )
        }
        try {
            if (file.isEmpty) {
                throw IllegalStateException("파일이 비어있습니다.")
            }
            if (dto.isEmpty()) {
                throw IllegalStateException("타입 이름과 패키지명은 비워둘 수 없습니다.")
            }
            //version 파일 생성
            val uploadDir = FileConstant.getApkPath(dto.kioskType, dto.version)
            FileIOUtils.createVersionDir(uploadDir)
            //app.apk 업로드
            val destFile = File(uploadDir + "app.apk")
            file.transferTo(destFile)
            //releaseNoto.txt 작성
            FileIOUtils.createReleaseNote(uploadDir, dto.releaseNote)
            return Response.success(
                    code = 201,
                    result = saved
            )
        } catch (e: IllegalStateException) {
            return Response.error(
                    code = 400,
                    message = e.message
            )
        } catch (e: Exception) {
            return Response.error(
                    code = 500,
                    message = e.message
            )
        }

    }

    /**
     * Kiosk info detail(상세) 페이지로 이동합니다.
     * Kiosk Type별 version목록을 확인합니다.
     */
    @GetMapping("info/{kioskType}")
    fun getKioskInfoDetail(
            @PathVariable kioskType: String,
    ): Response<KioskDetailDto> {
        var versionInfos = kioskInfoService.getAllVersionAndNote(kioskType)
        var kioskInfo = kioskInfoService.getKioskInfoOrNull(kioskType)
        if (versionInfos == null || kioskInfo == null) {
            val errorMessage = "현재 서버에 해당 키오스크 타입 폴더가 존재하지 않습니다."
            return Response.error(
                    code = 404,
                    message = errorMessage,
            )
        }
        return Response.success(
                KioskDetailDto(
                        kioskInfo,
                        versionInfos = versionInfos,
                )
        )
    }


    /**
     * Kiosk info Detail - kioskType의 상세 버전 정보를 수정한다.
     * kiosk-info-detail의 [ 메인으로 ] 버튼과 매핑됨
     * kiosk-info-detail의 릴리즈노트 [ 저장하기 ] 버튼과 매핑됨
     */
    @PutMapping("change-version/{kioskType}")
    fun updateKioskInfoDetail(
            @PathVariable kioskType: String,
            @RequestBody request: KioskInfoDto
    ): Response<KioskInfoDto> {
        val kioskInfoDto = kioskInfoService.getKioskInfoOrNull(kioskType).toDto()
        if (kioskInfoDto == null) {
            val errorMessage = "해당 키오스크 타입이 존재하지 않습니다."
            return Response.error(
                    code = 404,
                    message = errorMessage,
            )
        }
        val typeVerDir = FileConstant.getApkPathOrNull(kioskType, request.version)
        if (typeVerDir == null) {
            val errorMessage = "경로가 유효하지 않습니다.(해당 경로 폴더가 존재하지 않거나, 폴더명, 키오스크명은 반드시 입력되어야합니다)"
            return Response.error(
                    code = 404,
                    message = errorMessage,
            )
        }
        //[저장하기] -> 릴리즈 노트 수정
        val mainVersion = kioskInfoDto.version
        if (!request.releaseNote.isNullOrBlank()) {
            if (!mainVersion.isNullOrEmpty() && mainVersion.equals(request.version)) {
                kioskInfoDto.releaseNote = request.releaseNote //메인 version의 수정은 DB에도 함께 반경
                kioskInfoService.updateKioskInfoOrNull(kioskType, kioskInfoDto)
            }
            FileIOUtils.createReleaseNote(typeVerDir, request.releaseNote)
            return Response.success(
                    code = 201,
                    result = kioskInfoDto
            )
        }
        //[메인으로] -> 버전 수정
        var str: String = FileIOUtils.getReleaseNote(typeVerDir)
        kioskInfoDto.version = request.version
        kioskInfoDto.releaseNote = str
        val updatedKioskInfo = kioskInfoService.updateKioskInfoOrNull(kioskType, kioskInfoDto)
        return if (updatedKioskInfo != null) {
            return Response.success(
                    code = 201,
                    result = updatedKioskInfo
            )
        } else {
            val errorMessage = "버전 변경 중 문제가 발생했습니다."
            return Response.error(
                    code = 500,
                    message = errorMessage,
            )
        }
    }

    @DeleteMapping("/info/{kioskType}")
    fun deleteKioskInfo(
            @PathVariable kioskType: String
    ): Response<String> {
        return try {
            kioskInfoService.deleteKioskInfo(kioskType)
            Response.success(kioskType)
        } catch (e: IllegalArgumentException) {
            Response.error(
                    code = 400,
                    message = e.message
            )
        } catch (e: Exception) {
            val errorMessage = "삭제 중 문제가 발생했습니다."
            Response.error(
                    code = 500,
                    message = errorMessage
            )
        }
    }

    @DeleteMapping("/info/{kioskType}/{version}")
    fun deleteKioskVersion(
            @PathVariable kioskType: String,
            @PathVariable version: String
    ): Response<String> {
        //해당 버전 폴더 삭제
        try {
            kioskInfoService.deleteKioskVersion(kioskType = kioskType, version = version)
            return Response.success(version)
        } catch (e: IllegalArgumentException) {
            return Response.error(
                    code = 400,
                    message = e.message
            )
        }
    }


}