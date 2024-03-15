package com.moki.kioskupdateapi.controller.api

import com.moki.kioskupdateapi.dto.ApkUploadDto
import com.moki.kioskupdateapi.dto.KioskInfoDto
import com.moki.kioskupdateapi.dto.Response
import com.moki.kioskupdateapi.service.KioskInfoService
import com.moki.kioskupdateapi.utils.FileConstant
import com.moki.kioskupdateapi.utils.FileIOUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

@RestController
@RequestMapping("/api")
class KioskUploadController(
        private val kioskInfoService: KioskInfoService,
        private val executorService: ExecutorService,
) {

    @PostMapping("/upload")
    fun uploadApk(
            member: ApkUploadDto,
    ): Response<KioskInfoDto> {

        try {
            if (member.file.isEmpty) {
                throw IllegalStateException("파일이 비어있습니다.")
            }
            if (member.isEmpty()) {
                throw IllegalStateException("타입명, 버전명은 비워둘 수 없습니다")
            }
            val allVersionAndNote = kioskInfoService.getAllVersionAndNote(member.kioskType)
            val allVersions = allVersionAndNote.map { it.version }
            if (allVersions.contains(member.version)) {
                throw IllegalStateException("동일한 버전은 생성할 수 없습니다.")
            }
            //version 파일 생성
            val uploadDir = FileConstant.getApkPath(member.kioskType, member.version)
            FileIOUtils.createVersionDir(uploadDir)

            val futures = listOf(
                    CompletableFuture.runAsync({
                        try {
                            uploadApk(uploadDir, member.file)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, executorService),

                    CompletableFuture.runAsync({
                        try {
                            FileIOUtils.createReleaseNote(uploadDir, member.release)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, executorService),
                    CompletableFuture.runAsync({
                        try {
                            val kioskInfo = kioskInfoService.getKioskInfoOrNull(member.kioskType)
                            val updated = kioskInfoService.updateKioskInfoOrNull(
                                    member.kioskType, member.toKioskInfoDto(kioskInfo.packageName)
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, executorService)
            )

            // 모든 CompletableFuture가 완료될 때까지 대기
            CompletableFuture.allOf(*futures.toTypedArray()).join()
            println("응답 return 시작")
            return Response.success(
                    code = 201,
                    result = null//updated,
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

    private fun uploadApk(uploadDir: String, file: MultipartFile) {
        println("Uploading : ${file.getOriginalFilename()}")
        val destFile = File(uploadDir + "app.apk")
        file.transferTo(destFile)
        println("Uploaded : ${file.getOriginalFilename()}")

    }
}