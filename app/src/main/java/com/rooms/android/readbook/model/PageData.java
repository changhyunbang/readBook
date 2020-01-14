package com.rooms.android.readbook.model;

public class PageData {

    String bookId;
    String pageId;
    String pageIndex;
    String imagePath;
    String text;

    public PageData() {

    }

    public PageData(String bookId,
                    String pageId,
                    String pageIndex,
                    String imagePath,
                    String text,
                    String audioData) {

        this.bookId = bookId;
        this.pageId = pageId;
        this.pageIndex = pageIndex;
        this.imagePath = imagePath;
        this.text = text;
    }

    public String toString() {

        return String.format("[bookId] : %s [pageId] : %s [pageIndex] : %s [imagePath] : %s [text] : %s",
                bookId, pageId, pageIndex, imagePath, text);
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
