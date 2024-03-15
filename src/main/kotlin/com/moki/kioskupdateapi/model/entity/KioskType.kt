package com.moki.kioskupdateapi.model.entity

enum class KioskType(val type: String) {
    NEW("new"), RECIEPT("reciept"), BASIC("basic");


    fun getString(): String {
        return this.type;
    }

}