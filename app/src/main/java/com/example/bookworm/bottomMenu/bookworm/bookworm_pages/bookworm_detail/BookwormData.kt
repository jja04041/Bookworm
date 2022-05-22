package com.example.bookworm.bottomMenu.bookworm.bookworm_pages.bookworm_detail

class BookwormData {
    lateinit var name:String
    var id:Int=0
    var hasBw:Boolean = false

    fun setBwData(id: Int,name:String,boolean: Boolean){
        this.name = name
        this.id =id
        hasBw=boolean
    }
}