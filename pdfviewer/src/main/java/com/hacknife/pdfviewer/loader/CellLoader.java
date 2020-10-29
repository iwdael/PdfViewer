package com.hacknife.pdfviewer.loader;

import android.graphics.Bitmap;

import com.hacknife.pdfviewer.listener.OnCellLoaderListener;

public class CellLoader {
    OnCellLoaderListener cellLoaderListener;
    CellLoaderTask task;

    public CellLoader(OnCellLoaderListener cellLoaderListener) {
        this.cellLoaderListener = cellLoaderListener;
    }


    public void load(Bitmap bitmap) {
        if (task != null) task.cancelTask(true);
        task = new CellLoaderTask(cellLoaderListener).exec(bitmap);
    }
}
