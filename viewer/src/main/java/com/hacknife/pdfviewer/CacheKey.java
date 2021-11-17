package com.hacknife.pdfviewer;

import android.graphics.RectF;

import java.util.Objects;

class CacheKey {
    final int page;
    final RectF pageRelativeBounds;
    boolean isDirty = false;

    public CacheKey(int page, RectF pageRelativeBounds) {
        this.page = page;
        this.pageRelativeBounds = pageRelativeBounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey lruKey = (CacheKey) o;
        return page == lruKey.page &&
                Objects.equals(pageRelativeBounds, lruKey.pageRelativeBounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, pageRelativeBounds);
    }
}
