package com.hacknife.pdfviewer.model;

public class SizeF {
    public float width;
    public float height;

    public SizeF(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public SizeF scale(float scale) {
        width = width * scale;
        height = height * scale;
        return this;
    }

    public SizeF widthScaleTo(float width) {
        return scale(width / this.width);
    }
    public SizeF heightScaleTo(float height) {
        return scale(height / this.height);
    }
}
