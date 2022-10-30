package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

class BookwormData {
    lateinit var name: String
    var id: String= ""
    var hasBw: Boolean = false

    fun setBwData(id: String, name: String, boolean: Boolean) {
        this.name = name
        this.id = id
        hasBw = boolean
    }
}