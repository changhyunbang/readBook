package com.rooms.android.readbook.model;

import java.util.ArrayList;

public class BookData {

    String bookId;
    String bookName;
    String imagePath;

    public BookData() {

    }

    public BookData(String bookName, String imagePath) {

        this.bookName = bookName;
        this.imagePath = imagePath;
    }

    public String toString() {

        return String.format("[bookId] : %s [bookName] : %s [imagePath] : %s", bookId, bookName, imagePath);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
