package com.iwdael.pdfviewer.core.model;

import android.graphics.RectF;

public class Link {
    private RectF bounds;
    private Integer destPageIdx;
    private String uri;

    public void setBounds(RectF bounds) {
        this.bounds = bounds;
    }

    public void setDestPageIdx(Integer destPageIdx) {
        this.destPageIdx = destPageIdx;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getDestPageIdx() {
        return destPageIdx;
    }

    public String getUri() {
        return uri;
    }

    public RectF getBounds() {
        return bounds;
    }
}
