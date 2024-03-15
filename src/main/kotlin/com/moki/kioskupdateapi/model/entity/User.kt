package com.moki.kioskupdateapi.model.entity

import javax.persistence.*

@Entity
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Int? = null,

    @Column(unique = true)
    val username: String,
    var password: String,
    var loginStatus: Boolean,
    var tryCount: Int
)

