package com.hacknife.pdfviewer.helper;

import android.util.Log;

public class Logger {


    public static log t(String tag) {
        return new log(tag);
    }

    public static class log {
        String tag;


        public log(String tag) {
            this.tag = tag.toLowerCase();
        }

        public void log(String mat, Object... content) {
            Log.v(String.format("dzq" + (tag == null ? "" : "-" + tag)), String.format(mat, content));
        }
    }
}
