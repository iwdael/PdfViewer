package com.hacknife.pdfviewer.model;

import android.graphics.Bitmap;

import com.hacknife.pdfviewer.state.PatchState;

public class Patch extends PatchKey {
    public Bitmap bitmap;
    public PatchState state;

    public Patch(int width, int height) {
        super(-1, -1, -1, -1, -1, -1);
        this.state = PatchState.PREPARED; //创建Patch
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }




}
