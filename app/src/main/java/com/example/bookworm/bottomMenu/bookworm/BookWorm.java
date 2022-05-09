package com.example.bookworm.bottomMenu.bookworm;

import com.example.bookworm.R;
import com.example.bookworm.bottomMenu.search.items.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BookWorm {

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;


    // 업적 달성시 이 벡터에 책볼레 drawble id값을 추가합니다.
    private int wormtype = R.drawable.bw_default;
    private Vector<Integer> wormvec = new Vector<>();

    private int bgtype = R.drawable.bg_default;
    private Vector<Integer> bgvec = new Vector<>();

    private HashMap<String, Boolean> achievementmap = new HashMap<>();


    private int readcount;
    private Vector<Book> readedbook = new Vector<>();


    public BookWorm() {
        this.wormtype = R.drawable.bw_default;
        this.wormvec.add(R.drawable.bw_default);
        this.bgtype = R.drawable.bg_default;
        this.bgvec.add(R.drawable.bg_default);

        this.achievementmap.put("디폴트", true);

        this.readcount = 0;
    }


    public void add(Map document) {
        this.token = (String) document.get("token");
        this.wormtype = Integer.parseInt(String.valueOf(document.get("wormtype")));
        this.wormvec = new Vector<>((ArrayList<Integer>) document.get("wormvec"));

        this.bgtype = Integer.parseInt(String.valueOf(document.get("bgtype")));
        this.bgvec = new Vector<>((ArrayList<Integer>) document.get("bgvec"));


        this.achievementmap = new HashMap<>((HashMap<String, Boolean>) document.get("achievementmap"));

        this.achievementmap.put("디폴트", true);

        this.readcount = Integer.parseInt(String.valueOf(document.get("readcount")));
        this.readedbook = new Vector<>((ArrayList<Book>) document.get("readedbook"));

    }


    // 볼레
    public Vector<Integer> getWormvec() {
        return wormvec;
    }

    public void setWormvec(Vector<Integer> wormvec) {
        this.wormvec = wormvec;
    }

    public int getWormtype() {
        return wormtype;
    }

    public void setWormtype(int wormtype) {
        this.wormtype = wormtype;
    }


    // 배경
    public int getBgtype() {
        return bgtype;
    }

    public void setBgtype(int bgtype) {
        this.bgtype = bgtype;
    }

    public Vector<Integer> getBgvec() {
        return bgvec;
    }

    public void setBgvec(Vector<Integer> bgvec) {
        this.bgvec = bgvec;
    }


    public HashMap<String, Boolean> getAchievementmap() {
        return achievementmap;
    }

    public void setAchievementmap(HashMap<String, Boolean> achievementmap) {
        this.achievementmap = achievementmap;
    }

    public int getReadcount() {
        return readcount;
    }

    public void setReadcount(int readcount) {
        this.readcount = readcount;
    }

    public Vector<Book> getReadedbook() {
        return readedbook;
    }

    public void setReadedbook(Vector<Book> readedbook) {
        this.readedbook = readedbook;
    }

}

