package com.example.bookworm.Extension.Follow.Interfaces

interface PagerInterface {
    interface Page{

    }
    interface PageAdapter{
        fun UpdateTapName(newName:String?,page:Int)
    }
}