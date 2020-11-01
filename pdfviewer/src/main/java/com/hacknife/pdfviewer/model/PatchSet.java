package com.hacknife.pdfviewer.model;
import java.util.SortedSet;
import java.util.TreeSet;

public class PatchSet {
    public int pageNumber;
    public SortedSet<Patch> patches;

    public PatchSet(int pageNumber) {
        this.pageNumber = pageNumber;
        this.patches = new TreeSet<>();
    }
}
