package com.example.bookworm.chat;

import java.util.HashMap;
import java.util.Map;

public class Chatmodel {

    public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저

    public MessageItem messageitem = new MessageItem(); //채팅 메시지

    public static class MessageItem {

        private String token;
        private String name;
        private String message;
        private String time;
        private String profileUri;

        public MessageItem(String name, String message, String time, String profileUri) {
            this.name = name;
            this.message = message;
            this.time = time;
            this.profileUri = profileUri;
        }

        //firebase DB에 객체로 값을 읽어올 때
        //파라미터가 비어있는 생성자가 필요함
        public MessageItem() {
        }

        //Getter & Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

//        public String getOpponent() {
//                return opponent;
//        }
//
//        public void setOpponent(String opponent) {
//                this.opponent = opponent;
//        }


        public String getMessage() {
            return message;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getProfileUri() {
            return profileUri;
        }

        public void setProfileUri(String profileUri) {
            this.profileUri = profileUri;
        }


    }

    public MessageItem getMessageitem() {
        return messageitem;
    }

    public void setMessageitem(MessageItem messageitem) {
        this.messageitem = messageitem;
    }




}
