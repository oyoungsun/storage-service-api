package com.moki.kioskupdateapi.dto;

import com.moki.kioskupdateapi.model.entity.KioskInfo

data class KioskDetailDto(
        val kioskInfo: KioskInfo,
        val versionInfos: List<VersionInfo>,
)
