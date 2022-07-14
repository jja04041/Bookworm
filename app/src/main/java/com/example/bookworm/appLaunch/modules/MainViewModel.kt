package com.example.bookworm.appLaunch.modules

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.appLaunch.views.MainActivity
import com.example.bookworm.bottomMenu.profile.UserInfoViewModel
import com.example.bookworm.core.login.LoginActivity
import com.example.bookworm.core.userdata.UserInfo
import kotlinx.coroutines.launch

//앱  로그인 시에 사용하는 뷰모델


class MainViewModel(val context: Context) : ViewModel() {
    val ct = if (context is MainActivity) context else context as LoginActivity
    var userInfoViewModel =
        ViewModelProvider(ct, UserInfoViewModel.Factory(context)).get(UserInfoViewModel::class.java)
    val data: MutableLiveData<Boolean> = MutableLiveData()
    val userInfo: MutableLiveData<UserInfo> = MutableLiveData()

    class Factory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(context) as T
        }
    }

    fun createUser(userInfo: UserInfo) {
        viewModelScope.launch {
            data.value = userInfoViewModel.createUser(userInfo)
        }
    }

    fun getUser(token: String?, getFromExt: Boolean) {
        viewModelScope.launch {
            userInfoViewModel.getUser(token, getFromExt)
            userInfo.value = userInfoViewModel.data.value
        }
    }


}