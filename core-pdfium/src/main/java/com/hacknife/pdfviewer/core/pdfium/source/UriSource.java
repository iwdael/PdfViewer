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
package com.hacknife.pdfviewer.core.pdfium.source;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.hacknife.pdfviewer.core.CoreSource;
import com.hacknife.pdfviewer.core.DocumentSource;


import java.io.IOException;

public class UriSource implements DocumentSource {

    private Uri uri;

    public UriSource(Uri uri) {
        this.uri = uri;
    }

//    @Override
//    public DocumentSource createDocument(Context context, CoreSource core, String password) throws IOException {
//        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
//        return core.newDocument(pfd, password);
//    }

    @Override
    public CoreSource createCore(Context context, String password) throws IOException {
        return null;
    }
}