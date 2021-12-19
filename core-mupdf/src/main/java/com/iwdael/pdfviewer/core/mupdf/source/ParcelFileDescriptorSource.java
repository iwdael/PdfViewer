package com.iwdael.pdfviewer.core.mupdf.source;


import android.content.Context;
import android.os.ParcelFileDescriptor;

import com.iwdael.pdfviewer.core.CoreSource;
import com.iwdael.pdfviewer.core.DocumentSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfCoreSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfDocument;
import com.iwdael.pdfviewer.core.mupdf.util.Util;

import java.io.IOException;

public class ParcelFileDescriptorSource implements DocumentSource {

    private ParcelFileDescriptor pfd;

    public ParcelFileDescriptorSource(ParcelFileDescriptor pfd) {
        this.pfd = pfd;
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        assert pfd != null;
        PdfDocument document;
        if (Util.FileDescriptor2File(pfd.getFileDescriptor()) != null) {
            document = PdfDocument.openDocument(Util.FileDescriptor2File(pfd.getFileDescriptor()).getAbsolutePath());
        } else {
            document = PdfDocument.openDocument(Util.toByteArray(pfd), "pdf");
        }
        if (document.needsPassword() && password != null) document.authenticatePassword(password);
        return new PdfCoreSource(document);
    }
}
