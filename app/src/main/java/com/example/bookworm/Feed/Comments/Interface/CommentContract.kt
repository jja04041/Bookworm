package com.example.bookworm.Feed.Comments.Interface

interface CommentContract {
    interface View{
        fun showData()
    }
    interface Presenter{
        fun LoadData()
        fun setData()
    }
}