package com.iwdael.pdfviewer.core.mupdf.source;

import android.content.Context;

import com.artifex.mupdf.fitz.SeekableInputStream;
import com.iwdael.pdfviewer.core.CoreSource;
import com.iwdael.pdfviewer.core.DocumentSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfCoreSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfDocument;

import java.io.IOException;

public class SeekableInputStreamSource implements DocumentSource {
    private SeekableInputStream stream;

    public SeekableInputStreamSource(SeekableInputStream stream) {
        this.stream = stream;
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        PdfDocument document = PdfDocument.openDocument(stream, "pdf");
        if (document.needsPassword() && password != null) document.authenticatePassword(password);
        return new PdfCoreSource(document);
    }
}
