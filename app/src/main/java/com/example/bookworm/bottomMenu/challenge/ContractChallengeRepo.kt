package com.example.bookworm.bottomMenu.challenge

import com.example.bookworm.bottomMenu.challenge.items.Challenge
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

/** -- 챌린지 Repository 인터페이스 --
 *
 * 외부의 리소스를 가져와서 뷰모델에게 전달하는 Repository에 관한 인터페이스를 작성해두었습니다.
 *
 * 외부의 리소스를 가져오다보니, suspend를 사용하여 비동기처리하는 부분이 추가가 되었습니다.
 */
interface ContractChallengeRepo {
    //챌린지 관련
    suspend fun loadChallenges(keyword: String = "", lastVisible: String? = null, pageSize: Long):ArrayList<Challenge>?//챌린지 목록을 불러옴
    suspend fun createChallenge(challenge: Challenge) :Boolean //챌린지를 생성
    suspend fun allowToJoinChallenge(challengeID: String) //챌린지에 참여가능한지 확인


    //보드 관련
    suspend fun loadBoards() //챌린지 인증 게시물 목록을 불러옴
    suspend fun createBoard() //인증 게시물을 작성함
    suspend fun deleteBoard() //인증 게시물을 삭제함
    suspend fun updateBoard() //인증 게시물을 수정함

}