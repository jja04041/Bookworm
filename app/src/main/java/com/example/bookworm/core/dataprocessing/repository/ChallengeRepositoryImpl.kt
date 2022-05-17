package com.example.bookworm.core.dataprocessing.repository

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.example.bookworm.core.userdata.UserInfo
import com.example.bookworm.extension.follow.view.FollowViewModelImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChallengeRepositoryImpl(val context: Context) : DataRepository.HandleChallenge {
    val db = FirebaseFirestore.getInstance() //파이어스토어와 연결
    var collectionReference = db.collection("challenge")
    var followViewModelImpl =
        ViewModelProvider(context as AppCompatActivity, FollowViewModelImpl.Factory(context)).get(
            FollowViewModelImpl::class.java
        )

    override suspend fun getChallenges(token: String): ArrayList<Challenge> {
        val returnValue:ArrayList<Challenge> = ArrayList()
        var followingTokenList = CoroutineScope(Dispatchers.IO).async {
            followViewModelImpl.getFollowTokenList(token, false, null);
        }.await()
        var result= collectionReference.whereIn("masterToken", followingTokenList).get().await()
         for (i in result.documents){
             var challenge=Challenge(i.getData())
             returnValue.add(challenge)
         }
        return  returnValue
    }
}