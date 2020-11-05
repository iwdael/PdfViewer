package com.hacknife.pdfviewwe.core.mupdf.kernel;

import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.SeekableInputStream;
import com.artifex.mupdf.fitz.SeekableOutputStream;
import com.hacknife.pdfviewer.core.model.Size;

public class PdfDocument {
    private Document document;

    public PdfDocument(Document document) {
        this.document = document;
    }


    public Size getPageSize(int page) {
        Rect b = getPage(page).getBounds();
        return new Size((int) Math.abs(b.x1 - b.x0), (int) Math.abs(b.y1 - b.y0));
    }

    public void destroy() {
        document.destroy();
    }


    public int pageCount() {
        return document.countPages();
    }

    public static PdfDocument openDocument(String filename) {
        return new PdfDocument(Document.openDocument(filename));
    }

    public static PdfDocument openDocument(String filename, String accelerator) {
        return new PdfDocument(Document.openDocument(filename, accelerator));
    }

    public static PdfDocument openDocument(String filename, SeekableInputStream accelerator) {
        return new PdfDocument(Document.openDocument(filename, accelerator));
    }

    public static PdfDocument openDocument(byte[] buffer, String magic) {
        return new PdfDocument(Document.openDocument(buffer, magic));
    }

    public static PdfDocument openDocument(byte[] buffer, String magic, byte[] accelerator) {
        return new PdfDocument(Document.openDocument(buffer, magic, accelerator));
    }

    public static PdfDocument openDocument(SeekableInputStream stream, String magic) {
        return new PdfDocument(Document.openDocument(stream, magic));
    }

    public static PdfDocument openDocument(SeekableInputStream stream, String magic, SeekableInputStream accelerator) {
        return new PdfDocument(Document.openDocument(stream, magic, accelerator));
    }

    public static boolean recognize(String var0) {
        return Document.recognize(var0);
    }

    public void saveAccelerator(String var1) {
        document.saveAccelerator(var1);
    }

    public void outputAccelerator(SeekableOutputStream var1) {
        document.outputAccelerator(var1);
    }

    public boolean needsPassword() {
        return document.needsPassword();
    }

    public boolean authenticatePassword(String password) {
        return document.authenticatePassword(password);
    }

    public int countChapters() {
        return document.countChapters();
    }


    public Page getPage(int pageNumber) {
        return document.loadPage(pageNumber);
    }
}
