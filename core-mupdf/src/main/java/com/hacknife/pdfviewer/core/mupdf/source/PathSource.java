package com.hacknife.pdfviewer.core.mupdf.source;

import android.content.Context;

import com.hacknife.pdfviewer.core.CoreSource;
import com.hacknife.pdfviewer.core.DocumentSource;
import com.hacknife.pdfviewer.core.mupdf.kernel.PdfCoreSource;
import com.hacknife.pdfviewer.core.mupdf.kernel.PdfDocument;

import java.io.IOException;

public class PathSource implements DocumentSource {
    String path;

    public PathSource(String path) {
        this.path = path;
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        PdfDocument document = PdfDocument.openDocument(path);
        if (document.needsPassword() && password != null) document.authenticatePassword(password);
        return new PdfCoreSource(document);
    }
}
