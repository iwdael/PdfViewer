package com.hacknife.pdfviewer.loader;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.hacknife.pdfviewer.listener.OnCellLoaderListener;

class PDFLoaderTask extends AsyncTask<Bitmap, Void, Bitmap> {
    OnCellLoaderListener loaderListener;
    boolean isCancel = false;

    public PDFLoaderTask(OnCellLoaderListener loaderListener) {
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

    public PDFLoaderTask exec(Bitmap bitmap) {
        execute(bitmap);
        return this;
    }

    public void cancelTask(boolean b) {
        isCancel = true;
        cancel(b);
    }
}
