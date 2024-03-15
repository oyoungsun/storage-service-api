package com.moki.kioskupdateapi.utils

import org.apache.tomcat.util.http.fileupload.FileUtils
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import java.io.*
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * ### 파일 관련 유틸
 *
 * @author donguk
 * @since 1.0.0
 */
object FileIOUtils {

    /**
     * 파일의 리소스를 가져옵니다.
     *
     * @param path 파일 경로
     * @return 파일 리소스
     */
    @kotlin.jvm.Throws(IOException::class)
    fun loadAsResource(path: String): Resource {
        try {
            val file = Paths.get("").resolve(path)

            val resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                return resource
            } else {
                throw RuntimeException("Could not read file: $path")
            }

        } catch (e: MalformedURLException) {
            throw RuntimeException("Could not read file: $path")
        }
    }

    fun createVersionDir(url: String) {
        val file = File(url)
        if (file.exists()) {
            FileUtils.cleanDirectory(file);
        }
        Files.createDirectories(file.toPath())
    }

    fun cleanKioskTypeDir(kioskType: String) {
        var url = FileConstant.getWorkPath() + "/apk/" + kioskType
        val file = File(url)
        if (file.exists()) {
            FileUtils.cleanDirectory(file);
        }
        file.delete()
    }

    fun cleanKioskVersionDir(kioskType: String, version: String) {
        var url = FileConstant.getWorkPath() + "/apk/${kioskType}/${version}"
        val file = File(url)
        if (!file.exists()) {
            throw IllegalArgumentException("존재하지 않는 버전입니다.")
        }
        FileUtils.cleanDirectory(file);
        file.delete()
    }

    fun getReleaseNote(typeVerDir: String): String {
        val br: BufferedReader
        try {
            br = BufferedReader(FileReader(typeVerDir + "releaseNote.txt"));
        } catch (e: FileNotFoundException) {
            return "";
        }
        var str: String = ""
        do {
            var temp = br.readLine()
            if (temp == null) break
            str += temp
        } while (temp != null)
        br.close()
        return str

    }

    fun createReleaseNote(uploadDir: String, release: String?) {
        val bw = BufferedWriter(FileWriter(uploadDir + "releaseNote.txt"));
        bw.write(release)
        bw.flush()
        bw.close()
    }
}