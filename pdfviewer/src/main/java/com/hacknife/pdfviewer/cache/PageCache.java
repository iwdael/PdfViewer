package com.hacknife.pdfviewer.cache;

import com.hacknife.pdfviewer.model.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PageCache {
    private Map<Integer,Page> pages;

    public PageCache() {
        this.pages = new HashMap<>();
    }
    public PageCache put(int page,Page p){
        this.pages.put(page,p);
        return this;
    }

    public Page getPage(int page){
        return this.pages.get(page);
    }

    @Override
    public String toString() {
        return  pages.toString();
    }
}
