package com.hacknife.pdfviewwe.core.mupdf.source;

import android.content.Context;

import com.artifex.mupdf.fitz.SeekableInputStream;
import com.hacknife.pdfviewer.core.CoreSource;
import com.hacknife.pdfviewer.core.DocumentSource;
import com.hacknife.pdfviewwe.core.mupdf.kernel.PdfCoreSource;
import com.hacknife.pdfviewwe.core.mupdf.kernel.PdfDocument;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileSource implements SeekableInputStream, DocumentSource {
    private RandomAccessFile file;

    public RandomAccessFileSource(RandomAccessFile randomAccessFile) {
        this.file = randomAccessFile;
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        this.file.write(buf, off, len);
    }

    public long seek(long offset, int whence) throws IOException {
        switch (whence) {
            case 0:
                this.file.seek(offset);
                break;
            case 1:
                this.file.seek(this.file.getFilePointer() + offset);
                break;
            case 2:
                this.file.seek(this.file.length() + offset);
        }

        return this.file.getFilePointer();
    }

    public long position() throws IOException {
        return this.file.getFilePointer();
    }


    public int read(byte[] buf) throws IOException {
        return this.file.read(buf);
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        PdfDocument document = PdfDocument.openDocument(this, null);
        if (document.needsPassword() && password != null) document.authenticatePassword(password);
        return new PdfCoreSource(document);
    }

}
