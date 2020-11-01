package com.hacknife.pdfviewer.model;

public class SizeF {
    public float width;
    public float height;

    public SizeF(float width, float height) {
        this.width = width;
        this.height = height;
    }

//    public SizeF scale(float scale) {
//        width = width * scale;
//        height = height * scale;
//        return this;
//    }

    public SizeF scale(float scale) {
        return new SizeF(width * scale, height * scale);
    }

    public SizeF widthScaleTo(float width) {
        return scale(width / this.width);
    }

    public SizeF heightScaleTo(float height) {
        return scale(height / this.height);
    }


    public Size toSize() {
        return new Size((int) width, (int) height);
    }

    @Override
    public String toString() {
        return "{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    public String toScaleString() {
        return "" + height / width;
    }


}
