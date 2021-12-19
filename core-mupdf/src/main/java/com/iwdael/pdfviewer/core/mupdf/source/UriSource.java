/*
 * Copyright (C) 2016 Bartosz Schiller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iwdael.pdfviewer.core.mupdf.source;


import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.iwdael.pdfviewer.core.CoreSource;
import com.iwdael.pdfviewer.core.DocumentSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfCoreSource;
import com.iwdael.pdfviewer.core.mupdf.kernel.PdfDocument;
import com.iwdael.pdfviewer.core.mupdf.util.Util;
import java.io.IOException;

public class UriSource implements DocumentSource {

    private Uri uri;

    public UriSource(Uri uri) {
        this.uri = uri;
    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
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
