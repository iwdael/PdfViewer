package com.hacknife.pdfviewer.cache;

import android.graphics.Bitmap;

import androidx.core.graphics.PathSegment;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.model.PatchKey;
import com.hacknife.pdfviewer.model.PatchSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ThumbnailCache {
    public static final String TAG = "thumbnail_cache";

    private final List<Patch> patches;
    private final List<Patch> rubbish;
    private final Map<PatchKey, PatchSet> patchBuffer;
    private final Map<PatchKey, PatchSet> patchReveal;
    private final int thumbnailPatchCount;

    public ThumbnailCache(Configurator configurator) {
        this.patches = new ArrayList<>();
        this.rubbish = new ArrayList<>();
        this.patchBuffer = new HashMap<>();
        this.patchReveal = new HashMap<>();
        this.thumbnailPatchCount = configurator.thumbnailPatchCount();
        int patchSize = configurator.thumbnailPatchSize();
        for (int i = 0; i < thumbnailPatchCount; i++) {
            Patch patch = new Patch(patchSize, patchSize);
            patches.add(patch);
            rubbish.add(patch);
        }
    }


    public void buffer(Patch patch) {
        PatchKey key = new PatchKey(patch.page, patch.scale);
        PatchSet set = patchBuffer.get(key);
        if (set == null) {
            set = new PatchSet(patch.page);
            patchBuffer.put(key, set);
        }
        set.patches.add(patch);
    }

    public PatchSet buffer(PatchKey key) {
        return patchBuffer.get(key);
    }

    public void reveal(Patch patch) {
        PatchKey key = new PatchKey(patch.page, patch.scale);
        PatchSet set = patchReveal.get(key);
        if (set == null) {
            set = new PatchSet(patch.page);
            patchReveal.put(key, set);
        }
        set.patches.add(patch);
    }

    public PatchSet reveal(PatchKey key) {
        PatchSet buffer = buffer(key);
        PatchSet reveal = patchReveal.get(key);
        if (buffer != null) {
            patchBuffer.remove(key);
            if (reveal != null) {
                for (Patch patch : buffer.patches) {
                    reveal.patches.add(patch);
                }
            } else {
                patchReveal.put(key, buffer);
                reveal = buffer;
            }
        }
        return reveal;
    }

    public Patch rubbish() {
        if (rubbish.isEmpty()) return null;
        return rubbish.remove(0);
    }
}
