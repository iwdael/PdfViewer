package com.hacknife.pdfviewer.core.pdfium.kernel;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.hacknife.pdfviewer.core.CoreSource;
import com.hacknife.pdfviewer.core.model.Bookmark;
import com.hacknife.pdfviewer.core.model.Link;
import com.hacknife.pdfviewer.core.model.Meta;
import com.hacknife.pdfviewer.core.model.Size;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class PdfCoreSource implements CoreSource {
    private PdfDocument document;

    public PdfCoreSource(PdfDocument document) {
        this.document = document;
    }

    @Override
    public void renderPageBitmap(Bitmap bitmap, int docPage, int left, int top, int width, int height, boolean annotationRendering) {

    }

    @Override
    public Meta getDocumentMeta() {
        return null;
    }

    @Override
    public List<Bookmark> getTableOfContents() {
        return null;
    }

    @Override
    public List<Link> getPageLinks(int docPage) {
        return null;
    }

    @Override
    public RectF mapRectToDevice(int docPage, int startX, int startY, int sizeX, int sizeY, int i, RectF rect) {
        return null;
    }

    @Override
    public void closeDocument() {

    }

    @Override
    public int getPageCount() {
        return 0;
    }

    @Override
    public Size getPageSize(int page) {
        return null;
    }
}
