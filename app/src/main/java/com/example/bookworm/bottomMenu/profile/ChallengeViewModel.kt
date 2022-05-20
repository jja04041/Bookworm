package com.example.bookworm.bottomMenu.profile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.core.dataprocessing.repository.ChallengeRepositoryImpl
import kotlinx.coroutines.launch


class ChallengeViewModel(val context: Context):ViewModel() {
    var repo: ChallengeRepositoryImpl
    val challengeList:MutableLiveData<ArrayList<Challenge>> = MutableLiveData()
    class Factory(val context: Context): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChallengeViewModel(context) as T
        }
    }
    init{
        repo = ChallengeRepositoryImpl(context)
    }
    fun getChallengeList(token:String){
        viewModelScope.launch {
            challengeList.value=repo.getChallenges(token)
        }
    }
}