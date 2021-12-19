package com.iwdael.pdfviewer.core.pdfium.source;

import android.content.Context;
import android.os.ParcelFileDescriptor;

import com.iwdael.pdfviewer.core.CoreSource;
import com.iwdael.pdfviewer.core.DocumentSource;
import com.iwdael.pdfviewer.core.pdfium.kernel.PdfCoreSource;
import com.shockwave.pdfium.PdfiumCore;

import java.io.IOException;

public class ParcelFileDescriptorSource implements DocumentSource {

    private ParcelFileDescriptor  pfd;

    public ParcelFileDescriptorSource(ParcelFileDescriptor pfd) {
        this.pfd = pfd;
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        return PdfCoreSource.create(pdfiumCore.newDocument(pfd, password), pdfiumCore);
    }
}
