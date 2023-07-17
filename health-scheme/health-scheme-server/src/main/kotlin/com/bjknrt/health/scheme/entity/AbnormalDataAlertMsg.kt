package com.bjknrt.health.scheme.entity

class AbnormalDataAlertMsg() {
    lateinit var title: String
    lateinit var content: String

    constructor(title: String, content: String) : this() {
        this.title = title
        this.content = content
    }
}



