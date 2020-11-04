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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskFactory {
    public static final String TAG = TaskFactory.class.getSimpleName();

    public static void createThumbnailTask(Configurator configurator) {
        ScaleMode mode = configurator.scaleMode();
        PageCache pageCache = configurator.pageCache();
        ThumbnailPool thumbnailPool = configurator.thumbnailPool();
        float thumbnailScale = configurator.thumbnailScale();
        //容器大小
        int pageSize = configurator.pageSize();
        //缩略图大小
        int thumbnailLandscapeSize = configurator.thumbnailLandscapeSize();
        int thumbnailPatchSize = configurator.thumbnailPatchSize();
        int thumbnailPatchCount = configurator.thumbnailPatchCount();
        //碎片加载数量
        int numberOfPatchLoading = 0;
        for (int page = configurator.pageNumber(); page < configurator.core().pageCount() && numberOfPatchLoading < thumbnailPatchCount; page++) {
            PDF pdf = pageCache.getPage(page);
            SizeF size = pdf.size;
            int thumbnailPortraitSize = (int) (size.height / size.width * thumbnailLandscapeSize);
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


    public static void createThumbnailPageTask(Configurator configurator, int pageNumber) {
        SizeF pdfSize = configurator.pageCache().getPage(pageNumber).size;
        ThumbnailPool thumbnailPool = configurator.thumbnailPool();
        int thumbnailPatchSize = configurator.thumbnailPatchSize();
        int thumbnailLandscapeSize = configurator.thumbnailLandscapeSize();
        int thumbnailPortraitSize = (int) (pdfSize.height / pdfSize.width * thumbnailLandscapeSize);
        for (int portraitSize = 0; portraitSize < thumbnailPortraitSize; ) {
            for (int landscapeSize = 0; landscapeSize < thumbnailLandscapeSize; ) {
                int right = landscapeSize + (thumbnailPatchSize + landscapeSize > thumbnailLandscapeSize ? thumbnailLandscapeSize - landscapeSize : thumbnailPatchSize);
                int bottom = portraitSize + (thumbnailPatchSize + portraitSize > thumbnailPortraitSize ? thumbnailPortraitSize - portraitSize : thumbnailPatchSize);
                thumbnailPool.push(new PatchTask(
                        new PatchKey(
                                pageNumber,
                                landscapeSize,
                                portraitSize,
                                right,
                                bottom,
                                configurator.thumbnailScale()
                        ),
                        configurator.pageSize(),
                        configurator.scaleMode(),
                        configurator
                ));
                landscapeSize += thumbnailPatchSize;
            }
            portraitSize += thumbnailPatchSize;
        }
    }


    //反向 起始
    public static void createThumbnailPatchTask(Configurator configurator, int pageNumber, int originate, int patchCount, boolean reverse) {
        SizeF pdfSize = configurator.pageCache().getPage(pageNumber).size;
        ThumbnailPool thumbnailPool = configurator.thumbnailPool();
        int thumbnailPatchSize = configurator.thumbnailPatchSize();
        int thumbnailLandscapeSize = configurator.thumbnailLandscapeSize();
        int thumbnailPortraitSize = (int) (pdfSize.height / pdfSize.width * thumbnailLandscapeSize);
        int count = 0;
        if (!reverse) {
            for (int portraitSize = 0; portraitSize < thumbnailPortraitSize; ) {
                for (int landscapeSize = 0; landscapeSize < thumbnailLandscapeSize; ) {
                    int right = landscapeSize + (thumbnailPatchSize + landscapeSize > thumbnailLandscapeSize ? thumbnailLandscapeSize - landscapeSize : thumbnailPatchSize);
                    int bottom = portraitSize + (thumbnailPatchSize + portraitSize > thumbnailPortraitSize ? thumbnailPortraitSize - portraitSize : thumbnailPatchSize);
                    if (count >= originate) {
                        PatchKey key = new PatchKey(
                                pageNumber,
                                landscapeSize,
                                portraitSize,
                                right,
                                bottom,
                                configurator.thumbnailScale()
                        );
                        thumbnailPool.push(new PatchTask(
                                key,
                                configurator.pageSize(),
                                configurator.scaleMode(),
                                configurator
                        ));
//                        Logger.t(TAG).log("createThumbnailPatchTask:" + key.toString());
                    }
                    count++;
                    if (count - originate >= patchCount) return;//完结
                    landscapeSize += thumbnailPatchSize;
                }
                portraitSize += thumbnailPatchSize;
            }
        } else {
            for (int portraitSize = (((int) thumbnailPortraitSize / thumbnailPatchSize) + 1) * thumbnailPatchSize; portraitSize > 0; ) {
                for (int landscapeSize = (((int) thumbnailLandscapeSize / thumbnailPatchSize) + 1) * thumbnailPatchSize; landscapeSize > 0; ) {
                    int right = Math.min(landscapeSize, thumbnailLandscapeSize);
                    int bottom = Math.min(portraitSize, thumbnailPortraitSize);

                    if (count >= originate) {
                        PatchKey key = new PatchKey(
                                pageNumber,
                                landscapeSize - thumbnailPatchSize,
                                portraitSize - thumbnailPatchSize,
                                right,
                                bottom,
                                configurator.thumbnailScale()
                        );
                        thumbnailPool.push(new PatchTask(
                                key,
                                configurator.pageSize(),
                                configurator.scaleMode(),
                                configurator
                        ));
//                        Logger.t(TAG).log("createThumbnailPatchTask:" + key.toString());
                    }
                    count++;
                    if (count - originate >= patchCount) return;//完结
                    landscapeSize -= thumbnailPatchSize;
                }
                portraitSize -= thumbnailPatchSize;
            }
        }
        //能运行到这里说明 还没有达到预期加载数量
        if (!reverse) {//正序
            if (pageNumber + 1 < configurator.core().pageCount()) {
                createThumbnailPatchTask(configurator, pageNumber+1, 0, patchCount - count, false);
            } else {
                Logger.t(TAG).log("页面超出正常范围：" + (pageNumber + 1));
            }
        } else {//逆序
            if (pageNumber - 1 >= 0) {
                createThumbnailPatchTask(configurator, pageNumber-1, 0, patchCount - count, true);
            } else {
                Logger.t(TAG).log("页面超出正常范围：" + (pageNumber - 1));
            }
        }
    }

    public static void createThumbnailPatchTask(Configurator configurator, PatchKey originate, int patchCount, boolean reverse) {
        int pageNumber = originate.page;
        SizeF pdfSize = configurator.pageCache().getPage(pageNumber).size;
        ThumbnailPool thumbnailPool = configurator.thumbnailPool();
        int thumbnailPatchSize = configurator.thumbnailPatchSize();
        int thumbnailLandscapeSize = configurator.thumbnailLandscapeSize();
        int thumbnailPortraitSize = (int) (pdfSize.height / pdfSize.width * thumbnailLandscapeSize);
        int count = 0;
        if (!reverse) { //正序
            for (int portraitSize = 0; portraitSize < thumbnailPortraitSize; ) {
                for (int landscapeSize = 0; landscapeSize < thumbnailLandscapeSize; ) {
                    int right = landscapeSize + (thumbnailPatchSize + landscapeSize > thumbnailLandscapeSize ? thumbnailLandscapeSize - landscapeSize : thumbnailPatchSize);
                    int bottom = portraitSize + (thumbnailPatchSize + portraitSize > thumbnailPortraitSize ? thumbnailPortraitSize - portraitSize : thumbnailPatchSize);
                    PatchKey key = new PatchKey(
                            pageNumber,
                            landscapeSize,
                            portraitSize,
                            right,
                            bottom,
                            configurator.thumbnailScale()
                    );
                    if (key.compareTo(originate) > 0) {
                        thumbnailPool.push(new PatchTask(
                                key,
                                configurator.pageSize(),
                                configurator.scaleMode(),
                                configurator
                        ));
//                        Logger.t(TAG).log("createThumbnailPatchTask:" + key.toString());
                        count++;
                    }
                    if (count >= patchCount) return;//完结
                    landscapeSize += thumbnailPatchSize;
                }
                portraitSize += thumbnailPatchSize;
            }
        } else { //逆序
            for (int portraitSize = (((int) thumbnailPortraitSize / thumbnailPatchSize) + 1) * thumbnailPatchSize; portraitSize > 0; ) {
                for (int landscapeSize = (((int) thumbnailLandscapeSize / thumbnailPatchSize) + 1) * thumbnailPatchSize; landscapeSize > 0; ) {
                    int right = Math.min(landscapeSize, thumbnailLandscapeSize);
                    int bottom = Math.min(portraitSize, thumbnailPortraitSize);
                    PatchKey key = new PatchKey(
                            pageNumber,
                            landscapeSize - thumbnailPatchSize,
                            portraitSize - thumbnailPatchSize,
                            right,
                            bottom,
                            configurator.thumbnailScale()
                    );
                    if (key.compareTo(originate) < 0) {
                        thumbnailPool.push(new PatchTask(
                                key,
                                configurator.pageSize(),
                                configurator.scaleMode(),
                                configurator
                        ));
//                        Logger.t(TAG).log("createThumbnailPatchTask:" + key.toString());
                        count++;
                    }
                    if (count >= patchCount) return;//完结
                    landscapeSize -= thumbnailPatchSize;
                }
                portraitSize -= thumbnailPatchSize;
            }
        }
        //能运行到这里说明 还没有达到预期加载数量
        if (!reverse) {//正序
            if (pageNumber + 1 < configurator.core().pageCount()) {
                createThumbnailPatchTask(configurator, pageNumber + 1 , 0, patchCount - count, false);
            } else {
                Logger.t(TAG).log("PatchKey页面超出正常范围：" + (pageNumber + 1));
            }
        } else {//逆序
            if (pageNumber - 1 >= 0) {
                createThumbnailPatchTask(configurator, pageNumber-1, 0, patchCount - count, true);
            } else {
                Logger.t(TAG).log("PatchKey页面超出正常范围：" + (pageNumber - 1));
            }
        }
    }
}
