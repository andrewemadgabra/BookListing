package com.example.andrew.booklisting;

/**
 * Created by andrew on 10/11/2017.
 */

public class book {
    private String title, language, author;

    public book(String ctitle, String clanguage, String cauthor) {
        title = ctitle;
        language = clanguage;
        author = cauthor;
    }

    String getTitle() {
        return title;
    }

    String getauthor() {
        return author;
    }

    String getLanguage() {
        return language;
    }

}
