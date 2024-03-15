package com.moki.kioskupdateapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(
            failureHandler: CustomAuthenticationFailureHandler,
            successHandler: CustomAuthenticationSuccessHandler,
            http: HttpSecurity,
    ): SecurityFilterChain {
        http
                ?.cors().and().csrf().disable()
                ?.authorizeRequests()
                ?.mvcMatchers("/html/**", "/css/**", "/scripts/**", "/plugin/**", "/fonts/**")?.permitAll()
                ?.antMatchers("/kiosk_update/admin/login")?.permitAll()
                ?.antMatchers("/kiosk_update/create-user")?.permitAll()
                ?.antMatchers("/api/latest_apk_info")?.permitAll()
                ?.antMatchers("/api/download_apk")?.permitAll()
                ?.anyRequest()?.authenticated()
                ?.and()
                ?.formLogin { formLogin ->
                    formLogin
                            .usernameParameter("username")
                            .passwordParameter("password")
                            .loginPage("/kiosk_update/admin/login")
                            .successHandler(successHandler)
                            .failureHandler(failureHandler)
                }

        UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY
        return http.build()
    }
}