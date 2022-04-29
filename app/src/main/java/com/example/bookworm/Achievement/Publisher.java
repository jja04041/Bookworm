package com.example.bookworm.Achievement;

//이벤트 발생시키는 객체 인터페이스
public interface Publisher {
    //관찰자 객체 추가
    public void addObserver(ObserverItf o);
    //관찰자 객체 삭제
    public void deleteObserver(ObserverItf o);
    //관찰자들에게 이벤트 발생 전달
    public void notifyObservers();
}
