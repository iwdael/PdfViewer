package com.hacknife.pdfviewer.model;

public class BmPack {
    public int row;
    public int col;
    public static final int  SPACE = 3 ;

    public BmPack(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public float width;
    public float height;

    public float getRowWidth() {
        return width * col;
    }

    public float getColHeight() {
        return height * row;
    }
}
