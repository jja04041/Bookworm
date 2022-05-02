package com.example.bookworm.Follow.Interfaces

interface PagerInterface {
    interface Page{

    }
    interface PageAdapter{
        fun UpdateTapName(newName:String?,page:Int)
    }
}