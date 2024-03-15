package com.moki.kioskupdateapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig {
    @Value("\${spring.mail.username}")
    private val username: String? = null

    @Value("\${spring.mail.password}")
    private val password: String? = null
    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "smtp.gmail.com"
        mailSender.port = 587
        mailSender.username = username;
        mailSender.password = password // 본인의 이메일 비밀번호로 변경
        val props = mailSender.javaMailProperties
        props["mail.smtp.auth"] = true
        props["mail.smtp.starttls.enable"] = true
        return mailSender
    }
}
