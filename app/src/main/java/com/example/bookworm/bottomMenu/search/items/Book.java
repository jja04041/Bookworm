package com.example.bookworm.bottomMenu.search.items;

import java.io.Serializable;
import java.util.Map;

public class Book implements Serializable {
    //제목, 간단한 내용, 출판사, 저자 정도 표기?
    private String img_url = "";
    private String title= "";
    private String categoryname = "";
    private String content= "";
    private String publisher = "";
    private String author= "";
    private String itemId = "";

    public Book(String... strings) {
        if (strings != null) {
            this.title = strings[0];
            this.categoryname = strings[1];
            this.content = strings[2];
            this.publisher = strings[3];
            this.author = strings[4];
            this.img_url = strings[5].replace("coversum", "cover500");
            if (strings.length == 7) this.itemId = strings[6];
        }
    }

    public Book(){}

    public void setBook(Map map) {
        this.title = (String) map.get("title");
        this.categoryname = (String) map.get("categoryname");
        this.content = (String) map.get("content");
        this.img_url = (String) map.get("img_url");
        this.publisher = (String) map.get("publisher");
        this.author = (String) map.get("author");
        this.itemId = (String) map.get("itemId");
    }

    public String getImg_url() {
        return img_url;
    }

    public String getItemId() {
        return itemId;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }


}
