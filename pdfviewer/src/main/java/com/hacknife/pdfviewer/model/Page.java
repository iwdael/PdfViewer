package com.hacknife.pdfviewer.model;

public class Page {
    public int page;
    public SizeF size;

    public Page(int page, SizeF size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public String toString() {
        return "{" +
                "\"size\":" + size.newScale(1/size.width) +
                '}';
    }
}
