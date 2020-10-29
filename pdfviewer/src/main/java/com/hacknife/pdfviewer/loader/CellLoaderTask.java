package com.hacknife.pdfviewer.loader;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.hacknife.pdfviewer.listener.OnCellLoaderListener;

class CellLoaderTask extends AsyncTask<Bitmap, Void, Bitmap> {
    OnCellLoaderListener loaderListener;
    boolean isCancel = false;

    public CellLoaderTask(OnCellLoaderListener loaderListener) {
        this.loaderListener = loaderListener;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps) {
        if (isCancel) return null;
        return loaderListener.onLoadBitmap(bitmaps[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancel) return;
        loaderListener.onDraw(bitmap);
    }

    public CellLoaderTask exec(Bitmap bitmap) {
        execute(bitmap);
        return this;
    }

    public void cancelTask(boolean b) {
        isCancel = true;
        cancel(b);
    }
}
