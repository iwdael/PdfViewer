package com.hacknife.pdfviewer.core.model;

import java.util.ArrayList;
import java.util.List;

public class Bookmark {

    private List<Bookmark> children = new ArrayList<>();
    String title;
    long pageIdx;
    long mNativePtr;


    public List<Bookmark> getChildren() {
        return children;
    }

    public void setChildren(List<Bookmark> children) {
        this.children = children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getPageIdx() {
        return pageIdx;
    }

    public void setPageIdx(long pageIdx) {
        this.pageIdx = pageIdx;
    }

    public long getmNativePtr() {
        return mNativePtr;
    }

    public void setmNativePtr(long mNativePtr) {
        this.mNativePtr = mNativePtr;
    }
}
