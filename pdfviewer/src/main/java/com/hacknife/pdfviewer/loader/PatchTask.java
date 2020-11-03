package com.hacknife.pdfviewer.loader;

import android.graphics.Rect;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.cache.ThumbnailCache;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.listener.OnThumbnailListener;
import com.hacknife.pdfviewer.model.PDF;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.model.PatchKey;
import com.hacknife.pdfviewer.state.PatchState;
import com.hacknife.pdfviewer.state.ScaleMode;

import java.util.Objects;

import static com.hacknife.pdfviewer.state.PatchState.PREPARE;

public class PatchTask implements Runnable, Comparable<PatchTask> {
    public static final String TAG = PatchTask.class.getSimpleName();


    private final int pageSize;
    private final ScaleMode mode;
    private final long createTime;
    private final Configurator configurator;
    public final PatchKey key;
    private final ThumbnailCache thumbnailCache;

    public PatchTask(PatchKey key, int pageSize, ScaleMode mode, Configurator configurator) {
        this.key = key;
        this.pageSize = pageSize;
        this.mode = mode;
        this.configurator = configurator;
        this.thumbnailCache = configurator.thumbnailCache();
        this.createTime = System.currentTimeMillis();
        Logger.t(TAG).log("create task| page:%d , patchLeft:%d , patchTop:%d , patchRight:%d , patchBottom:%d , scale:%f ,pageSize:%d ", key.page, key.rect.left, key.rect.top, key.rect.right, key.rect.bottom, key.scale, pageSize);
    }

    @Override
    public void run() {
        //检测是否已经加载了
        if (thumbnailCache.achieve(key) != null) {
            Logger.t(TAG).log("任务已经完成，终结:" + key.toString());
            return;
        }

        Patch patch = configurator.thumbnailCache().rubbish();
        if (patch == null) {
            Logger.t(TAG).log("没有碎片了");
            return;
        } else {
            patch.clearKey();
        }
        long startTime = System.currentTimeMillis();
        PDF pdf = configurator.pageCache().getPage(this.key.page);
        pdf.drawBitmap(patch.bitmap, pageSize, mode, this.key.rect.left, this.key.rect.top, this.key.scale);
        patch.state = PatchState.PREPARING;
        patch.scale = this.key.scale;
        patch.rect.set(this.key.rect);
        patch.page = pdf.pageNumber;
        patch.state = PatchState.PREPARED;//drawBitmap 完成
//        Logger.t(TAG).log("time:%d , page:%d , patchX:%d , patchY:%d , scale:%f ", (int) (System.currentTimeMillis() - startTime), pdf.pageNumber, patchX, patchY, scale);
        Logger.t(TAG).log("time:%d , %s", (int) (System.currentTimeMillis() - startTime), patch.toString());
        configurator.thumbnailCache().notifyDataChange(patch);
        for (OnThumbnailListener listener : configurator.thumbnailListeners()) {
            if (listener.onThumbnail(patch)) {
                listener.onReload();
                break;
            }
        }
    }

    @Override
    public int compareTo(PatchTask patchTask) {
        if (createTime > patchTask.createTime) return 1;
        else if (createTime < patchTask.createTime) return -1;
        else return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatchTask task = (PatchTask) o;
        return mode == task.mode && this.key.equals(task.key);

    }


}
