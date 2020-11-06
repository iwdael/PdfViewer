package com.hacknife.pdfviewer.core.pdfium.source;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.hacknife.pdfviewer.core.CoreSource;
import com.hacknife.pdfviewer.core.DocumentSource;
import com.hacknife.pdfviewer.core.pdfium.kernel.PdfCoreSource;
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
