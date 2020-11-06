package com.hacknife.pdfviewer.core.mupdf.source;

import android.content.Context;

import com.hacknife.pdfviewer.core.CoreSource;
import com.hacknife.pdfviewer.core.DocumentSource;
import com.hacknife.pdfviewer.core.mupdf.kernel.PdfCoreSource;
import com.hacknife.pdfviewer.core.mupdf.kernel.PdfDocument;

import java.io.IOException;

public class ByteArraySource implements DocumentSource {
    private byte[] bytes;

    public ByteArraySource(byte[] path) {
        this.bytes = path;
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        PdfDocument document = PdfDocument.openDocument(bytes, "pdf");
        if (document.needsPassword() && password != null) document.authenticatePassword(password);
        return new PdfCoreSource(document);
    }
}
