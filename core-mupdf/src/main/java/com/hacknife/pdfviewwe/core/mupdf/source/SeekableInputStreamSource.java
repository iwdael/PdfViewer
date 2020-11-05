package com.hacknife.pdfviewwe.core.mupdf.source;

import android.content.Context;

import com.artifex.mupdf.fitz.SeekableInputStream;
import com.hacknife.pdfviewer.core.CoreSource;
import com.hacknife.pdfviewer.core.DocumentSource;
import com.hacknife.pdfviewwe.core.mupdf.kernel.PdfCoreSource;
import com.hacknife.pdfviewwe.core.mupdf.kernel.PdfDocument;

import java.io.IOException;

public class SeekableInputStreamSource implements DocumentSource {
    private SeekableInputStream stream;

    public SeekableInputStreamSource(SeekableInputStream stream) {
        this.stream = stream;
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        PdfDocument document = PdfDocument.openDocument(stream, null);
        if (document.needsPassword() && password != null) document.authenticatePassword(password);
        return new PdfCoreSource(document);
    }
}
