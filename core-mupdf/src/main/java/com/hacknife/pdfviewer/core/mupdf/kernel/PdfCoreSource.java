package com.hacknife.pdfviewer.core.mupdf.kernel;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.RectI;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;
import com.hacknife.pdfviewer.core.CoreSource;
import com.hacknife.pdfviewer.core.model.Bookmark;
import com.hacknife.pdfviewer.core.model.Link;
import com.hacknife.pdfviewer.core.model.Meta;
import com.hacknife.pdfviewer.core.model.Size;

import java.util.ArrayList;
import java.util.List;

public class PdfCoreSource implements CoreSource {

    private PdfDocument document;

    public PdfCoreSource(PdfDocument document) {
        this.document = document;
    }

    public synchronized void renderPageBitmap(Bitmap bitmap, int docPage, int left, int top, int width, int height, boolean annotationRendering) {
        Page page = document.getPage(docPage);
        Rect rect = page.getBounds();
        Matrix ctm = new Matrix(1, 1);
        RectI box = new RectI(rect);
        float scaleX = (float) width / (float) (box.x1 - box.x0);
        float scaleY = (float) height / (float) (box.y1 - box.y0);
        ctm.scale(scaleX, scaleY);
        AndroidDrawDevice dev = new AndroidDrawDevice(bitmap, left, top);
        try {
            page.run(dev, ctm, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dev.close();
        dev.destroy();
    }

    public Meta getDocumentMeta() {
        return document.getDocumentMeta();
    }

    public List<Bookmark> getTableOfContents() {
        return document.getTableOfContents();
    }

    public List<Link> getPageLinks(int docPage) {
        return document.getPageLinks(docPage);
    }

    public RectF mapRectToDevice(int docPage, int startX, int startY, int sizeX, int sizeY, int i, RectF rect) {
        return new RectF();
    }

    public void closeDocument() {
        document.destroy();
    }

    public int getPageCount() {
        return document.pageCount();
    }

    @Override
    public Size getPageSize(int page) {
        return document.getPageSize(page);
    }
}

