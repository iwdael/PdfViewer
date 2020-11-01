package com.hacknife.pdfviewer.model;

import java.util.Objects;

public class PatchKey {
    int pageNumber;
    float scale;

    public PatchKey(int pageNumber, float scale) {
        this.pageNumber = pageNumber;
        this.scale = scale;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PatchKey patchKey = (PatchKey) o;
        return pageNumber == patchKey.pageNumber && Float.compare(patchKey.scale, scale) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, scale);
    }
}
