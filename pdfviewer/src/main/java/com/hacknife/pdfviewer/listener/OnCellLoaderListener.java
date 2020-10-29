package com.hacknife.pdfviewer.listener;

import android.graphics.Bitmap;

public interface OnCellLoaderListener {

    Bitmap onLoadBitmap(Bitmap bitmap);

    void onDraw(Bitmap bitmap);
}
