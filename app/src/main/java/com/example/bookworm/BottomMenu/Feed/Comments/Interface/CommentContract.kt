package com.example.bookworm.BottomMenu.Feed.Comments.Interface

interface CommentContract {
    interface View{
        fun showData()
    }
    interface Presenter{
        fun LoadData()
        fun setData()
    }
}