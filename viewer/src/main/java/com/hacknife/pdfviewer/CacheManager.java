/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hacknife.pdfviewer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;

import com.hacknife.pdfviewer.model.PagePart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hacknife.pdfviewer.util.Constants.Cache.CACHE_SIZE;
import static com.hacknife.pdfviewer.util.Constants.Cache.THUMBNAILS_CACHE_SIZE;

class CacheManager {

    private final LinkedHashMap<CacheKey, PagePart> cacheThumbnails;
    private final LinkedHashMap<CacheKey, PagePart> cachePart;
    private final Object cachePartLock = new Object();
    private final Object cacheThumbnailsLock = new Object();

    public CacheManager() {
        cacheThumbnails = new LinkedHashMap<>(0, 0.75f, true);
        cachePart = new LinkedHashMap<>(0, 0.75f, true);
    }


    public void cachePart(PagePart part) {
        synchronized (cachePartLock) {
            makeAFreeSpace();
            cachePart.put(new CacheKey(part.getPage(), part.getPageRelativeBounds()), part);
        }
    }

    public void makeANewSet() {
        synchronized (cachePartLock) {
            for (CacheKey key : cachePart.keySet()) key.isDirty = true;
        }
    }

    private void makeAFreeSpace() {
        synchronized (cachePartLock) {
            while (true) {
                if (cachePart.isEmpty()) return;
                Set<CacheKey> keySet = cachePart.keySet();
                if (keySet.size() >= CACHE_SIZE) {
                    PagePart part = cachePart.remove(keySet.iterator().next());
                    if (part != null) part.getRenderedBitmap().recycle();
                } else {
                    break;
                }
            }
        }

        synchronized (cacheThumbnailsLock) {
            while (true) {
                if (cacheThumbnails.isEmpty()) return;
                Set<CacheKey> keySet = cacheThumbnails.keySet();
                if (keySet.size() >= THUMBNAILS_CACHE_SIZE) {
                    PagePart part = cacheThumbnails.remove(keySet.iterator().next());
                    if (part != null) part.getRenderedBitmap().recycle();
                } else {
                    break;
                }
            }
        }
    }

    public void cacheThumbnail(PagePart part) {
        synchronized (cacheThumbnailsLock) {
            makeAFreeSpace();
            cacheThumbnails.put(new CacheKey(part.getPage(), part.getPageRelativeBounds()), part);
        }

    }

    public boolean upPartIfContained(int page, RectF pageRelativeBounds) {
        CacheKey key = new CacheKey(page, pageRelativeBounds);
        synchronized (cachePartLock) {
            for (Map.Entry<CacheKey, PagePart> entry : cachePart.entrySet()) {
                if (entry.getKey().equals(key)) {
                    entry.getKey().isDirty = false;
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * Return true if already contains the described PagePart
     */
    public boolean containsThumbnail(int page, RectF pageRelativeBounds) {
        CacheKey key = new CacheKey(page, pageRelativeBounds);
        synchronized (cacheThumbnailsLock) {
            for (Map.Entry<CacheKey, PagePart> entry : cacheThumbnails.entrySet()) {
                if (entry.getKey().equals(key)) {
                    entry.getKey().isDirty = false;
                    return true;
                }
            }
            return false;
        }
    }


    public List<PagePart> getPageParts(int[] range) {
        List<PagePart> parts = new ArrayList<>();
        synchronized (cachePartLock) {
            List<CacheKey> keys = new ArrayList<>(cachePart.keySet());
            for (CacheKey key : keys) {
                if (key.page >= range[0] && key.page <= range[1] && !key.isDirty) {
                    PagePart part = cachePart.get(key);
                    if (part != null) parts.add(part);
                }
            }
            return parts;
        }
    }

    public List<PagePart> getThumbnails(int[] range) {
        List<PagePart> parts = new ArrayList<>();
        synchronized (cacheThumbnailsLock) {
            List<CacheKey> keys = new ArrayList<>(cacheThumbnails.keySet());
            for (CacheKey key : keys) {
                if (key.page >= range[0] && key.page <= range[1] && !key.isDirty) {
                    PagePart part = cacheThumbnails.get(key);
                    if (part != null) parts.add(part);
                }
            }
            return parts;
        }
    }

    public void recycle() {
        synchronized (cachePartLock) {
            for (PagePart part : cachePart.values()) {
                part.getRenderedBitmap().recycle();
            }
            cachePart.clear();
        }
        synchronized (cacheThumbnailsLock) {
            for (PagePart part : cacheThumbnails.values()) {
                part.getRenderedBitmap().recycle();
            }
            cacheThumbnails.clear();
        }
    }

    public Bitmap getRenderedBitmap(int width, int height, Bitmap.Config config, boolean thumbnail) {
        if (!thumbnail)
            synchronized (cachePartLock) {
                if (cachePart.isEmpty()) return Bitmap.createBitmap(width, height, config);
                if (cachePart.size() < CACHE_SIZE - 10)
                    return Bitmap.createBitmap(width, height, config);
                CacheKey key = cachePart.keySet().iterator().next();
                PagePart part = cachePart.remove(key);
                if (part != null) {
                    Bitmap bitmap = part.getRenderedBitmap();
                    bitmap.eraseColor(Color.TRANSPARENT);
                    return bitmap;
                }
                return Bitmap.createBitmap(width, height, config);
            }
        else
            synchronized (cacheThumbnailsLock) {
                if (cacheThumbnails.isEmpty()) return Bitmap.createBitmap(width, height, config);
                if (cacheThumbnails.size() < THUMBNAILS_CACHE_SIZE)
                    return Bitmap.createBitmap(width, height, config);
                CacheKey key = cacheThumbnails.keySet().iterator().next();
                PagePart part = cacheThumbnails.remove(key);
                if (part != null) {
                    return part.getRenderedBitmap();
                }
                return Bitmap.createBitmap(width, height, config);
            }
    }
}
