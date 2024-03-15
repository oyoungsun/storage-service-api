package com.moki.kioskupdateapi.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/kiosk_update/admin")
class ViewController {
    /**
     * 로그인 화면 반환
     */
    @GetMapping("/login")
    fun login(): String {
        return "/html/login.html"
    }

    @GetMapping("/index")
    fun index(): String {
        return "/html/index.html"
    }

    @GetMapping("/business")
    fun business(): String {
        return "/html/business.html"
    }

    @GetMapping("/password-change")
    fun passwordChange(): String {
        return "/html/password-change.html"
    }

}