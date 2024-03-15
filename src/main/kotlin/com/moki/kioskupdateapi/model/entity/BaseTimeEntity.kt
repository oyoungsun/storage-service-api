package com.moki.kioskupdateapi.model.entity

import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass
import javax.persistence.PrePersist


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity(
    @LastModifiedDate
    var modifiedAt: LocalDateTime? = null
) {
    /**
     * 생성시에도 modifiedAt 초기화되도록 설정함
     */
    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        this.modifiedAt = now
    }
}

