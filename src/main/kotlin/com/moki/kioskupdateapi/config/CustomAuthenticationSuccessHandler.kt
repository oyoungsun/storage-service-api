package com.moki.kioskupdateapi.config

import com.moki.kioskupdateapi.service.UserSecurityService
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthenticationSuccessHandler(
    private val userSecurityService: UserSecurityService,
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        if (authentication != null && authentication.isAuthenticated ) {
            val principal = authentication.principal
            val username = (principal as UserDetails).username
            val status = userSecurityService.getLoginStatusByUsername(username)
            if (status) {
                response?.sendRedirect("/kiosk_update/admin/index")
            } else {
                response?.sendRedirect("/kiosk_update/admin/password-change")
            }
        }
        else throw IllegalStateException("권한이 없는 유저입니다.")
    }

}