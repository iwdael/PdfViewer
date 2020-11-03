package com.hacknife.pdfviewer.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.cache.ThumbnailCache;
import com.hacknife.pdfviewer.helper.Logger;
import com.hacknife.pdfviewer.loader.PatchTask;
import com.hacknife.pdfviewer.loader.ThumbnailPool;
import com.hacknife.pdfviewer.model.PDF;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.model.PatchKey;
import com.hacknife.pdfviewer.model.PatchSet;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.model.SizeF;

public class Page extends View {
    public static final String TAG = Page.class.getSimpleName();
    protected final Configurator configurator;
    public int pageNumber = -1;
    private Paint separatorPaint = new Paint();
    private SizeF pdfSize;
    private final ThumbnailCache thumbnailCache;
    private final ThumbnailPool thumbnailPool;
    private int separator = 0;
    private PatchKey key;

    public Page(Context context, Configurator configurator) {
        super(context);
        this.configurator = configurator;
        this.separatorPaint.setColor(configurator.spaceColor());
        this.separatorPaint.setAntiAlias(true);
        this.separatorPaint.setStyle(Paint.Style.FILL);
        this.thumbnailCache = configurator.thumbnailCache();
        this.thumbnailPool = configurator.thumbnailPool();
        this.key = new PatchKey();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#FF6200EE"));
        drawSeparator(canvas);
        drawThumbnail(canvas);
    }

    private void drawSeparator(Canvas canvas) {
        if (pageNumber <= 0) return;
        canvas.drawRect(0, 0, configurator.scale() * configurator.packSize().width, configurator.space(), separatorPaint);
    }


    public PatchKey[] keys() {
        if (pageNumber == -1) return null;
        int thumbnailPatchSize = configurator.thumbnailPatchSize();
        int thumbnailLandscapeSize = configurator.thumbnailLandscapeSize();
        int thumbnailPortraitSize = (int) (pdfSize.height / pdfSize.width * thumbnailLandscapeSize);
        PatchKey[] key = new PatchKey[2];
        for (int portraitSize = 0; portraitSize < thumbnailPortraitSize; ) {
            for (int landscapeSize = 0; landscapeSize < thumbnailLandscapeSize; ) {
                int right = landscapeSize + (thumbnailPatchSize + landscapeSize > thumbnailLandscapeSize ? thumbnailLandscapeSize - landscapeSize : thumbnailPatchSize);
                int bottom = portraitSize + (thumbnailPatchSize + portraitSize > thumbnailPortraitSize ? thumbnailPortraitSize - portraitSize : thumbnailPatchSize);
                if (portraitSize == 0 && landscapeSize == 0) {
                    key[0] = new PatchKey(pageNumber,
                            landscapeSize,
                            portraitSize,
                            right,
                            bottom,
                            configurator.thumbnailScale());
                }
                key[1] = new PatchKey(pageNumber,
                        landscapeSize,
                        portraitSize,
                        right,
                        bottom,
                        configurator.thumbnailScale());
                landscapeSize += thumbnailPatchSize;
            }
            portraitSize += thumbnailPatchSize;
        }
        return key;
    }

    private void drawThumbnail(Canvas canvas) {
        if (pageNumber == -1) return;
        int thumbnailPatchSize = configurator.thumbnailPatchSize();
        int thumbnailLandscapeSize = configurator.thumbnailLandscapeSize();
        int thumbnailPortraitSize = (int) (pdfSize.height / pdfSize.width * thumbnailLandscapeSize);
        for (int portraitSize = 0; portraitSize < thumbnailPortraitSize; ) {
            for (int landscapeSize = 0; landscapeSize < thumbnailLandscapeSize; ) {
                key.changeKey(
                        pageNumber,
                        landscapeSize,
                        portraitSize,
                        landscapeSize + (thumbnailPatchSize + landscapeSize > thumbnailLandscapeSize ? thumbnailLandscapeSize - landscapeSize : thumbnailPatchSize),
                        portraitSize + (thumbnailPatchSize + portraitSize > thumbnailPortraitSize ? thumbnailPortraitSize - portraitSize : thumbnailPatchSize),
                        configurator.thumbnailScale()
                );
                Patch patch = thumbnailCache.achieve(key);
                if (patch != null) {
                    canvas.drawBitmap(patch.bitmap,
                            new Rect(0, 0, patch.rect.width(),
                                    patch.rect.height()),
                            new RectF(
                                    patch.rect.left / configurator.thumbnailScale() * configurator.scale(),
                                    separator + patch.rect.top / configurator.thumbnailScale() * configurator.scale(),
                                    patch.rect.right / configurator.thumbnailScale() * configurator.scale(),
                                    separator + patch.rect.bottom / configurator.thumbnailScale() * configurator.scale()),
                            null);
                } else {
                    thumbnailPool.push(new PatchTask(key.clone(), configurator.pageSize(), configurator.scaleMode(), configurator));
                }
                landscapeSize += thumbnailPatchSize;
            }
            portraitSize += thumbnailPatchSize;
        }

    }

    protected void setPage(int pageNumber) {
        this.pageNumber = pageNumber;
        if (pageNumber > 0) separator = configurator.space();
        else this.separator = 0;
        this.pdfSize = this.configurator.pageCache().getPage(pageNumber).size;
    }
}