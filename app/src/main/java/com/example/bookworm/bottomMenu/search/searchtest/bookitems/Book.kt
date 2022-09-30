package com.example.bookworm.bottomMenu.search.searchtest.bookitems

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

//도서 DTO(Data Transfer Object)
@Parcelize
data class Book(
        var imgUrl: String = "", //도서 이미지
        var title: String = "",
        var categoryName: String = "",
        var content: String = "",
        var publisher: String = "",
        var author: String = "",
        var itemId: String = "",
        var isbn: String = "",
        @Exclude @get:Exclude @set:Exclude
        var isRecommend: Boolean = false,
) : Parcelable