package com.example.bookworm.Core.UserData.Modules

import com.example.bookworm.Core.UserData.Interface.UserContract
import com.example.bookworm.Core.UserData.UserInfo
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class LoadUser(val view:UserContract.View):UserContract.Presenter {
    var token: String? = null
    var reference: CollectionReference? =null
    var task: Task<DocumentSnapshot>? =null
    var boolean:Boolean?=null
    override fun getData(token: String,boolean: Boolean?) {
        this.token =token
        reference= FirebaseFirestore.getInstance().collection("users")
        task=reference!!.document(token).get()
        if(boolean!=null)this.boolean=boolean
        task!!.addOnCompleteListener({
            setProfile(it.result)
        })
    }

    override fun setProfile(document: DocumentSnapshot) {
//        var map= document.data!!["UserInfo"] as Map<String?,Any?>
//        var userData=UserInfo()
//        userData.add(map)
//        view.showProfile(userData,boolean)
    }
}