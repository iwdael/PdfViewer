package com.hacknife.pdfviewer.model;

import android.graphics.Bitmap;

public class Cell {
    public Bitmap bitmap;
    private Cell top;
    private Cell bottom;
    private Cell left;
    private Cell right;

    public int x;
    public int y;

    public Cell(float cellW, float cellH, int x, int y) {
        this(cellW, cellH);
        this.x = x;
        this.y = y;

    }

    @Override
    public String toString() {
        return "{" +
                "\"x\":" + x +
                ", \"y\":" + y +
                '}';
    }

    public Cell(float width, float height) {
        this.bitmap = Bitmap.createBitmap(Math.round(width), Math.round(height), Bitmap.Config.ARGB_8888);
    }

    public Cell next(Direction direction) {
        return direction == Direction.VERTICAL ? bottom : right;
    }

    public Cell previous(Direction direction) {
        return direction == Direction.VERTICAL ? top : left;
    }

    public void setBottom(Cell bottom) {
        this.bottom = bottom;
//        System.out.println("bottom  y:  " + y + " < " + bottom.y);
    }

    public void setLeft(Cell left) {
        this.left = left;
//        System.out.println("left  x:  " +  toString() + " > " + left.toString());
    }

    public void setRight(Cell right) {
        this.right = right;
//        System.out.println("right x:  " + toString() + " < " + right.toString());
    }

    public void setTop(Cell top) {
        this.top = top;
//        System.out.println("top    y:  " + y + " > " + top.y);

    }


    public void erase() {
        this.bitmap.eraseColor(0);
    }
}
