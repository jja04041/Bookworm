package com.example.bookworm.extension.follow.interfaces

interface PagerInterface {
    interface Page{

    }
    interface PageAdapter{
        fun UpdateTapName(newName:String?,page:Int)
    }
}