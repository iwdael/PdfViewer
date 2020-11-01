package com.hacknife.pdfviewer.model;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.hacknife.pdfviewer.state.PatchState;

public class Patch implements Comparable<Patch> {
    public int page;
    public Bitmap bitmap;
    public Rect rect;
    public float scale;
    public PatchState state;

    public Patch(int width, int height) {
        this.page = -1;
        this.rect = new Rect(0, 0, 0, 0);
        this.scale = -1;
        this.state = PatchState.PREPARE;
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }


    @Override
    public int compareTo(Patch patch) {
        if (rect.left == patch.rect.left) {
            return rect.top < patch.rect.top ? -1 : 1;
        } else if (rect.left < patch.rect.left) {
            if (rect.top == patch.rect.top) return -1;
            else if (rect.top > patch.rect.top) return 1;
            else return -1;
        } else  {
            if (rect.top == patch.rect.top) return 1;
            else if (rect.top > patch.rect.top) return 1;
            else return -1;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"page\":" + page +
                ", \"bitmap\":" + bitmap +
                ", \"rect\":" + rect +
                ", \"scale\":" + scale +
                ", \"state\":" + state +
                '}';
    }
}
