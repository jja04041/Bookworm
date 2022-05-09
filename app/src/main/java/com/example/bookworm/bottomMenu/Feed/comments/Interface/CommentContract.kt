package com.example.bookworm.bottomMenu.Feed.comments.Interface

interface CommentContract {
    interface View{
        fun showData()
    }
    interface Presenter{
        fun LoadData()
        fun setData()
    }
}