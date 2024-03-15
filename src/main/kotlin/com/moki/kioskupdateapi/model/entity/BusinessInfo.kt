package com.moki.kioskupdateapi.model.entity

import javax.persistence.*

@Entity
class BusinessInfo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val businessInfoId: Int? = null,

    @Column(unique = true)
    var businessNumber: String,

    @ManyToOne
    @JoinColumn(name = "kiosk_info_id")
    var kioskInfo: KioskInfo,
)