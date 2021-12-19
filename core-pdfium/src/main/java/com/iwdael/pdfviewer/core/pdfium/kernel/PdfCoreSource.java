package com.iwdael.pdfviewer.core.pdfium.kernel;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.iwdael.pdfviewer.core.CoreSource;
import com.iwdael.pdfviewer.core.model.Bookmark;
import com.iwdael.pdfviewer.core.model.Link;
import com.iwdael.pdfviewer.core.model.Meta;
import com.iwdael.pdfviewer.core.model.Size;
import com.iwdael.pdfviewer.core.pdfium.util.InformationConverter;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.util.List;

public class PdfCoreSource implements CoreSource {
    private PdfDocument document;
    private PdfiumCore core;

    private PdfCoreSource(PdfDocument document, PdfiumCore core) {
        this.document = document;
        this.core = core;
    }


    public static PdfCoreSource create(PdfDocument document, PdfiumCore core) {
        return new PdfCoreSource(document, core);
    }

    @Override
    public void renderPageBitmap(Bitmap bitmap, int docPage, int left, int top, int width, int height, boolean annotationRendering) {
        core.openPage(document, docPage);
        core.renderPageBitmap(document, bitmap, docPage, -left, -top, width, height, annotationRendering);
    }

    @Override
    public Meta getDocumentMeta() {
        return InformationConverter.metaConvert(core.getDocumentMeta(document));
    }

    @Override
    public List<Bookmark> getTableOfContents() {
        return InformationConverter.bookmarkConvert(core.getTableOfContents(document));
    }

    @Override
    public List<Link> getPageLinks(int docPage) {
        return InformationConverter.linkConvert(core.getPageLinks(document, docPage));
    }

    @Override
    public RectF mapRectToDevice(int docPage, int startX, int startY, int sizeX, int sizeY, int i, RectF rect) {
        return core.mapRectToDevice(document, docPage, startX, startY, sizeX, sizeY, i, rect);
    }

    @Override
    public void closeDocument() {
        core.closeDocument(document);
    }

    @Override
    public int getPageCount() {
        return core.getPageCount(document);
    }

    @Override
    public Size getPageSize(int page) {
        com.shockwave.pdfium.util.Size size = core.getPageSize(document, page);
        return new Size(size.getWidth(), size.getHeight());
    }
}
