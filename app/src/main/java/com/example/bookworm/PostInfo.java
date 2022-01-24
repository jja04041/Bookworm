package com.example.bookworm;

import java.util.HashMap;
import java.util.Map;

public class PostInfo {

        public String postid; // 로그인한 아이디
        public String photo; // 게시글 사진
        public String photopath; // 게시글사진 이름(사진삭제할때 필요, 절대경로를 뜻함)
        public String contents; // 게시글 내용
        public String profileimg; // 회원가입시 프로필사진
        public String username; // 회원가입시 닉네임

        public int Like = 0; // 좋아요 갯수
        public Map<String, Boolean> stars = new HashMap<>(); // 좋아요 한 사람
        // String 값은 아이디를 뜻하고, boolean 은 true


}
