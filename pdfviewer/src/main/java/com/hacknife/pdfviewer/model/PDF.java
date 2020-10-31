package com.hacknife.pdfviewer.model;

import android.graphics.Bitmap;

import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.RectI;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;
import com.hacknife.pdfviewer.state.ScaleMode;

public class PDF {
    public int pageNumber;
    public Page page;
    public SizeF size;

    public PDF(int pageNumber, Page page) {
        this.pageNumber = pageNumber;
        this.page = page;
        Rect b = page.getBounds();
        this.size = new SizeF(b.x1 - b.x0, b.y1 - b.y0);

    }

        public  void drawPage(Bitmap bm,   int pageSize, ScaleMode mode, float offsetX, float offsetY, float scale) {

        Matrix ctm = new Matrix(1, 1);
        RectI box = new RectI(this.page.getBounds().transform(ctm));
        if (mode == ScaleMode.WIDTH)
            scale = (float) pageSize / (float) (box.x1 - box.x0) * scale;
        else
            scale = (float) pageSize / (float) (box.y1 - box.y0) * scale;
        ctm.scale(scale, scale);
        AndroidDrawDevice dev = new AndroidDrawDevice(bm, (int) offsetX, (int) offsetY);
        page.run(dev, ctm, null);
        dev.close();
        dev.destroy();
    }

    @Override
    public String toString() {
        return "{" +
                "\"size\":" + size.newScale(1 / size.width) +
                '}';
    }


}
