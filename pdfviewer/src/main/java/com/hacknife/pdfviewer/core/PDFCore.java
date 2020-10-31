package com.hacknife.pdfviewer.core;

import android.graphics.Bitmap;

import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Matrix;

import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.RectI;
import com.artifex.mupdf.fitz.SeekableInputStream;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;

public class PDFCore {

    private Document pdfDocument;
    private int pageCount = -1;
    private Page page;


    public PDFCore(String filename) {
        this.pdfDocument = Document.openDocument(filename);
        this.pageCount = this.pdfDocument.countPages();
    }

    public PDFCore(byte[] buffer, String magic) {
        this.pdfDocument = Document.openDocument(buffer, magic);
        this.pageCount = this.pdfDocument.countPages();
    }

    public PDFCore(SeekableInputStream stream) {
        this.pdfDocument = Document.openDocument(stream, null);
        this.pageCount = this.pdfDocument.countPages();
    }


    public int pageCount() {
        return pageCount;
    }



    public synchronized Page getPage (int pageNum) {
        return this.pdfDocument.loadPage(pageNum);
    }


    public synchronized void close() {
        if (this.page != null) this.page.destroy();
        this.page = null;
        if (this.pdfDocument != null) this.pdfDocument.destroy();
        this.pdfDocument = null;
    }



    public synchronized boolean needsPassword() {
        return this.pdfDocument.needsPassword();
    }

    public synchronized boolean authenticatePassword(String password) {
        return this.pdfDocument.authenticatePassword(password);
    }

}
