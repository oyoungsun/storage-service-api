package com.moki.kioskupdateapi.model.repository

import com.moki.kioskupdateapi.model.entity.*
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String): User?
}
