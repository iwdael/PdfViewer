package com.hacknife.pdfviewer.loader;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.listener.OnThumbnailListener;
import com.hacknife.pdfviewer.model.PDF;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.state.PatchState;
import com.hacknife.pdfviewer.state.ScaleMode;

public class PatchTask implements Runnable, Comparable<PatchTask> {
    public static final String TAG = PatchTask.class.getSimpleName();
    private Patch patch;
    private PDF pdf;
    private float scale;
    private int patchX;
    private int patchY;
    private int pageSize;
    private ScaleMode mode;
    private long createTime;
    private Configurator configurator;
    private int patchSize;
    public PatchTask(Patch patch, PDF pdf, float scale, int patchX, int patchY , int pageSize, ScaleMode mode, Configurator configurator) {
        this.patch = patch;
        this.pdf = pdf;
        this.scale = scale;
        this.patchX = patchX;
        this.patchY = patchY;
        this.pageSize = pageSize;
        this.mode = mode;
        this.configurator = configurator;
        this.patchSize = patch.bitmap.getWidth();
        this.createTime = System.currentTimeMillis();
        Logger.t(TAG).log("create task| page:%d , patchSize:%d , patchX:%d , patchY:%d , scale:%f ,pageSize:%d ", pdf.pageNumber, patch.bitmap.getWidth(),  patchX, patchY, scale,pageSize);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        pdf.drawBitmap(patch.bitmap, pageSize, mode, patchX, patchY, scale);
        patch.state = PatchState.PREPARE;
        patch.scale = scale;
        patch.rect.set(patchX, patchY, patchX + patchSize, patchY + patchSize);
        patch.page = pdf.pageNumber;
//        Logger.t(TAG).log("time:%d , page:%d , patchX:%d , patchY:%d , scale:%f ", (int) (System.currentTimeMillis() - startTime), pdf.pageNumber, patchX, patchY, scale);
        configurator.thumbnailCache().buffer(patch);
        for (OnThumbnailListener listener : configurator.thumbnailListeners()) {
            if (listener.onThumbnail(patch)) {
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

}
