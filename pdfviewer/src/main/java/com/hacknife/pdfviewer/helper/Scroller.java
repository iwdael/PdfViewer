package com.hacknife.pdfviewer.helper;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

public class Scroller extends OverScroller {
    private int lastX = 0;
    private int lastY = 0;

    public Scroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }


    public int getOffsetX() {
        int current = getCurrX();
        int offsetX = lastX - current;
        if (lastX == 0) offsetX = 0;
        lastX = current;
        return offsetX;
    }

    public int getOffsetY() {
        int current = getCurrY();
        int offsetY = lastY - current;
        if (lastY == 0) offsetY = 0;
        lastY = current;
        return offsetY;
    }

    public void finish() {
        lastY = 0;
        lastX = 0;
        forceFinished(true);
    }
}
