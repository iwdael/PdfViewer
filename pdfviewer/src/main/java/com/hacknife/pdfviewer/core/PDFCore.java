package com.hacknife.pdfviewer.core;

import android.graphics.Bitmap;

import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Matrix;

import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.RectI;
import com.artifex.mupdf.fitz.SeekableInputStream;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;
import com.hacknife.pdfviewer.model.SizeF;

public class PDFCore {

    private Document pdfDocument;
    private int pageCount = -1;
    private int currentPage;
    private Page page;
    private float pageWidth;
    private float pageHeight;


    public PDFCore(String filename) {
        this.pdfDocument = Document.openDocument(filename);
        this.pageCount = this.pdfDocument.countPages();
        this.currentPage = -1;
    }

    public PDFCore(byte[] buffer, String magic) {
        this.pdfDocument = Document.openDocument(buffer, magic);
        this.pageCount = this.pdfDocument.countPages();
        this.currentPage = -1;
    }

    public PDFCore(SeekableInputStream stream) {
        this.pdfDocument = Document.openDocument(stream, null);
        this.pageCount = this.pdfDocument.countPages();
        this.currentPage = -1;
    }


    public int pageCount() {
        return 10;
    }


    private synchronized void pageTo(int pageNum) {
        if (pageNum > this.pageCount - 1) {
            pageNum = this.pageCount - 1;
        } else if (pageNum < 0) {
            pageNum = 0;
        }

        if (pageNum != this.currentPage) {
            this.currentPage = pageNum;
            if (this.page != null) {
                this.page.destroy();
            }

            this.page = this.pdfDocument.loadPage(pageNum);
            Rect b = this.page.getBounds();
            this.pageWidth = b.x1 - b.x0;
            this.pageHeight = b.y1 - b.y0;
        }

    }

    public synchronized SizeF getPageSize(int pageNum) {
        this.pageTo(pageNum);
        return new SizeF(this.pageWidth, this.pageHeight);
//        return new SizeF(1, 1);
    }


    public synchronized void close() {
        if (this.page != null) this.page.destroy();
        this.page = null;
        if (this.pdfDocument != null) this.pdfDocument.destroy();
        this.pdfDocument = null;
    }

    public synchronized void drawPage(Bitmap bm, int pageNum, int pageSize, MODE mode, float offsetX, float offsetY, float scale) {
        this.pageTo(pageNum);
        Matrix ctm = new Matrix(1, 1);
        RectI box = new RectI(this.page.getBounds().transform(ctm));
        if (mode == MODE.WIDTH)
            scale = (float) pageSize / (float) (box.x1 - box.x0) * scale;
        else
            scale = (float) pageSize / (float) (box.y1 - box.y0) * scale;
        ctm.scale(scale, scale);
        AndroidDrawDevice dev = new AndroidDrawDevice(bm, (int) offsetX, (int) offsetY);
        page.run(dev, ctm, null);
        dev.close();
        dev.destroy();
    }

    public synchronized boolean needsPassword() {
        return this.pdfDocument.needsPassword();
    }

    public synchronized boolean authenticatePassword(String password) {
        return this.pdfDocument.authenticatePassword(password);
    }

    public enum MODE {
        WIDTH, HEIGHT
    }
}
