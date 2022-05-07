package com.example.bookworm.achievement;

//관찰자 객체 인터페이스
interface ObserverItf {
    //이벤트 발생에 따른 행위
    public void notify(boolean play);
}