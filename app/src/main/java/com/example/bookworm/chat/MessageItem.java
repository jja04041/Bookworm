package com.example.bookworm.chat;

public class MessageItem {

        private String name;
        private String message;
        private String time;
        private String profileUrl;

public MessageItem(String name, String message, String time, String pofileUrl) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.profileUrl = pofileUrl;
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

        public String getProfileUrl() {
                return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
                this.profileUrl = profileUrl;
        }


}
