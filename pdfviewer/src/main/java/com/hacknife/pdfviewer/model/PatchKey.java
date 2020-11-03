package com.hacknife.pdfviewer.model;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.hacknife.pdfviewer.state.PatchState;

import java.util.Objects;


public class PatchKey implements Comparable<PatchKey> {
    public int page;
    public Rect rect;
    public float scale;


    public PatchKey(int page, int left, int top, int right, int bottom, float scale) {
        this.page = page;
        this.rect = new Rect(left, top, right, bottom);
        this.scale = scale;
    }

    public PatchKey() {
        this(-1, -1, -1, -1, -1, 0f);
    }

    public void changeKey(int page, int left, int top, int right, int bottom, float scale) {
        this.page = page;
        this.rect.set(left, top, right, bottom);
        this.scale = scale;
    }

    public PatchKey clone() {
        return new PatchKey(page, rect.left, rect.top, rect.right, rect.bottom, scale);
    }

    public void clearKey() {
        this.page = -1;
        this.rect.set(-1, -1, -1, -1);
        this.scale = 0f;
    }

    @Override
    public int compareTo(PatchKey patch) {
        if (page < patch.page) {
            return -1;
        } else if (page > patch.page) {
            return 1;
        } else {
            if (rect.isEmpty() && patch.rect.isEmpty()) return 0;
            if (rect.isEmpty()) return -1;
            if (patch.rect.isEmpty()) return 1;
            if (rect.left == patch.rect.left) {
                if (rect.top == patch.rect.top) return 0;
                else return rect.top < patch.rect.top ? -1 : 1;
            } else if (rect.left < patch.rect.left) {
                if (rect.top == patch.rect.top) return -1;
                else if (rect.top > patch.rect.top) return 1;
                else return -1;
            } else {
                if (rect.top == patch.rect.top) return 1;
                else if (rect.top > patch.rect.top) return 1;
                else return -1;
            }
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"page\":" + page +
                ", \"rect\":" + rect +
                ", \"scale\":" + scale +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || (getClass() != o.getClass() && getClass() != o.getClass().getSuperclass()))
            return false;
        PatchKey patch = (PatchKey) o;
        return page == patch.page &&
                Float.compare(patch.scale, scale) == 0 &&
                Objects.equals(rect, patch.rect);
    }

}
