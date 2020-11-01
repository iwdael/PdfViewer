package com.hacknife.pdfviewer.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.model.PatchKey;
import com.hacknife.pdfviewer.model.PatchSet;

public class Page extends View {
    public static final String TAG = Page.class.getSimpleName();
    protected final Configurator configurator;
    public int pageNumber = -1;

    public Page(Context context, Configurator configurator) {
        super(context);
        this.configurator = configurator;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#FF6200EE"));
        drawThumbnail(canvas);
    }

    private void drawThumbnail(Canvas canvas) {
        PatchSet set = configurator.thumbnailCache().reveal(new PatchKey(pageNumber, configurator.thumbnailScale()));
        if (set == null) return;
        Logger.t(TAG).log("开始重绘："+pageNumber);
        for (Patch patch : set.patches) {
            Logger.t(TAG).log(patch.toString());
            canvas.drawBitmap(patch.bitmap,
                    new Rect(0, 0, patch.rect.width(),
                            patch.rect.height()),
                    new RectF(patch.rect.left / configurator.thumbnailScale() * configurator.scale(), patch.rect.top / configurator.thumbnailScale() * configurator.scale(), patch.rect.right / configurator.thumbnailScale() * configurator.scale(), patch.rect.bottom / configurator.thumbnailScale() * configurator.scale()),
                    null);
        }
    }

    protected void setPage(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
