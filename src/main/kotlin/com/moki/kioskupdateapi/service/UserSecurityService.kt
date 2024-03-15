package com.moki.kioskupdateapi.service

import com.moki.kioskupdateapi.dto.EmailMessage
import com.moki.kioskupdateapi.model.repository.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder.setContext
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import javax.mail.MessagingException
import javax.mail.internet.MimeMessage
import javax.transaction.Transactional


@Service
class UserSecurityService(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val javaMailSender: JavaMailSender,
) : UserDetailsService {
    @Value("\${send-email}")
    private val EMAIL :  String = "default@gmail.com"
    private val PASSWORD_LENGTH = 10;
    private val PASSWORD_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userRepository.findByUsername(username)
        return if (user != null) {
            User(user.username, user.password, emptyList())
        } else {
            null
        }
    }

    fun isUserTryCountIsOk(username: String): STATUS {
        val user = userRepository.findByUsername(username)
        return if (user != null) {
                if (user.tryCount == 10) {
                    return STATUS.RESET
                }
                else if(user.tryCount > 10) return STATUS.OVER
                else return STATUS.OK
            } else {
                return STATUS.FAIL
            }
        }
    @Transactional
    fun resetUserPassword(username : String) {
        val user = userRepository.findByUsername(username)
        if(user!=null) {
            val generatedPassword = generateRandomPassword()
            println(generatedPassword + " : 비밀번호")
            user.password = passwordEncoder.encode(generatedPassword)
            user.loginStatus = false
            userRepository.saveAndFlush(user)

            sendPasswordToEmail(EmailMessage(
                    EMAIL,
                    "[모키 update2] 관리자 비밀번호 변경되었습니다.",
                    "username : ${user.username}\n비밀번호 `${generatedPassword}`로 재로그인 후, 비밀번호를 변경하세요"
                    ))
        }
    }

    private fun generateRandomPassword(): String {
        return (1..PASSWORD_LENGTH)
                .map { PASSWORD_CHARSET.random() }
                .joinToString("")
    }

    private fun sendPasswordToEmail(emailMessage: EmailMessage){
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, "UTF-8")
            mimeMessageHelper.setTo(emailMessage.to) // 메일 수신자
            mimeMessageHelper.setSubject(emailMessage.subject) // 메일 제목
            mimeMessageHelper.setText(emailMessage.message)
            javaMailSender.send(mimeMessage)
        }catch (e : MessagingException){
            throw IllegalArgumentException("이메일 전송에 실패했습니다.")
        }
    }

    @Transactional
    fun addTryCount(username : String) {
        val user = userRepository.findByUsername(username)
        if(user!=null){
            user.tryCount+=1
            userRepository.saveAndFlush(user)
        }
    }

    enum class STATUS{
        OK, RESET, OVER, FAIL;

        fun isOk(): Boolean {
            return this == STATUS.OK
        }
        fun isReset(): Boolean {
            return this == STATUS.RESET
        }
        /*
        FAIL : username == null
        OVER : 이미 비밀번호가 10회 이상 틀려서, 비밀번호 재발급
        RESET : 비밀번호 재발급 필요한 상태
        OK : 10회 미만으로 틀림
         */
    }

    fun getUsernameByContext() : String{
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated ) {
            val principal = authentication.principal
            return (principal as UserDetails).username
        }
        throw IllegalStateException("존재하지 않는 유저입니다")
    }

    fun getLoginStatusByUsername(username: String) : Boolean{
        userRepository.findByUsername(username)?.let {
                return it.loginStatus
            }?: throw IllegalStateException("존재하지 않는 유저임")
        }

    @Transactional
    fun changePassword(username: String, newPassword: String): String {
        userRepository.findByUsername(username)?.let {
            it.loginStatus = true
            it.tryCount = 0
            println("--password 바꿈 : ${newPassword}")
            it.password = passwordEncoder.encode(newPassword)
            return username
        }?: throw IllegalStateException("존재하지 않는 유저입니다.")
    }

}