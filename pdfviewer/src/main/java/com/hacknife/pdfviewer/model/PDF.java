package com.hacknife.pdfviewer.model;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.RectI;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.state.ScaleMode;

public class PDF {
    public static final String TAG = PDF.class.getSimpleName();
    public final int pageNumber;
    private final Page page;
    public final SizeF size;
    private final RectF bounds;

    public PDF(int pageNumber, Page page) {
        this.pageNumber = pageNumber;
        this.page = page;
        Rect b = page.getBounds();
        this.bounds = new RectF(b.x0, b.y0, b.x1, b.y1);
        this.size = new SizeF(bounds.width(), bounds.height());

    }

    public synchronized void drawBitmap(Bitmap bm, int pageSize, ScaleMode mode, int offsetX, int offsetY, float scale) {
//        Logger.t(TAG).log("lock:" + pageNumber);
        Matrix ctm = new Matrix(1, 1);
        RectI box = new RectI(new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom).transform(ctm));
        if (mode == ScaleMode.WIDTH)
            scale = (float) pageSize / (float) (box.x1 - box.x0) * scale;
        else
            scale = (float) pageSize / (float) (box.y1 - box.y0) * scale;
        ctm.scale(scale, scale);
        AndroidDrawDevice dev = new AndroidDrawDevice(bm,  offsetX,   offsetY);
        page.run(dev, ctm, null);
        dev.close();
        dev.destroy();
//        Logger.t(TAG).log("release:" + pageNumber);
    }


}
