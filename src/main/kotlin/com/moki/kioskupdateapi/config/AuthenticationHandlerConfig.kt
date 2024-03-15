package com.moki.kioskupdateapi.config

import com.moki.kioskupdateapi.service.UserSecurityService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthenticationHandlerConfig(
        private val userSecurityService: UserSecurityService
) {

    @Bean
    fun customAuthenticationFailureHandler(): CustomAuthenticationFailureHandler {
        return CustomAuthenticationFailureHandler(userSecurityService)
    }

    @Bean
    fun custonAuthenticationSuccessHandler(): CustomAuthenticationSuccessHandler {
        return CustomAuthenticationSuccessHandler(userSecurityService)
    }
}
