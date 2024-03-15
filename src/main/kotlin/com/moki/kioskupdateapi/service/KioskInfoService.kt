package com.moki.kioskupdateapi.service

import com.moki.kioskupdateapi.dto.*
import com.moki.kioskupdateapi.model.entity.KioskInfo
import com.moki.kioskupdateapi.model.repository.BusinessInfoRepository
import com.moki.kioskupdateapi.model.repository.KioskInfoRepository
import com.moki.kioskupdateapi.utils.FileConstant
import com.moki.kioskupdateapi.utils.FileIOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import javax.transaction.Transactional

@Service
class KioskInfoService(
        @Autowired val kioskInfoRepository: KioskInfoRepository,
        @Autowired val businessInfoRepository: BusinessInfoRepository
) {

    /**
     * 해당 키오스크 타입의 정보를 가져오거나, null을 반환합니다.
     *
     * null을 반환하는 상황
     * 1. 타입 - 정보 매핑 테이블 파일을 불러올 수 없거나, 없는(비어있는) 경우
     * 2. 매핑 테이블은 있으나, JSON 형식이 아닌경우
     * 3. 매핑 테이블은 있으나, 해당 타입의 정보가 없거나, 부족한 경우
     * @param kioskType 키오스크 타입
     * @return 키오스크 정보 or null
     */
    fun getKioskInfoOrNull(kioskType: String): KioskInfo {
        var kioskInfo = kioskInfoRepository.findByKioskType(kioskType)
        if (kioskInfo == null) throw IllegalArgumentException("해당 키오스크 정보를 찾을 수 없습니다.")
        return kioskInfo
    }

    fun getAllVersionAndNote(kioskType: String): List<VersionInfo> {
        val kioskTypeDir = FileConstant.getKioskTypePath(kioskType)
        val file = File(kioskTypeDir)
        if (file.list() == null) {
            println("filename을 읽을 수 없습니다.")
            return emptyList()
        } else {
            var versionInfos = mutableListOf<VersionInfo>()
            for (filename in file.list()) {
                versionInfos.add(VersionInfo(filename, FileIOUtils.getReleaseNote(kioskTypeDir + filename + "/")))
            }
            return versionInfos
        }
    }

    fun getAllKioskInfo(criteria: String, sort: String): List<KioskInfoDate> {
        return kioskInfoRepository.findAll().map {
            it.toDateDto()
        }
    }


    @Transactional
    fun updateKioskInfoOrNull(kioskType: String, kioskInfoDto: KioskInfoDto): KioskInfoDto? {
        kioskInfoRepository.findByKioskType(kioskType)?.let {
            it.update(kioskInfoDto)
            return it.toDto()
        }
        return null
    }

    /**
     * 새로운 키오스크 정보를 등록합니다.
     * version : zero, releaseNote : Noting 으로 저장됩니다.
     */
    @Transactional
    fun createKioskInfoOrNull(request: KioskInfoDto): KioskInfoDto? {
        val kioskInfo = KioskInfo(
                kioskType = request.kioskType,
                version = request.version,
                packageName = request.packageName,
                releaseNote = request.releaseNote
        )
        var file = File(FileConstant.getApkPath(request.kioskType, ""))
        Files.createDirectories(file.toPath())

        var unique = kioskInfoRepository.findByKioskType(request.kioskType)
        if (unique != null) return null

        kioskInfoRepository.save(kioskInfo).let {
            return it.toDto()
        }
        return null
    }


    @Transactional
    fun deleteKioskInfo(kioskType: String) {
        kioskInfoRepository.findByKioskType(kioskType)?.let { kioskInfo -> //DB삭제
            kioskInfo.kioskInfoId?.let {
                val businessInfos = businessInfoRepository.findAllByKioskInfo(kioskInfo)
                businessInfoRepository.deleteAll(businessInfos)
                kioskInfoRepository.delete(kioskInfo)
                //폴더 삭제
                FileIOUtils.cleanKioskTypeDir(kioskType)
            }
        } ?: throw IllegalArgumentException("존재하지 않는 KioskType입니다.")
    }

    @Transactional
    fun deleteKioskVersion(kioskType: String, version: String) {
        //TODO: 존재하지 않는 폴더명인 경우 예외 던지기
        //TODO: 존재하지 않는 키오스크 타입인 경우 예외 던지기
        kioskInfoRepository.findByKioskType(kioskType)?.let {
            FileIOUtils.cleanKioskVersionDir(kioskType, version)
        } ?: throw IllegalArgumentException("존재하지 않는 KioskType입니다.")
    }
}