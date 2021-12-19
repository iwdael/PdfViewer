package com.iwdael.pdfviewer.util;

import android.content.Context;
import android.util.TypedValue;

public class Utils {
    public static int getDP(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
