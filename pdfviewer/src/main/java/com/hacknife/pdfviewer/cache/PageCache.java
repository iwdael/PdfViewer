package com.hacknife.pdfviewer.cache;

import com.hacknife.pdfviewer.PdfView;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.model.PDF;

import java.util.HashMap;
import java.util.Map;

public class PageCache {
    private Map<Integer, PDF> pages;

    public PageCache() {
        this.pages = new HashMap<>();
    }

    public PageCache put(int page, PDF p) {
        this.pages.put(page, p);
        return this;
    }

    public PDF getPage(int page) {
        return this.pages.get(page);
    }

    @Override
    public String toString() {
        return pages.toString();
    }
}
