package com.hacknife.pdfviewer.cache;

import android.graphics.Bitmap;

import com.hacknife.pdfviewer.helper.Logger;

import java.util.HashMap;
import java.util.Map;

public class ThumbnailCache {
    public static final String TAG = "thumbnail_cache";
    Map<Integer, Bitmap> bitmapMap;

    public ThumbnailCache() {
        this.bitmapMap =new HashMap<>();
    }

    public ThumbnailCache put(int page, Bitmap bitmap) {
        this.bitmapMap.put(page,bitmap);
        Logger.t(TAG).log("缓存：%d",page);
        return this;
    }
    public Bitmap getPage(int page){
       return this.bitmapMap.get(page);
    }
}
