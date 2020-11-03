package com.hacknife.pdfviewer.helper;

import android.graphics.Rect;
import android.graphics.RectF;

import com.hacknife.pdfviewer.Configurator;
import com.hacknife.pdfviewer.cache.PageCache;
import com.hacknife.pdfviewer.cache.ThumbnailCache;
import com.hacknife.pdfviewer.loader.ThumbnailPool;
import com.hacknife.pdfviewer.model.Patch;
import com.hacknife.pdfviewer.loader.PatchTask;
import com.hacknife.pdfviewer.model.PDF;
import com.hacknife.pdfviewer.model.PatchKey;
import com.hacknife.pdfviewer.model.Size;
import com.hacknife.pdfviewer.model.SizeF;
import com.hacknife.pdfviewer.state.ScaleMode;

public class TaskFactory {
    public static final String TAG = TaskFactory.class.getSimpleName();

    public static void createThumbnailTask(Configurator configurator) {


        ScaleMode mode = configurator.scaleMode();
        PageCache pageCache = configurator.pageCache();
        ThumbnailPool thumbnailPool = configurator.thumbnailPool();
        ThumbnailCache thumbnailCache = configurator.thumbnailCache();
        float thumbnailScale = configurator.thumbnailScale();


        //容器大小
        int pageSize = configurator.pageSize();
        //缩略图大小
        int thumbnailLandscapeSize = configurator.thumbnailLandscapeSize();
        int thumbnailPatchSize = configurator.thumbnailPatchSize();
        int thumbnailPatchCount = configurator.thumbnailPatchCount();
        Logger.t(TAG).log("thumbnailPatchCount：" + thumbnailPatchCount);
        //碎片加载数量
        int numberOfPatchLoading = 0;
        for (int page = configurator.pageNumber(); page < configurator.core().pageCount() && numberOfPatchLoading < thumbnailPatchCount; page++) {
            PDF pdf = pageCache.getPage(page);
            SizeF size = pdf.size;
            int thumbnailPortraitSize = (int) (size.height / size.width * thumbnailLandscapeSize);
            Logger.t(TAG).log("create thumbnail page| page:%d , landscape size:%d , portrait size:%d", page, thumbnailLandscapeSize, thumbnailPortraitSize);
            for (int portraitSize = 0; portraitSize < thumbnailPortraitSize && numberOfPatchLoading < thumbnailPatchCount; ) {
                for (int landscapeSize = 0; landscapeSize < thumbnailLandscapeSize && numberOfPatchLoading < thumbnailPatchCount; ) {
                    thumbnailPool.push(
                            new PatchTask(
                                    new PatchKey(
                                            page,
                                            landscapeSize,
                                            portraitSize,
                                            landscapeSize + (thumbnailPatchSize + landscapeSize > thumbnailLandscapeSize ? thumbnailLandscapeSize - landscapeSize : thumbnailPatchSize),
                                            portraitSize + (thumbnailPatchSize + portraitSize > thumbnailPortraitSize ? thumbnailPortraitSize - portraitSize : thumbnailPatchSize),
                                            thumbnailScale
                                    ),
                                    pageSize,
                                    mode,
                                    configurator)
                    );
                    landscapeSize += thumbnailPatchSize;
                    numberOfPatchLoading++;
                }
                portraitSize += thumbnailPatchSize;
            }
        }

    }
}
