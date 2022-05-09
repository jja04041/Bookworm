package com.example.bookworm.bottomMenu.Feed.views

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.core.dataprocessing.repository.UserRepositoryImpl
import com.example.bookworm.core.userdata.UserInfo
import kotlinx.coroutines.launch

class FeedViewModel(val context: Context): ViewModel()  {
    class Factory(val context: Context): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FeedViewModel(context) as T
        }
    }
    val repo = UserRepositoryImpl(context)
    fun getUser(token: String?,liveData: MutableLiveData<UserInfo>) {
        viewModelScope.launch {
            liveData.value = repo.getUser(token,false) //데이터 변경을 감지하면, 값이 업데이트 된다.
        }
    }

}