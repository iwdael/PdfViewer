package com.iwdael.pdfviewer.core.mupdf.source;

import android.content.Context;

import com.iwdael.pdfviewer.core.CoreSource;
import com.iwdael.pdfviewer.core.DocumentSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfCoreSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfDocument;

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
