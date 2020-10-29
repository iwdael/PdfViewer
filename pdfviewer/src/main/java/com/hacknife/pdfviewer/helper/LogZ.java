package com.hacknife.pdfviewer.helper;

import android.util.Log;

public class LogZ {

    public static void log(String mat, Object... content) {
        Log.v("dzq", String.format(mat, content));
    }
}
