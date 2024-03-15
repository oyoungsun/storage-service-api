package com.moki.kioskupdateapi.config

import com.moki.kioskupdateapi.service.UserSecurityService
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthenticationFailureHandler(
        private val userSecurityService: UserSecurityService
) : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
            request: HttpServletRequest?,
            response: HttpServletResponse?,
            exception: AuthenticationException?
    ) {
        val username = request?.getParameter("username")
        if (request != null && response != null && username != null) {
            userSecurityService.addTryCount(username)
            val status = userSecurityService.isUserTryCountIsOk(username)
            if (status.isOk()) {
                response.sendRedirect("/login")
            } else if(status.isReset()) {
                println("로그인 10회 초과, db를 수정합니다")
                userSecurityService.resetUserPassword(username)
                response.sendRedirect("/html/login-error.html")
            }else{//OVER
                response.sendRedirect("/html/login-error.html")

            }
        }
    }

}
