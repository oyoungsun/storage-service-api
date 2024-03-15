package com.moki.kioskupdateapi.utils

import java.io.File
import java.nio.file.Paths

/**
 * ### 파일 관련 상수 객체
 *
 * @author donguk
 */
object FileConstant {
    const val BUSINESS_NUMBER_MAPPING_TABLE_FILE_NAME = "business_number_mapping_table.csv"
    const val KIOSK_INFO_MAPPING_TABLE_FILE_NAME = "kiosk_info_mapping_table.json"
    const val APK_FILE_NAME = "app.apk"

    /**
     * 데이터 경로를 가져옵니다.
     *
     */
    fun getDataPath() = getWorkPath() + "/data/"

    /**
     * APK 경로를 가져옵니다.
     *
     * @param kioskType 키오스크 타입
     */
    fun getKioskTypePath(kioskType: String) = getWorkPath() + "/apk/" + kioskType + "/"
    fun getApkPathOrNull(kioskType: String, version: String): String? {
        var url = getApkPath(kioskType, version)
        if(File(url).exists()) return url;
        else return null;
    }

    fun getApkPath(kioskType: String, version:String) = getWorkPath() + "/apk/" + kioskType + "/" + version + "/"

    /**
     * 작업 디렉토리의 절대 경로를 가져옵니다.
     */
    fun getWorkPath() = Paths.get("").toAbsolutePath().toString()

}