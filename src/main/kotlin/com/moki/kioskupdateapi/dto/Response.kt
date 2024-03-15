package com.moki.kioskupdateapi.dto

data class Response<T>(
        val code: Int,
        val message: String?,
        val result: T?,
){
    companion object {
        const val DEFAULT_SUCCESS_CODE = 200

        fun <T> success(result: T?, code: Int = DEFAULT_SUCCESS_CODE, message: String? = ""): Response<T> {
            return Response(code = code, message = message, result = result)
        }

        fun <T> error(code: Int, message: String? = ""): Response<T> {
            return Response(code = code, message = message, result = null)
        }
    }
}
const val DEFAULT_SUCCESS_CODE = 200
