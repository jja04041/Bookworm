package com.example.bookworm.Bw;

public enum enum_wormtype {
// 벌레의 종류입니다.

    디폴트(0), 공포(1), 추리(2), 로맨스(3), 시사(4), 경제(5), 에세이(6);

    enum_wormtype (int value) { this.value = value; }
    private final int value;
    public int value() { return value; }
}
