package com.example.bookworm.Search.items;

import java.io.Serializable;

public class Book implements Serializable {
    //제목, 간단한 내용, 출판사, 저자 정도 표기?
    private String img_url;
    private String title;
    private String content;
    private String publisher;
    private String author;
    private String itemId;


    public Book(String... strings) {

        this.title = strings[0];
        this.content = strings[1];
        this.publisher = strings[2];
        this.author = strings[3];
        this.img_url = strings[4];
        if (strings.length == 6) this.itemId = strings[5];

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

}
