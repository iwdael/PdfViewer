package com.iwdael.pdfviewer.core.mupdf.source;

import android.content.Context;

import com.iwdael.pdfviewer.core.CoreSource;
import com.iwdael.pdfviewer.core.DocumentSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfCoreSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfDocument;

import java.io.File;
import java.io.IOException;

public class FileSource implements DocumentSource {
    File file;

    public FileSource(File file) {
        this.file = file;
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        PdfDocument document = PdfDocument.openDocument(file.getAbsolutePath());
        if (document.needsPassword() && password != null) document.authenticatePassword(password);
        return new PdfCoreSource(document);
    }
}