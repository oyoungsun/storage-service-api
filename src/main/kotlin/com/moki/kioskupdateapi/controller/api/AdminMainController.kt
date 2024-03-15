package com.moki.kioskupdateapi.controller.api

import com.moki.kioskupdateapi.dto.Response
import com.moki.kioskupdateapi.model.entity.User
import com.moki.kioskupdateapi.model.repository.UserRepository
import com.moki.kioskupdateapi.service.UserSecurityService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException

@RestController
@RequestMapping("")
class AdminMainController(
    private val userRepository: UserRepository,
    private val userSecurityService: UserSecurityService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("/kiosk_update/create-user")
    fun createUser(): Response<User> {
        var user =
            User(username = "admin5", password = passwordEncoder.encode("qazwsx123"), loginStatus = true, tryCount = 0)
        userRepository.save(user)
        return Response.success(
            result = user
        )
    }

    @PostMapping("/kiosk_update/change-password")
    fun changePassword(
        @RequestBody passwordMap : Map<String, String>
    ): Response<String> {
        return try {
            val username = userSecurityService.getUsernameByContext()
            val newPassword = passwordMap.get("newPassword")!!
            val usernameWithNewPassword = userSecurityService.changePassword(username, newPassword)
            Response.success(
                message = "비밀번호가 성공적으로 변경되었습니다. 다시 로그인 해주세요",
                result = usernameWithNewPassword)
        } catch (e: IllegalStateException) {
            Response.error(
                code = 404,
                message = e.message
            )
        }
    }
}