package com.hacknife.pdfviewer.core;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.ParcelFileDescriptor;

import com.hacknife.pdfviewer.core.model.Bookmark;
import com.hacknife.pdfviewer.core.model.Link;
import com.hacknife.pdfviewer.core.model.Meta;
import com.hacknife.pdfviewer.core.model.Size;

import java.util.List;

public interface CoreSource {

    void renderPageBitmap(Bitmap bitmap, int docPage, int left, int top, int width, int height, boolean annotationRendering);

    Meta getDocumentMeta();

    List<Bookmark> getTableOfContents();

    List<Link> getPageLinks(int docPage);

    RectF mapRectToDevice(int docPage, int startX, int startY, int sizeX, int sizeY, int i, RectF rect);

    void closeDocument();

    int getPageCount();

    Size getPageSize(int page);
}
